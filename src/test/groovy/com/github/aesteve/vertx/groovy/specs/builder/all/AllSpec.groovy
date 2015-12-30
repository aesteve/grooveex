package com.github.aesteve.vertx.groovy.specs.builder.all

import com.github.aesteve.vertx.groovy.builder.RouterBuilder
import com.github.aesteve.vertx.groovy.specs.TestBase
import io.vertx.core.http.HttpMethod
import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

class AllSpec extends TestBase {
	@Override
	void router() {
		router = RouterBuilder.buildRouter(vertx, new File('src/test/resources/all-routes.groovy'))
	}

	@Test
	void testGetInline(TestContext context) {
		testRequest context, '/allInline', HttpMethod.GET
	}

	@Test
	void testPostInline(TestContext context) {
		testRequest context, '/allInline', HttpMethod.POST
	}

	@Test
	void testGetMulti(TestContext context) {
		testRequest context, '/allMulti', HttpMethod.GET
	}

	@Test
	void testPostMulti(TestContext context) {
		testRequest context, '/allMulti', HttpMethod.POST
	}

	private void testRequest(TestContext context, String path, HttpMethod method) {
		context.async { async ->
			def req = client.request(method, path) { response ->
				response >>> {
					def before = response.headers['X-Date-Before']
					def after = response.headers['X-Date-After']
					assertNotNull before
					assertNotNull after
					before = Long.valueOf before
					after = Long.valueOf after
					assertTrue after > before + 400
					async++
				}
			}
			req++
		}
	}
}
