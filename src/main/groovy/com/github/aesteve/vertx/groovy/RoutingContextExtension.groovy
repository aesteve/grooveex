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
import io.vertx.groovy.ext.web.Route
import io.vertx.groovy.ext.web.RoutingContext
import io.vertx.groovy.ext.web.Session

@TypeChecked
class RoutingContextExtension {

    private final static String PAYLOAD = 'ROUTING_CTX_PAYLOAD'
    private final static String MARSHALLERS = 'ROUTING_CTX_MARSHALLERS'

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
        List<String> contentTypes = getContentTypes self
        List<String> usableMarshallers = contentTypes.intersect marshallers.keySet()
        if (usableMarshallers.size() == 0) return null
        marshallers[usableMarshallers[0]]
    }

    static def getBody(RoutingContext self) {
        Marshaller m = getMarshaller self
        if (!m) {
            return self.bodyAsString as Buffer // not happy with that, but can't delegate :\
        }
        new RequestPayload(context: self)
    }

}
