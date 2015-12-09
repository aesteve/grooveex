package com.github.aesteve.vertx.groovy.specs

import io.vertx.core.http.HttpHeaders
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.HttpClientRequest
import io.vertx.groovy.ext.unit.Async
import io.vertx.groovy.ext.unit.TestContext
import io.vertx.groovy.ext.web.Router
import org.junit.Test

class ServerRequestSpec extends TestBase {
	
	final static String HEADER = HttpHeaders.CONTENT_TYPE
	final static String PARAM = 'dog'
	final static String VAL = 'Snoopy'
	final static String PATH = '/serverrequesttest'
	
	@Override
	void router() {
		router = Router.router vertx
		router["$PATH/header"] = { it.response << it.request.headers[HEADER] }
		router["$PATH/param"] = { it.response << it.request.params[PARAM] }
		router.post("$PATH/body") >> { ctx ->
			ctx.request >>> { ctx.response << it }
		}
		router.post("$PATH/pump") >> { ctx ->
			ctx.response.chunked = true
			(ctx.request | ctx.response)++
		}
	}
	
	@Test
	void testGetAt(TestContext context) {
		context.async { async ->
			HttpClientRequest req = client["$PATH/header"]
			req >> { response -> 
				response >>> {
					assertEquals it as String, VAL
					async++
				}
			}
			req.putHeader(HEADER, VAL)++
		}
	}
	
	@Test
	void testParams(TestContext context) {
		context.async { async ->
			client.getNow "$PATH/param?$PARAM=$VAL", { response ->
				response >>> {
					assertEquals it as String, VAL
					async++
				}
			}
		}
	}
	
	@Test
	void testPost(TestContext context) {
		context.async { async ->
			Buffer buff = VAL as Buffer
			HttpClientRequest post = client.post "$PATH/body", { response ->
				response >>> {
					assertEquals it as String, VAL
					async++
				}
			}
			post << buff
		}
	}
	
	@Test
	void testPump(TestContext context) {
		context.async { async ->
			Buffer buff = VAL as Buffer
			Buffer received = Buffer.buffer()
			HttpClientRequest post = client.post "$PATH/pump", { response ->
				response >> {
					received << it
					if (received.length() == buff.length()) {
						assertEquals it as String, VAL
						async++
					}
				}
			}
			post << buff
		}
	}
}
