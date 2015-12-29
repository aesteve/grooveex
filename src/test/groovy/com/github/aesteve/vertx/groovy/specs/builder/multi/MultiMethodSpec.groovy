package com.github.aesteve.vertx.groovy.specs.builder.multi

import com.github.aesteve.vertx.groovy.builder.RouterBuilder
import com.github.aesteve.vertx.groovy.specs.TestBase
import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

class MultiMethodSpec extends TestBase {
	@Override
	void router() {
		router = RouterBuilder.buildRouter(vertx, new File('src/test/resources/multi-method.groovy'))
	}

	@Test
	void testGetWithMulti(TestContext context) {
		context.async { async ->
			client.getNow('/multimethods') { response ->
				assertEquals 200, response.statusCode
				response >>> {
					assertEquals 'get | post | options', it as String
					async++
				}
			}
		}
	}

	@Test
	void testPostWithMulti(TestContext context) {
		context.async { async ->
			def req = client.post('/multimethods') { response ->
				assertEquals 200, response.statusCode
				response >>> {
					assertEquals 'get | post | options', it as String
					async++
				}
			}
			req++
		}
	}

	@Test
	void testPutWithMulti(TestContext context) {
		context.async { async ->
			def req = client.put('/multimethods') { response ->
				assertEquals 200, response.statusCode
				response >>> {
					assertEquals 'put', it as String
					async++
				}
			}
			req++
		}
	}

	@Test
	void testOptionsWithMulti(TestContext context) {
		context.async { async ->
			def req = client.options('/multimethods') { response ->
				assertEquals 200, response.statusCode
				response >>> {
					assertEquals 'get | post | options', it as String
					async++
				}
			}
			req++
		}
	}
}
