package com.github.aesteve.vertx.groovy.specs

import io.vertx.groovy.ext.unit.Async
import io.vertx.groovy.ext.unit.TestContext
import io.vertx.groovy.ext.web.Router
import org.junit.Test

class RoutingContextSpec extends TestBase {
	
	final static String KEY = 'dog'
	final static String VAL = 'Snoopy'
	final static String PATH = '/routingcontexttest'
	
	@Override
	void router() {
		router = Router.router vertx
		router[PATH] = {
			it[KEY] = VAL
			it++
		}
		router[PATH] = {
			it.response().end it[KEY]
		}
	}
	
	@Test
	void testGetPutAndCall(TestContext context) {
		Async async = context.async()
		client.getNow PATH, { response -> 
			response.bodyHandler {
				context.assertEquals it as String, VAL
				async.complete()
			}
		}
	}
}
