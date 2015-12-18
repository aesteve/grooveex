package com.github.aesteve.vertx.groovy.specs

import io.vertx.groovy.core.Vertx
import io.vertx.groovy.core.http.HttpClient
import io.vertx.groovy.core.http.HttpServer
import io.vertx.groovy.ext.unit.TestContext
import io.vertx.groovy.ext.unit.junit.VertxUnitRunner
import io.vertx.groovy.ext.web.Router
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith

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
	public void setUpServer(TestContext context) {
		vertx = Vertx.vertx
		server = vertx.createHttpServer(serverOptions)
		router()
		server.requestHandler(router.&accept).listen context.asyncAssertSuccess()
	}

	@After
	public void tearDown(TestContext context) {
		server.close context.asyncAssertSuccess()
	}

	abstract void router()

	HttpClient getClient() {
		vertx.createHttpClient clientOptions
	}
}
