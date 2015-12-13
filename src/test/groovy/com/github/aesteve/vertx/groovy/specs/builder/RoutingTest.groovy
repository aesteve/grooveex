package com.github.aesteve.vertx.groovy.specs.builder

import groovy.json.JsonBuilder
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.HttpClientRequest
import io.vertx.groovy.core.http.HttpClientResponse
import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

import static io.vertx.core.http.HttpHeaders.ACCEPT
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE

public class RoutingTest extends BuilderTestBase {

    @Test
    public void testSimpleGet(TestContext context) {
        context.async { async ->
            HttpClientRequest req = client['/simpleGet']
            req >> { response ->
                response >>> {
                    assertEquals it as String, 'Simple GET'
                    async++
                }
            }
            req++
        }
    }

    @Test
    public void testGetHandler(TestContext context) {
        context.async { async ->
            String expected = new JsonBuilder([result: "GET"]).toString()
            HttpClientRequest req = client["/handlers"]
            req >> { HttpClientResponse response ->
                assertEquals response.statusCode, 200
                response >>> { Buffer buffer ->
                    assertEquals buffer as String, expected
                    async++
                }
            }
            req.headers[ACCEPT] = "application/json"
            req.headers[CONTENT_TYPE] = "application/json"
            req++
        }
    }

    @Test
    public void testWrongContentType(TestContext context) {
        context.async { async ->
            HttpClientRequest req = client["/handlers"]
            req >> { HttpClientResponse response ->
                assertEquals 404, response.statusCode
                async++
            }
            req.headers[ACCEPT] = "application/xml"
            req.headers[CONTENT_TYPE] = "application/xml"
            req++
        }
    }

    @Test
    public void testPostHandler(TestContext context) {
        context.async { async ->
            JsonBuilder payload = new JsonBuilder([someKey: 'someValue'])
            HttpClientRequest req = client.post "/handlers"
            req >> { response ->
                assertEquals 200, response.statusCode
                response >> { Buffer buffer ->
                    assertEquals buffer as String, payload as String
                    async++
                }
            }
            req.headers[ACCEPT] = "application/json"
            req.headers[CONTENT_TYPE] = "application/json"
            req << payload
        }
    }

    @Test
    public void testGetStatic(TestContext context) {
        context.async { async ->
            JsonBuilder result = new JsonBuilder([result: "closure"])
            HttpClientRequest req = client["/staticClosure"]
            req >> { response ->
                assertEquals 200, response.statusCode
                response >>> { buffer ->
                    context.assertEquals buffer as String, result as String
                    async++
                }
            }
            req++
        }
    }

    @Test
    public void testBlocking(TestContext context) {
        context.async { async ->
            HttpClientRequest req = client['/blocking']
            req >> { response ->
                assertEquals 200, response.statusCode
                response >>> { buffer ->
                    assertEquals buffer as String, 'done !'
                    async++
                }
            }
            req++
        }
    }
}
