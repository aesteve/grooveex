package com.github.aesteve.vertx.groovy.specs.builder

import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.HttpClientRequest
import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

import static io.vertx.core.http.HttpHeaders.ACCEPT
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE

class NestedRoutesSpec extends BuilderTestBase {

	@Test
	void testSubRoutes(TestContext context) {
		context.async { async ->
			HttpClientRequest req = client['/sugar']
			req >> { response ->
				assertEquals 200, response.statusCode
				response >>> { buff ->
					assertEquals buff as String, 'Yes please !'
					async++
				}
			}
			req++
		}
	}

	@Test
	void testSubRoutes2(TestContext context) {
		context.async { async ->
			Buffer received = Buffer.buffer()
			HttpClientRequest req = client['/sugar/sex/magic']
			req >> { response ->
				assertEquals 200, response.statusCode
				response >> { received += it }
				response.endHandler {
					assertEquals received as String, 'Is the city I live in......The city of Angels - RHCP (1991)'
					async++
				}
			}
			req++
		}
	}

	@Test
	void testRootRoute(TestContext context) {
		context.async { async ->
			HttpClientRequest req = client['/withsubroute']
			req >> { response ->
				assertEquals 200, response.statusCode
				response >>> {
					assertEquals it as String, "get"
					async++
				}
			}
			req.headers[CONTENT_TYPE] = 'application/json'
			req.headers[ACCEPT] = 'application/json'
			req++
		}
	}

	@Test
	void testSubRoute(TestContext context) {
		String subPath = "something"
		context.async { async ->
			HttpClientRequest req = client["/withsubroute/$subPath"]
			req >> { response ->
				assertEquals 200, response.statusCode
				response >>> {
					assertEquals it as String, "get $subPath"
					async++
				}
			}
			req.headers[CONTENT_TYPE] = 'application/json'
			req.headers[ACCEPT] = 'application/json'
			req++
		}
	}
}
