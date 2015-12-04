package com.github.aesteve.vertx.groovy.specs

import io.vertx.core.http.HttpHeaders
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
	}
	
	@Test
	void testGetAt(TestContext context) {
		Async async = context.async()
		HttpClientRequest req = client["$PATH/header"]
		req >> { response -> 
			response >>> {
				context.assertEquals it.toString('UTF-8'), VAL
				async.complete()
			}
		}
		req.putHeader(HEADER, VAL)++
	}
	
	@Test
	void testParams(TestContext context) {
		Async async = context.async()
		client.getNow "$PATH/param?$PARAM=$VAL", { response ->
			response.bodyHandler {
				context.assertEquals it.toString('UTF-8'), VAL
				async.complete()
			}
		}
	}
}
