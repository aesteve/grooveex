package com.github.aesteve.vertx.groovy.specs.extensions

import com.github.aesteve.vertx.groovy.builder.RouterBuilder
import com.github.aesteve.vertx.groovy.specs.TestBase
import groovy.time.TimeDuration
import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

class ThrottledSpec2 extends TestBase {

	@Override
	void router() {
		Binding binding = new Binding()
		binding.setProperty('hour', new TimeDuration(1, 0, 0, 0))
		router = RouterBuilder.buildRouter(binding, vertx, new File('src/test/resources/throttledWithBinding.groovy'))
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
