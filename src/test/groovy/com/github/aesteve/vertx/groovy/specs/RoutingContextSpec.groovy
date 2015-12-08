package com.github.aesteve.vertx.groovy.specs

import io.vertx.groovy.ext.unit.Async
import io.vertx.groovy.ext.unit.TestContext
import io.vertx.groovy.ext.web.Router
import org.junit.Test

class RoutingContextSpec extends TestBase {
	
	final static String KEY = 'dog'
	final static String VAL = 'Snoopy'
	final static String PATH = '/routingcontexttest'
	final static String FAILED_STATUS = '/failWithStatus'
	final static String FAILED_THROW = '/failWithThrowable'
	final static int STATUS = 400
	final static Throwable ex = new RuntimeException("ex")
	
	@Override
	void router() {
		router = Router.router vertx
		router[PATH] = {
			it[KEY] = VAL
			it++
		}
		router[PATH] = {
			it.response << it[KEY]
		}
		router[FAILED_STATUS] = {
			it -= STATUS
		}
		router[FAILED_THROW] = {
			it -= ex
		}
	}
	
	@Test
	void testGetPutAndCall(TestContext context) {
		Async async = context.async()
		client.getNow PATH, { response -> 
			response >>> {
				context.assertEquals it as String, VAL
				async.complete()
			}
		}
	}
	
	@Test
	void testFailWithStatus(TestContext context) {
		Async async = context.async()
		client.getNow FAILED_STATUS, { response ->
			context.assertEquals response.statusCode, STATUS
			async.complete()
		}
	}

	@Test
	void testFailWithThrowable(TestContext context) {
		Async async = context.async()
		client.getNow FAILED_THROW, { response ->
			context.assertEquals response.statusCode, 500
			async.complete()
		}
	}
}
