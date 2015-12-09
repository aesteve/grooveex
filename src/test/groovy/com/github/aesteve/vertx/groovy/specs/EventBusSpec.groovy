package com.github.aesteve.vertx.groovy.specs

import io.vertx.groovy.ext.unit.TestContext
import io.vertx.groovy.core.eventbus.EventBus
import io.vertx.groovy.ext.unit.Async
import io.vertx.groovy.ext.web.Router
import org.junit.Test

class EventBusSpec extends TestBase {

	private final static String ADDR = 'some-address'
	
	@Override
	public void router() {
		router = Router.router(vertx)
	}
	
	@Test
	public void testEventBusPublish(TestContext context) {
		context.async { async ->
			EventBus eb = vertx.eventBus
			String msg = "ping"
			eb[ADDR] >> {
				assertEquals it.address, ADDR
				assertEquals it.body, msg
				async++
			}
			eb[ADDR] << msg
		}
	}
	
	@Test
	public void testEventBusSend(TestContext context) {
		context.async { async ->
			EventBus eb = vertx.eventBus
			String msg = "ping"
			eb[ADDR] >> {
				assertEquals it.address, ADDR
				assertEquals it.body, msg
				it << "pong"
				async++
			}
			eb[ADDR] << msg
		}
	}

}
