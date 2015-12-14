package com.github.aesteve.vertx.groovy.builder

import com.github.aesteve.vertx.groovy.io.Marshaller
import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.groovy.ext.web.Route
import io.vertx.groovy.ext.web.handler.BodyHandler
import io.vertx.groovy.ext.web.handler.CorsHandler
import io.vertx.groovy.ext.web.handler.SessionHandler
import io.vertx.groovy.ext.web.sstore.ClusteredSessionStore
import io.vertx.groovy.ext.web.sstore.LocalSessionStore

class RouteDSL {

    RouterDSL parent
    def path
    def bodyHandler
    boolean cookies
    def sessionStore
    private List<Route> routes = []
    private List<List<Object>> missingMethods = []
    boolean blocking
    List<Closure> expectations = []
    List<Checker> checkers = []
    List<String> consumes = []
    List<String> produces = []
    Map<String, Marshaller> marshallers = [:]

    def static make(RouterDSL parent, def path, boolean cookies, String parentPath = null) {
        String completePath = ''
        if (parentPath) {
            completePath += parentPath
        }
        completePath += path
        new RouteDSL(path: completePath, parent: parent, cookies: cookies)
    }

    void call(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = this
        closure.call(this)
    }

    def cors(String origin) {
        parent.router.route(path).handler(CorsHandler.create(origin))
    }

    def expect(Closure expectation) {
        expectations << expectation
    }

    Checker check(Closure check) {
        Checker checker = new Checker(check: check)
        checkers << checker
        checker
    }

    def session(Map options) {
        if (options.store) {
            sessionStore = options.store
        } else if (options.clustered) {
            sessionStore = LocalSessionStore.create(parent.vertx)
        } else {
            sessionStore = ClusteredSessionStore.create(parent.vertx)
        }
    }

    def consumes(String contentType) {
        consumes << contentType
    }

    def produces(String contentType) {
        produces << contentType
    }

    def route(String path, Closure clos) {
        RouteDSL.make(parent, path, cookies, this.path)(clos)
    }

    private void createRoute(HttpMethod method, Closure handler, boolean useBodyHandler = false) {
        if (useBodyHandler) {
            if (!bodyHandler) {
                bodyHandler = BodyHandler.create()
            }
            parent.router.route(method, path).handler(bodyHandler)
        }
        if (sessionStore) {
            parent.router.route(path).handler(SessionHandler.create(sessionStore))
        }
        expectations.each { expectation ->
            parent.router.route(method, path).handler { ctx ->
                try {
                    expectation.delegate = ctx
                    boolean expected = expectation(ctx)?.asBoolean()
                    if (!expected) {
                        ctx.fail 400
                    } else {
                        ctx++
                    }
                } catch (all) {
                    ctx.fail 400
                }
            }
        }
        checkers.each {
            parent.router.route(method, path).handler it as Handler
        }
        marshallers << parent.marshallers
        parent.router.route(method, path).handler { ctx ->
            ctx.marshallers = marshallers
            ctx++
        }
        Route route = parent.router.route(method, path)
        consumes.addAll parent.consumes
        produces.addAll parent.produces
        consumes.each { route.consumes it }
        produces.each { route.produces it }
        missingMethods.each { methodMissing ->
            callMethodOnRoute(route, methodMissing[0], methodMissing[1])
        }
        if (!blocking) {
            route.handler { context ->
                handler.delegate = context
                handler context
            }
        } else {
            route.blockingHandler { context ->
                handler.delegate = context
                handler context
            }
        }
        routes << route
    }

    def methodMissing(String name, args) {
		Closure extension = parent.extensions[name]
		if (extension) {
            Closure handler = extension.call(*args)
			if (handler) {
				parent.router.route(path).handler { ctx ->
					handler.delegate = ctx
					handler(ctx)
				}
			}
			return
		}
        HttpMethod method
        try {
            method = HttpMethod.valueOf name?.toUpperCase()
        } catch (all) {
        }
        if (method) {
            createRoute(method, args[0], true)
        } else {
            if (routes.empty) {
                // delay
                missingMethods << [name, args]
            }
            routes.each { Route route ->
                callMethodOnRoute(route, name, args)
            }
        }
    }

    def callMethodOnRoute(route, name, args) {
        route."$name"(*args)
    }

}
