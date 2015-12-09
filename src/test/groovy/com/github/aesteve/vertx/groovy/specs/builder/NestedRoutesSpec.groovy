package com.github.aesteve.vertx.groovy.specs.builder

import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.HttpClientRequest
import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

class NestedRoutesSpec extends BuilderTestBase {

	@Test
	public void testSubRoutes(TestContext context) {
		context.async().with {
			HttpClientRequest req = client['/sugar']
			req >> { response ->
				context.assertEquals 200, response.statusCode
				response >>> { buff ->
					context.assertEquals buff as String, 'Yes please !'
					complete()
				}
			}
			req++
		} 
	}
	
	@Test
	public void testSubRoutes2(TestContext context) {
		Buffer received = Buffer.buffer()
		context.async().with {
			HttpClientRequest req = client['/sugar/sex/magic']
			req >> { response ->
				context.assertEquals 200, response.statusCode
				response >> { buff ->
					received += buff
				}
				response.endHandler {
					context.assertEquals received as String, 'Is the city I live in......The city of Angels - RHCP (1991)'
					complete()
				}
			}
			req++
		}
	}
}
