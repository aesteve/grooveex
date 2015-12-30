package com.github.aesteve.vertx.groovy.specs.extensions

import com.github.aesteve.vertx.groovy.builder.RouterBuilder
import com.github.aesteve.vertx.groovy.specs.TestBase
import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

class ExtensionsOrderSpec extends TestBase {

	@Override
	void router() {
		router = RouterBuilder.buildRouter(vertx, new File('src/test/resources/extensions-order.groovy'))
	}


	@Test
	void testDateHeadersExtension(TestContext context) {
		context.async { async ->
			client.getNow '/extensions/with/order', { response ->
				assertEquals 200, response.statusCode
				def before = response.headers['X-Date-Before']
				assertNotNull before
				before = Long.valueOf before
				def after = response.headers['X-Date-After']
				assertNotNull after
				after = Long.valueOf after
				assertTrue(before + 400 < after)
				async++
			}
		}
	}
}
