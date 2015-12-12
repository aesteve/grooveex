package com.github.aesteve.vertx.groovy.builder

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
    String path
    def bodyHandler
    boolean cookies
    def sessionStore
    private List<Route> routes = []
    private List<List<Object>> missingMethods = []
	boolean blocking
	Set<Eval> expectations = []
	Set<Checker> checkers = []

    def static make(RouterDSL parent, String path, Closure closure, boolean cookies, String parentPath = null) {
		String completePath = ''
		if (parentPath) {
			completePath += parentPath
		}
		completePath += path
		RouteDSL routeDSL = new RouteDSL(path: completePath, parent: parent, cookies: cookies)
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = routeDSL
        closure.call()
    }

    def get(Closure handler) {
        createRoute(HttpMethod.GET, handler)
    }

    def post(Closure handler) {
        createRoute(HttpMethod.POST, handler, true)
    }

    def put(Closure handler) {
        createRoute(HttpMethod.PUT, handler, true)
    }

    def delete(Closure handler) {
        createRoute(HttpMethod.DELETE, handler)
    }

    def options(Closure handler) {
        createRoute(HttpMethod.OPTIONS, handler)
    }

    def head(Closure handler) {
        createRoute(HttpMethod.HEAD, handler)
    }

    def connect(Closure handler) {
        createRoute(HttpMethod.CONNECT, handler)
    }

    def trace(Closure handler) {
        createRoute(HttpMethod.TRACE, handler)
    }

    def patch(Closure handler) {
        createRoute(HttpMethod.PATCH, handler)
    }

    def cors(String origin) {
        parent.router.route(path).handler(CorsHandler.create(origin))
    }

	def expect(Closure expectation) {
		expectations << expectation
	}
	
	Checker check(Closure check) {
		Checker checker = new Checker(check:check) 
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
        parent.consumes contentType
    }

    def produces(String contentType) {
        parent.produces contentType
    }

    def route(String path, Closure clos) {
		RouteDSL.make parent, path, clos, cookies, this.path
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
				} catch(all) {
					ctx.fail 400
				}
			}
		}
		checkers.each {
			parent.router.route(method, path).handler it as Handler
		}
inherit co
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
        if (routes.empty) {
            // delay
            missingMethods << [name, args]
        }
        routes.each { Route route ->
            callMethodOnRoute(route, name, args)
        }
    }

    def callMethodOnRoute(route, name, args) {
        if (args && args.size() == 1) {
            route."$name"(args[0])
        } else {
            route."$name"(args)
        }
    }
}
