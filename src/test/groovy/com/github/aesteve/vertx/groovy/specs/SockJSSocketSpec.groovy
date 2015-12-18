package com.github.aesteve.vertx.groovy.specs

import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.WebSocket
import io.vertx.groovy.ext.unit.TestContext
import io.vertx.groovy.ext.web.Router
import io.vertx.groovy.ext.web.handler.sockjs.SockJSHandler
import io.vertx.groovy.ext.web.handler.sockjs.SockJSSocket
import org.junit.Test

class SockJSSocketSpec extends TestBase {

	@Override
	void router() {
		router = Router.router vertx
		SockJSHandler sockHandler = SockJSHandler.create vertx, [:]
		sockHandler.socketHandler { SockJSSocket sock ->
			sock >> { sock += it }
		}
		router['/sock/*'] >> sockHandler
	}

	@Test
	public void testSocketSugar(TestContext context) {
		context.async { async ->
			Buffer buff = "test" as Buffer
			client.websocket "/sock/websocket", { WebSocket sock ->
				sock.handler {
					assertEquals buff, it
					async++
				}
				sock += buff
			}
		}
	}

	@Test
	public void testPipeSugar(TestContext context) {
		context.async { async ->
			String filePath = "src/test/resources/file.txt"
			Buffer fileBuff = vertx.fileSystem.readFileBlocking filePath
			Buffer buff = Buffer.buffer()
			client.websocket "/sock/websocket", { WebSocket sock ->
				sock >> {
					buff += it // buff << it
					if (buff.length() >= fileBuff.length()) {
						async++
					}
				}
				vertx.fileSystem.open filePath, [:], { (it.result() | sock)++ }
			}
		}
	}

}
