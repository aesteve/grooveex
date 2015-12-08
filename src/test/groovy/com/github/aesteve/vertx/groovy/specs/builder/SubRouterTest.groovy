package com.github.aesteve.vertx.groovy.specs.builder

import io.vertx.groovy.ext.unit.Async
import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

class SubRouterTest extends BuilderTestBase {
    @Test
    public void testGetHandler(TestContext context) {
        Async async = context.async()
        client.getNow("/sub/firstSubRoute", { response ->
            context.assertEquals 200, response.statusCode()
            response >>> { buffer ->
                context.assertEquals buffer as String, "firstSubRoute"
                async.complete()
            }
        })
    }
}
