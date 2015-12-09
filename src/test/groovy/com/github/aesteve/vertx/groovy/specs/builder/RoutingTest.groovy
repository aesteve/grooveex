package com.github.aesteve.vertx.groovy.specs.builder

import groovy.json.JsonBuilder
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.HttpClientRequest
import io.vertx.groovy.core.http.HttpClientResponse
import io.vertx.groovy.ext.unit.Async
import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

import static io.vertx.core.http.HttpHeaders.ACCEPT
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE
import static org.junit.Assert.assertEquals

public class RoutingTest extends BuilderTestBase {

	@Test
	public void testSimpleGet(TestContext context) {
		context.async().with {
			HttpClientRequest req = client['/simpleGet']
			req >> { response ->
				response >>> {
					context.assertEquals it as String, 'Simple GET'
					complete()
				}
			}
			req++
		}
	}
	
    @Test
    public void testGetHandler(TestContext context) {
        Async async = context.async()
		String expected = new JsonBuilder([result: "GET"]).toString()
        HttpClientRequest req = client["/handlers"]
        req >> { HttpClientResponse response ->
            context.assertEquals response.statusCode, 200
            response >>> { Buffer buffer ->
                context.assertEquals buffer as String, expected
                async.complete()
            }
        }
        req.headers[ACCEPT] = "application/json"
        req.headers[CONTENT_TYPE] = "application/json"
        req++
    }

    @Test
    public void testWrongContentType(TestContext context) {
        Async async = context.async()
        HttpClientRequest req = client["/handlers"]
        req >> { HttpClientResponse response ->
            context.assertEquals 404, response.statusCode
            async.complete()
        }
        req.headers[ACCEPT] = "application/xml"
        req.headers[CONTENT_TYPE] = "application/xml"
        req++
    }

    @Test
    public void testPostHandler(TestContext context) {
        JsonBuilder payload = new JsonBuilder([someKey: 'someValue'])
        Async async = context.async()
        HttpClientRequest req = client.post "/handlers"
        req >> { response ->
            context.assertEquals 200, response.statusCode
            response >> { Buffer buffer ->
                context.assertEquals buffer as String, payload as String
                async.complete()
            }
        }
        req.headers[ACCEPT] =  "application/json"
        req.headers[CONTENT_TYPE] = "application/json"
        req << payload
    }

    @Test
    public void testGetStatic(TestContext context) {
        Async async = context.async()
		JsonBuilder result = new JsonBuilder([result: "closure"])
        HttpClientRequest req = client["/staticClosure"]
        req >> { response ->
            context.assertEquals 200, response.statusCode
            response >>> { buffer ->
                context.assertEquals buffer as String, result as String
                async.complete()
            }
        }
        req++
    }
	
	@Test
	public void testBlocking(TestContext context) {
		Async async = context.async()
		HttpClientRequest req = client['/blocking']
		req >> { response ->
			context.assertEquals 200, response.statusCode
			response >>> { buffer ->
				context.assertEquals buffer as String, 'done !'
				async.complete()
			}
		}
		req++
	}
}
