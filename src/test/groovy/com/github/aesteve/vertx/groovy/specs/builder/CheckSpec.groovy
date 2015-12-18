package com.github.aesteve.vertx.groovy.specs.builder

import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.HttpClientRequest
import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

class CheckSpec extends BuilderTestBase {

	@Test
	void testCheckOK(TestContext context) {
		context.async { async ->
			HttpClientRequest req = client['/check?token=magic']
			req >> { response ->
				assertEquals 200, response.statusCode
				response >>> { Buffer buffer ->
					assertEquals buffer as String, "everything's fine"
					async++
				}
			}
			req++
		}
	}

	@Test
	void testCheckFail2(TestContext context) {
		context.async { async ->
			HttpClientRequest req = client['/check?token=something']
			req >> { response ->
				assertEquals 403, response.statusCode
				async++
			}
			req++
		}
	}

	@Test
	void testCheckFail(TestContext context) {
		context.async { async ->
			HttpClientRequest req = client['/check']
			req >> { response ->
				assertEquals 401, response.statusCode
				async++
			}
			req++
		}
	}

}
