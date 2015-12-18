package com.github.aesteve.vertx.groovy.specs.builder

import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

class SubRouterTest extends BuilderTestBase {
	@Test
	void testGetHandler(TestContext context) {
		context.async { async ->
			client.getNow("/sub/firstSubRoute", { response ->
				assertEquals 200, response.statusCode
				response >>> { buffer ->
					assertEquals buffer as String, "firstSubRoute"
					async++
				}
			})
		}
	}

	@Test
	void testSubRoute(TestContext context) {
		context.async { async ->
			client.getNow("/sub/secondSubRoute", { response ->
				assertEquals 200, response.statusCode
				response >>> { buffer ->
					assertEquals buffer as String, "secondSubRoute"
					async++
				}
			})
		}
	}
}
