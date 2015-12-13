package com.github.aesteve.vertx.groovy.io

import groovy.transform.TypeChecked
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
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
        Marshaller m = context.getMarshaller()
        try {
            return m.unmarshall(context.bodyAsString, c)
        } catch(all) {
            context.fail 400
        }
    }
}
