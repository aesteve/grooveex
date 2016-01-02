package com.github.aesteve.vertx.groovy

import groovy.json.JsonBuilder
import groovy.transform.TypeChecked
import io.vertx.groovy.core.MultiMap
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.HttpServerResponse

@TypeChecked
class HttpServerResponseExtension {

	static MultiMap getHeaders(HttpServerResponse self) {
		self.headers()
	}

	static void next(HttpServerResponse self) {
		self.end()
	}

	static HttpServerResponse leftShift(HttpServerResponse self, JsonBuilder json) {
		self.end(json as Buffer)
		self
	}

	/** Inspired by finatra */
	/* Information status */

	static HttpServerResponse getContinue(HttpServerResponse self) {
		self.setStatusCode 100
		self.setStatusMessage "Continue"
	}

	static HttpServerResponse getSwitchingProtocols(HttpServerResponse self) {
		self.setStatusCode 101
		self.setStatusMessage "Switching Protocols"
	}

	static void switchingProtocols(HttpServerResponse self) {
		self.setStatusCode 101
		self.setStatusMessage "Switching Protocols"
		self.end()
	}

	/* Success status */

	static HttpServerResponse getOk(HttpServerResponse self) {
		self.setStatusCode 200
		self.setStatusMessage "OK"
	}

	static void ok(HttpServerResponse self) {
		self.setStatusCode 200
		self.setStatusMessage "OK"
		self.end()
	}

	static HttpServerResponse getCreated(HttpServerResponse self) {
		self.setStatusCode 201
		self.setStatusMessage "Created"
	}

	static void created(HttpServerResponse self) {
		self.setStatusCode 201
		self.setStatusMessage "Created"
		self.end()
	}

	static HttpServerResponse getAccepted(HttpServerResponse self) {
		self.setStatusCode 202
		self.setStatusMessage "Accepted"
	}

	static void accepted(HttpServerResponse self) {
		self.setStatusCode 202
		self.setStatusMessage "Accepted"
		self.end()
	}

	static HttpServerResponse getNoContent(HttpServerResponse self) {
		self.setStatusCode 204
		self.setStatusMessage "No Content"
	}

	static void noContent(HttpServerResponse self) {
		self.setStatusCode 204
		self.setStatusMessage "No Content"
		self.end()
	}

	static HttpServerResponse getResetContent(HttpServerResponse self) {
		self.setStatusCode 205
		self.setStatusMessage "Reset Content"
	}

	static void resetContent(HttpServerResponse self) {
		self.setStatusCode 205
		self.setStatusMessage "Reset Content"
		self.end()
	}

	static HttpServerResponse getPartialContent(HttpServerResponse self) {
		self.setStatusCode 206
		self.setStatusMessage "Partial Content"
	}

	static void partialContent(HttpServerResponse self) {
		self.setStatusCode 206
		self.setStatusMessage "Partial Content"
		self.end()
	}

	/* Redirect status */

	static HttpServerResponse getMultipleChoices(HttpServerResponse self) {
		self.setStatusCode 300
		self.setStatusMessage "Multiple Choices"
	}

	static void multipleChoices(HttpServerResponse self) {
		self.setStatusCode 300
		self.setStatusMessage "Multiple Choices"
		self.end()
	}

	static HttpServerResponse getMovedPermanently(HttpServerResponse self) {
		self.setStatusCode 301
		self.setStatusMessage "Moved Permanently"
	}

	static void movedPermanently(HttpServerResponse self) {
		self.setStatusCode 301
		self.setStatusMessage "Moved Permanently"
		self.end()
	}

	static HttpServerResponse getMovedTemporarily(HttpServerResponse self) {
		self.setStatusCode 302
		self.setStatusMessage "Moved Temporarily"
	}

	static void movedTemporarily(HttpServerResponse self) {
		self.setStatusCode 302
		self.setStatusMessage "Moved Temporarily"
		self.end()
	}


	static HttpServerResponse getSeeOther(HttpServerResponse self) {
		self.setStatusCode 303
		self.setStatusMessage "See Other"
	}

	static void seeOther(HttpServerResponse self) {
		self.setStatusCode 303
		self.setStatusMessage "See Other"
		self.end()
	}

	static HttpServerResponse getNotModified(HttpServerResponse self) {
		self.setStatusCode 304
		self.setStatusMessage "Not Modified"
	}

	static void notModified(HttpServerResponse self) {
		self.setStatusCode 304
		self.setStatusMessage "Not Modified"
		self.end()
	}

	static HttpServerResponse getUseProxy(HttpServerResponse self) {
		self.setStatusCode 305
		self.setStatusMessage "Use Proxy"
	}

	static void useProxy(HttpServerResponse self) {
		self.setStatusCode 305
		self.setStatusMessage "Use Proxy"
		self.end()
	}

	static HttpServerResponse getTemporaryRedirect(HttpServerResponse self) {
		self.setStatusCode 307
		self.setStatusMessage "Temporary Redirect"
	}

	static void temporaryRedirect(HttpServerResponse self) {
		self.setStatusCode 307
		self.setStatusMessage "Temporary Redirect"
		self.end()
	}

	static HttpServerResponse getPermanentRedirect(HttpServerResponse self) {
		self.setStatusCode 308
		self.setStatusMessage "Permanent Redirect"
	}

	static void permanentRedirect(HttpServerResponse self) {
		self.setStatusCode 308
		self.setStatusMessage "Permanent Redirect"
		self.end()
	}

	static HttpServerResponse getTooManyRedirects(HttpServerResponse self) {
		self.setStatusCode 310
		self.setStatusMessage "Too many Redirects"
	}

	static void tooManyRedirects(HttpServerResponse self) {
		self.setStatusCode 310
		self.setStatusMessage "Too many Redirects"
		self.end()
	}

	/* NB : client/server errors should be sent using context.fail(status) */
}
