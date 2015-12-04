package com.github.aesteve.vertx.groovy.specs

import io.vertx.core.http.HttpMethod
import io.vertx.groovy.core.Vertx
import io.vertx.groovy.core.http.HttpClient
import io.vertx.groovy.core.http.HttpServer
import io.vertx.groovy.ext.web.Router
import io.vertx.groovy.ext.web.handler.sockjs.SockJSHandler
import io.vertx.groovy.ext.web.handler.sockjs.SockJSSocket
import org.junit.After
import org.junit.Before

import org.junit.runner.RunWith
import io.vertx.groovy.ext.unit.junit.VertxUnitRunner

@RunWith(VertxUnitRunner.class)
abstract class TestBase {
	
	public final int PORT = 9090
	public final String HOST = 'localhost'
	public final Map serverOptions = [port: PORT, host: HOST]
	public final Map clientOptions = [defaultPort: PORT, defaultHost: HOST]
	
	protected Vertx vertx
	protected HttpServer server
	protected Router router
	
	@Before
	public void setUpServer() {
		vertx = Vertx.vertx()
		server = vertx.createHttpServer(serverOptions)
		router = Router.router vertx
		SockJSHandler sockHandler = SockJSHandler.create(vertx, [:])
		sockHandler.socketHandler { SockJSSocket sock ->
			sock >> {
				sock << it
			} 
		}
		router['/test'] = {
			it.response().end "test"
		}
		router['/sock/*'] >> sockHandler
		server.requestHandler(router.&accept).listen()
	}
	
	@After
	public void tearDown() {
		server.close()
	}
	
	HttpClient client() {
		vertx.createHttpClient clientOptions
	}
}
