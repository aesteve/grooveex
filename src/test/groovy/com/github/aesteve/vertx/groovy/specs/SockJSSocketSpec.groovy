package com.github.aesteve.vertx.groovy.specs

import groovy.transform.TypeChecked
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.WebSocket
import io.vertx.groovy.ext.unit.Async
import io.vertx.groovy.ext.unit.TestContext
import io.vertx.groovy.ext.web.handler.sockjs.SockJSHandler
import io.vertx.groovy.ext.web.handler.sockjs.SockJSSocket
import org.junit.Test

class SockJSSocketSpec extends TestBase {

	@Test
	public void testSocketSugar(TestContext context) {
		Async async = context.async()
		Buffer buff = Buffer.buffer "test"
		client().websocket "/sock/websocket", { WebSocket sock -> 
			sock.handler {
				context.assertEquals buff, it
				async.complete()
			}
			sock << buff
		}
	}
	
}
