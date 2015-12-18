package com.github.aesteve.vertx.groovy.io

import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.groovy.ext.web.RoutingContext

class RequestPayload {

	RoutingContext context

	def asType(Class c) {
		if (c == String.class) {
			return context.bodyAsString
		}
		if (c == JsonObject.class) {
			return context.bodyAsJson
		}
		if (c == Buffer.class) {
			return context.bodyAsString as Buffer // pretty bad... but still the delegate issue
		}
		if (context.getCachedBody(c)) {
			return context.getCachedBody(c)
		}
		Marshaller m = context.getMarshaller()
		try {
			def asObject = m.unmarshall(context.bodyAsString, c)
			context.setCachedBody(c, asObject)
			return asObject
		} catch (all) {
			context.fail 400
		}
	}
}
