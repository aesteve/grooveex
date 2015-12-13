package com.github.aesteve.vertx.groovy.specs.builder

import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

class RegexSpec extends BuilderTestBase {
    @Test
    void testRegex(TestContext context) {
        context.async { async ->
            client.getNow '/regex/firstparam', { response ->
                assertEquals response.statusCode, 200
                response >>> {
                    assertEquals it as String, 'firstparam'
                    async++
                }
            }
        }
    }
}
