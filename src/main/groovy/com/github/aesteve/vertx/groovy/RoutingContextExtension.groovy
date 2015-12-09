package com.github.aesteve.vertx.groovy

import io.vertx.core.AsyncResult
import io.vertx.core.Handler

import java.util.Set
import groovy.transform.TypeChecked 
import io.vertx.groovy.core.Vertx 
import io.vertx.groovy.core.http.HttpServerRequest 
import io.vertx.groovy.core.http.HttpServerResponse 
import io.vertx.groovy.ext.auth.User 
import io.vertx.groovy.ext.web.Cookie 
import io.vertx.groovy.ext.web.RoutingContext 
import io.vertx.groovy.ext.web.Session

@TypeChecked
class RoutingContextExtension {

	static RoutingContext putAt(RoutingContext self, String key, Object obj) {
		self.put key, obj
	}

	static Object getAt(RoutingContext self, String path) {
		self.get path
	}

	static HttpServerRequest getRequest(RoutingContext self) {
		self.request()
	}

	static HttpServerResponse getResponse(RoutingContext self) {
		self.response()
	}

	static User getUser(RoutingContext self) {
		self.user()
	}

	static Session getSession(RoutingContext self) {
		self.session()
	}

	static Set<Cookie> getCookies(RoutingContext self) {
		self.cookies()
	}

	static Vertx getVertx(RoutingContext self) {
		self.vertx()
	}

	static int getStatusCode(RoutingContext self) {
		self.statusCode()
	}

	static String getMountPoint(RoutingContext self) {
		self.mountPoint()
	}

	static String getNormalisedPath(RoutingContext self) {
		self.normalisedPath()
	}
	
	static RoutingContext minus(RoutingContext self, Throwable cause) {
		self.fail cause
		self
	} 

	static RoutingContext minus(RoutingContext self, int status) {
		self.fail status
		self
	}

	static<T> Handler<AsyncResult<T>> rightShift(RoutingContext self, Closure clos) {
		return { AsyncResult<T> res ->
			if (res.failed()) {
				self.fail res.cause()
			} else {
				clos.delegate = self
				clos res.result()
			}
		} as Handler
	}
	
}
