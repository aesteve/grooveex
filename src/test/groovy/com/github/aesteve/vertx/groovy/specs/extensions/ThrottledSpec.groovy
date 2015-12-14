package com.github.aesteve.vertx.groovy.specs.extensions

import com.github.aesteve.vertx.groovy.builder.RouterBuilder
import com.github.aesteve.vertx.groovy.specs.TestBase
import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

class ThrottledSpec extends TestBase {

    @Override
    void router() {
        router = RouterBuilder.buildRouter(vertx, new File('src/test/resources/throttled.groovy'))
    }

    @Test
    void testThrottled(TestContext context) {
        context.async { async ->
            10.times {
                client.getNow('/limited', { response ->
                    assertEquals 200, response.statusCode
                })
            }
            sleep(500)
            client.getNow('/limited', { response ->
                assertEquals 420, response.statusCode
                async++
            })
        }
    }
}
