package com.github.aesteve.vertx.groovy

import com.github.aesteve.vertx.groovy.io.Marshaller
import com.github.aesteve.vertx.groovy.io.RequestPayload
import groovy.transform.TypeChecked
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.impl.Utils
import io.vertx.groovy.core.MultiMap
import io.vertx.groovy.core.Vertx
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.eventbus.EventBus
import io.vertx.groovy.core.http.HttpServerRequest
import io.vertx.groovy.core.http.HttpServerResponse
import io.vertx.groovy.ext.auth.User
import io.vertx.groovy.ext.web.Cookie
import io.vertx.groovy.ext.web.RoutingContext
import io.vertx.groovy.ext.web.Session

@TypeChecked
class RoutingContextExtension {

	private final static String PAYLOAD = 'ROUTING_CTX_PAYLOAD'
	private final static String MARSHALLERS = 'ROUTING_CTX_MARSHALLERS'
	private final static String CACHED_BODY = 'ROUTING_CTX_CACHED_BODY'
	private final static String USER_IN_CONTEXT = 'USER_WITHIN_ROUTING_CONTEXT'

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

	static User user(RoutingContext self) {
		User u = self.get(USER_IN_CONTEXT) as User
		if (u) return u
		new User(((io.vertx.ext.web.RoutingContext) self.getDelegate()).user())
	}

	static User getUser(RoutingContext self) {
		self.user()
	}

	static RoutingContext setUser(RoutingContext self, User user) {
		io.vertx.ext.web.RoutingContext javaContext = self.getDelegate() as io.vertx.ext.web.RoutingContext
		javaContext.setUser(user.getDelegate() as io.vertx.ext.auth.User)
		self.put USER_IN_CONTEXT, user
		self
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

	static EventBus getEventBus(RoutingContext self) {
		self.vertx().eventBus()
	}

	static MultiMap getParams(RoutingContext self) {
		self.request().params()
	}

	static MultiMap getHeaders(RoutingContext self) {
		self.request().headers()
	}

	static RoutingContext setPayload(RoutingContext self, def payload) {
		self.put PAYLOAD, payload
	}

	static RoutingContext yield(RoutingContext self, def payload) {
		setPayload self, payload
		self++
		self
	}

	static def getPayload(RoutingContext self) {
		self.get PAYLOAD
	}

	static RoutingContext minus(RoutingContext self, Throwable cause) {
		self.fail cause
		self
	}

	static RoutingContext minus(RoutingContext self, int status) {
		self.fail status
		self
	}

	static FailOr getFail(RoutingContext self) {
		new FailOr(ctx: self)
	}

	static Closure getNext(RoutingContext self) {
		return { self.next() }
	}

	static Ensure ensure(RoutingContext self, Closure expectation) {
		new Ensure(expected: expectation, ctx: self)
	}

	static List<String> getContentTypes(RoutingContext self) {
		String accept = self.request().headers().get(HttpHeaders.ACCEPT.toString())
		return Utils.getSortedAcceptableMimeTypes(accept)
	}

	static RoutingContext addMarshaller(RoutingContext self, String contentType, Marshaller marshaller) {
		Map<String, Marshaller> marshallers = self.get(MARSHALLERS) as Map
		if (!marshallers) {
			marshallers = [:]
			self.put(MARSHALLERS, marshallers)
		}
		marshallers[contentType] = marshaller
		self
	}

	static void setMarshallers(RoutingContext self, Map marshallers) {
		self.put MARSHALLERS, marshallers
		self
	}


	static Marshaller getMarshaller(RoutingContext self) {
		Map<String, Marshaller> marshallers = self.get(MARSHALLERS) as Map
		if (!marshallers || marshallers.empty) {
			return null
		}
		List<String> contentTypes = getContentTypes self
		if (!contentTypes || contentTypes.empty) return
		Collection<String> usableMarshallers = contentTypes.intersect marshallers.keySet()
		if (usableMarshallers.size() == 0) return null
		String contentType = usableMarshallers[0]
		try {
			self.response().headers().add(HttpHeaders.CONTENT_TYPE.toString(), contentType)
		} catch(all) {} // response already written, ...
		marshallers[usableMarshallers[0]]
	}

	static def getBody(RoutingContext self) {
		Marshaller m = getMarshaller self
		if (!m) {
			return self.bodyAsString as Buffer // not happy with that, but can't delegate :\
		}
		new RequestPayload(context: self)
	}

	static def getCachedBody(RoutingContext self, Class c) {
		Map<Class, Object> cached = self.get(CACHED_BODY) as Map
		if (!cached) {
			cached = [:]
			self.put CACHED_BODY, cached
			return null
		}
		cached[c]
	}

	static RoutingContext setCachedBody(RoutingContext self, Class c, def bodyAsC) {
		Map<Class, Object> cached = self.get(CACHED_BODY) as Map
		if (!cached) {
			cached = [:]
			self.put CACHED_BODY, cached
		}
		cached[c] = bodyAsC
		self
	}

	static RoutingContext redirect(RoutingContext self, String path, int statusCode = 302) {
		self.response().setStatusCode statusCode
		self.response().putHeader(HttpHeaders.LOCATION.toString(), path)
		self.response().end()
		self
	}

}
