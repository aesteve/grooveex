package com.github.aesteve.vertx.groovy.specs

import io.vertx.groovy.core.Vertx
import io.vertx.groovy.core.http.HttpServer
import io.vertx.groovy.ext.unit.TestContext
import io.vertx.groovy.ext.unit.junit.VertxUnitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(VertxUnitRunner)
class ServerResponseSpec {

	Vertx vertx = Vertx.vertx()

	@Test
	void test200(TestContext context) {
		testStatusAndMessage(context, "ok", 200, "OK")
	}

	@Test
	void test200Invoke(TestContext context) {
		testStatusAndMessage(context, "ok", 200, "OK", true)
	}

	@Test
	void test201(TestContext context) {
		testStatusAndMessage(context, "created", 201, "Created")
	}

	@Test
	void test201Invoke(TestContext context) {
		testStatusAndMessage(context, "created", 201, "Created", true)
	}

	@Test
	void test202(TestContext context) {
		testStatusAndMessage(context, "accepted", 202, "Accepted")
	}

	@Test
	void test202Invoke(TestContext context) {
		testStatusAndMessage(context, "accepted", 202, "Accepted", true)
	}

	@Test
	void test204(TestContext context) {
		testStatusAndMessage(context, "noContent", 204, "No Content")
	}

	@Test
	void test204Invoke(TestContext context) {
		testStatusAndMessage(context, "noContent", 204, "No Content", true)
	}

	@Test
	void test205(TestContext context) {
		testStatusAndMessage(context, "resetContent", 205, "Reset Content")
	}

	@Test
	void test205Invoke(TestContext context) {
		testStatusAndMessage(context, "resetContent", 205, "Reset Content", true)
	}

	@Test
	void test206(TestContext context) {
		testStatusAndMessage(context, "partialContent", 206, "Partial Content")
	}

	@Test
	void test206Invoke(TestContext context) {
		testStatusAndMessage(context, "partialContent", 206, "Partial Content", true)
	}

	@Test
	void test300(TestContext context) {
		testStatusAndMessage(context, "multipleChoices", 300, "Multiple Choices")
	}

	@Test
	void test300Invoke(TestContext context) {
		testStatusAndMessage(context, "multipleChoices", 300, "Multiple Choices", true)
	}

	@Test
	void test301(TestContext context) {
		testStatusAndMessage(context, "movedPermanently", 301, "Moved Permanently")
	}

	@Test
	void test301Invoke(TestContext context) {
		testStatusAndMessage(context, "movedPermanently", 301, "Moved Permanently", true)
	}

	@Test
	void test302(TestContext context) {
		testStatusAndMessage(context, "movedTemporarily", 302, "Moved Temporarily")
	}

	@Test
	void test302Invoke(TestContext context) {
		testStatusAndMessage(context, "movedTemporarily", 302, "Moved Temporarily", true)
	}

	@Test
	void test303(TestContext context) {
		testStatusAndMessage(context, "seeOther", 303, "See Other")
	}

	@Test
	void test303Invoke(TestContext context) {
		testStatusAndMessage(context, "seeOther", 303, "See Other", true)
	}

	@Test
	void test304(TestContext context) {
		testStatusAndMessage(context, "notModified", 304, "Not Modified")
	}

	@Test
	void test304Invoke(TestContext context) {
		testStatusAndMessage(context, "notModified", 304, "Not Modified", true)
	}

	@Test
	void test305(TestContext context) {
		testStatusAndMessage(context, "useProxy", 305, "Use Proxy")
	}

	@Test
	void test305Invoke(TestContext context) {
		testStatusAndMessage(context, "useProxy", 305, "Use Proxy", true)
	}

	@Test
	void test307(TestContext context) {
		testStatusAndMessage(context, "temporaryRedirect", 307, "Temporary Redirect")
	}

	@Test
	void test307Invoke(TestContext context) {
		testStatusAndMessage(context, "temporaryRedirect", 307, "Temporary Redirect", true)
	}

	@Test
	void test308(TestContext context) {
		testStatusAndMessage(context, "permanentRedirect", 308, "Permanent Redirect")
	}

	@Test
	void test308Invoke(TestContext context) {
		testStatusAndMessage(context, "permanentRedirect", 308, "Permanent Redirect", true)
	}

	@Test
	void test310(TestContext context) {
		testStatusAndMessage(context, "tooManyRedirects", 310, "Too many Redirects")
	}

	@Test
	void test310Invoke(TestContext context) {
		testStatusAndMessage(context, "tooManyRedirects", 310, "Too many Redirects", true)
	}

	private testStatusAndMessage(TestContext context, String method, int status, String msg, boolean invoke = false) {
		context.async { async ->
			HttpServer server = vertx.createHttpServer()
			server.requestHandler { req ->
				if (invoke) req.response()."$method"()
				else req.response()."$method".end()
			}
			server.listen(9999) {
				def client = vertx.createHttpClient([defaultHost: 'localhost', defaultPort: 9999])
				client.getNow('/') { response ->
					assertEquals status, response.statusCode
					assertEquals msg, response.statusMessage
					server.close {
						async++
					}
				}
			}
		}
	}
}
