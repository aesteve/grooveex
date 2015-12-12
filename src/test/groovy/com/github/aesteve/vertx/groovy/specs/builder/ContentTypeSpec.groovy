package com.github.aesteve.vertx.groovy.specs.builder

import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.HttpClientRequest
import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

import static io.vertx.core.http.HttpHeaders.*

class ContentTypeSpec extends BuilderTestBase {

    @Test
    public void testProducesConsumes(TestContext context) {
		context.async { async ->
	        HttpClientRequest req = client['/json/pure']
	        req >> { response ->
	            assertEquals 200, response.statusCode
	            response >> { Buffer buffer ->
	                assertEquals buffer as String, 'json'
	                async++
	            }
	        }
			req[ACCEPT] = 'application/json'
			req[CONTENT_TYPE] = 'application/json'
	        req++
		}
    }

	@Test
	public void testNotProducesConsumes(TestContext context) {
		context.async { async ->
			HttpClientRequest req = client['/json/pure']
			req >> { response ->
				assertEquals 404, response.statusCode
				async++
			}
			req[ACCEPT] = 'text/plain'
			req[CONTENT_TYPE] = 'text/plain'
			req++
		}
	}

	@Test
	public void testSubProducesConsumes(TestContext context) {
		context.async { async ->
			HttpClientRequest req = client['/json/plain']
			req >> { response ->
				assertEquals 200, response.statusCode
				response >>> { Buffer buffer ->
					assertEquals buffer as String, 'json|plain'
					async++
				}
			}
			req[ACCEPT] = 'application/json'
			req[CONTENT_TYPE] = 'application/json'
			req++
		}
	}

	@Test
	public void testSubProducesConsumesPlain(TestContext context) {
		context.async { async ->
			HttpClientRequest req = client['/json/plain']
			req >> { response ->
				assertEquals 200, response.statusCode
				response >>> { Buffer buffer ->
					assertEquals buffer as String, 'json|plain'
					async++
				}
			}
			req[ACCEPT] = 'text/plain'
			req[CONTENT_TYPE] = 'text/plain'
			req++
		}
	}

	@Test
	public void testSubNotProducesConsumes(TestContext context) {
		context.async { async ->
			HttpClientRequest req = client['/json/plain']
			req >> { response ->
				assertEquals 404, response.statusCode
				async++
			}
			req[ACCEPT] = 'application/xml'
			req[CONTENT_TYPE] = 'application/xml'
			req++
		}
	}

}
