package com.github.aesteve.vertx.groovy.specs.builder

import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.HttpClientRequest
import io.vertx.groovy.ext.unit.Async
import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

import static io.vertx.core.http.HttpHeaders.*

class CORSTest extends BuilderTestBase {

    @Test
    public void testAllowOrigin(TestContext context) {
        Async async = context.async()
        HttpClientRequest req = client["/cors/test"]
        req >> { response ->
            context.assertEquals 200, response.statusCode()
            context.assertEquals "*", response.headers[ACCESS_CONTROL_ALLOW_ORIGIN]
            response >>> { Buffer buffer ->
                context.assertEquals(buffer as String, "CORS")
                async.complete()
            }
        }
        CharSequence s = ORIGIN
        req.headers[ORIGIN] = "vertx.io"
        req++
    }

}
