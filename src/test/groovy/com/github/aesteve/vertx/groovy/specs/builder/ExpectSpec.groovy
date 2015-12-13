package com.github.aesteve.vertx.groovy.specs.builder

import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.HttpClientRequest
import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

import static io.vertx.core.http.HttpHeaders.AUTHORIZATION

class ExpectSpec extends BuilderTestBase {

    @Test
    public void testCorrectRequest(TestContext context) {
        context.async { async ->
            HttpClientRequest req = client["/expect?long=123&exists=something"]
            req >> { response ->
                assertEquals 200, response.statusCode
                response >>> { Buffer buffer ->
                    assertEquals buffer as String, "everything's fine"
                    async++
                }
            }
            req.headers[AUTHORIZATION] = 'token MYTOKEN'
            req++
        }
    }

    @Test
    public void testMissingParam(TestContext context) {
        context.async { async ->
            HttpClientRequest req = client["/expect?long=123"]
            req >> { response ->
                assertEquals 400, response.statusCode
                async++
            }
            req.headers[AUTHORIZATION] = 'token MYTOKEN'
            req++
        }
    }

    @Test
    public void testWrongParamValue(TestContext context) {
        context.async { async ->
            HttpClientRequest req = client["/expect?long=string&exists=something"]
            req >> { response ->
                assertEquals 400, response.statusCode
                async++
            }
            req.headers[AUTHORIZATION] = 'token MYTOKEN'
            req++
        }
    }

    @Test
    public void testWrongHeader(TestContext context) {
        context.async { async ->
            HttpClientRequest req = client["/expect?long=string&exists=something"]
            req >> { response ->
                assertEquals 400, response.statusCode
                async++
            }
            req.headers[AUTHORIZATION] = 'invalidTokenFormat'
            req++
        }
    }
}
