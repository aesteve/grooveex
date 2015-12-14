package com.github.aesteve.vertx.groovy.specs.builder

import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

class ExtensionsSpec extends BuilderTestBase {
	@Test
	void testDateHeader(TestContext context) {
		context.async { async ->
			def req = client.getNow '/extensions/date', { response ->
				assertEquals response.statusCode, 200
				assertNotNull response.headers['X-MyCustomDate']
				response >>> {
					assertEquals it as String, 'check the "X-MyCustomDate" header :)'
					async++
				}
			}
		}
	}
}
