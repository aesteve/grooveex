package com.github.aesteve.vertx.groovy.specs.builder

import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.HttpClientRequest
import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

class NestedRoutesSpec extends BuilderTestBase {

    @Test
    public void testSubRoutes(TestContext context) {
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
    public void testSubRoutes2(TestContext context) {
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
}
