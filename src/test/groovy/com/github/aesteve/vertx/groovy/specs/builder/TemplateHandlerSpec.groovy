package com.github.aesteve.vertx.groovy.specs.builder

import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

class TemplateHandlerSpec extends BuilderTestBase {
	@Test
	void testTemplateEngine(TestContext context) {
		String name = 'Snoopy'
		context.async { async ->
			client.getNow "/handlebars/hello.hbs?name=$name", { response ->
				assertEquals 200, response.statusCode
				response >>> {
					assertEquals it as String, "Hello $name"
					async++
				}
			}
		}
	}
}
