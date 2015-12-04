package com.github.aesteve.vertx.groovy.specs

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
			it[KEY] = VALUE
			it.next()
		}
		router[PATH] = {
			it.response().end it[KEY]
		}
		router
	}
	
	@Test
	void testGetPut(TestContext context) {
		client().getNow PATH, { response -> 
			response.bodyHandler {
				context.assertEquals it.toString('UTF-8'), VAL
			}
		}
	}
}
