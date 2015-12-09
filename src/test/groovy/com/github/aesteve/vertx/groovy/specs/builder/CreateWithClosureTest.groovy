package com.github.aesteve.vertx.groovy.specs.builder

import com.github.aesteve.vertx.groovy.builder.RouterBuilder
import com.github.aesteve.vertx.groovy.specs.TestBase
import io.vertx.groovy.ext.unit.Async
import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

class CreateWithClosureTest extends TestBase {
    @Override
    void router() {
        router = RouterBuilder.buildRouter vertx, {
            get '/byClosure', {
                response << 'byClosure'
            }
        }
    }

    @Test
    void testByClosure(TestContext context) {
		context.async { async ->
	        def req = client['/byClosure']
	        req >> { response ->
	            assertEquals 200, response.statusCode()
	            response >>> { buff ->
	                assertEquals buff as String, 'byClosure'
	                async++
	            }
	        }
	        req++
		}
    }
}
