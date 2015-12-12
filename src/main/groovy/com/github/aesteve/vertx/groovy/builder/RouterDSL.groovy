package com.github.aesteve.vertx.groovy.builder

import io.vertx.core.http.HttpMethod
import io.vertx.groovy.core.Vertx
import io.vertx.groovy.ext.web.Route
import io.vertx.groovy.ext.web.Router
import io.vertx.groovy.ext.web.RoutingContext
import io.vertx.groovy.ext.web.handler.CookieHandler
import io.vertx.groovy.ext.web.handler.FaviconHandler
import io.vertx.groovy.ext.web.handler.StaticHandler
import io.vertx.groovy.ext.web.handler.TemplateHandler
import io.vertx.groovy.ext.web.handler.sockjs.SockJSHandler
import io.vertx.groovy.ext.web.templ.TemplateEngine

public class RouterDSL {

    Vertx vertx
    Router router
    Set<String> consumes = []
    Set<String> produces = []
    boolean cookies
    private StaticHandler staticHandler

    def make(Closure closure) {
        router = Router.router(vertx)
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = this
        closure()
    }

    def subRouter(String path, Closure closure) {
        RouterDSL dsl = new RouterDSL(vertx: vertx)
        dsl.make(closure)
        router.mountSubRouter(path, dsl.router)
    }

    def staticHandler(String path, Closure closure = null) {
        if (!staticHandler) {
            staticHandler = StaticHandler.create()
        }
        if (closure) {
            RouteDSL.make(this, path, closure, cookies)
        }
        router.route(path).handler(staticHandler)
    }

    def staticHandler(String path, String webroot, Closure closure = null) {
        if (closure) {
            RouteDSL.make(this, path, closure, cookies)
        }
        router.route(path).handler(StaticHandler.create(webroot))
    }

    def templateHandler(String path, def engine, Closure closure = null) {
        TemplateHandler tplHandler = TemplateHandler.create(engine)
        if (closure) {
            RouteDSL.make(this, path, closure, cookies)
        }
        router.route(path).handler(tplHandler)
    }

    def sockJS(String path, Map options = [:], Closure closure) {
        SockJSHandler sockJS = SockJSHandler.create(vertx, options)
        sockJS.socketHandler(closure)
        router.route(path).handler(sockJS)
    }

    def favicon(String iconPath = null, Long maxAgeSeconds = null) {
        FaviconHandler favHandler
        if (iconPath) {
            if (maxAgeSeconds && maxAgeSeconds > 0) {
                favHandler = FaviconHandler.create(iconPath, maxAgeSeconds)
            } else {
                favHandler = FaviconHandler.create(iconPath)
            }
        } else {
            if (maxAgeSeconds && maxAgeSeconds > 0) {
                favHandler = FaviconHandler.create(maxAgeSeconds)
            } else {
                favHandler = FaviconHandler.create()
            }
        }
        router.route().handler(favHandler)
        router.route().handler { RoutingContext context ->
            context.response().end()
        }
    }

    def consumes(String contentType) {
        consumes << contentType
        this
    }

    def produces(String contentType) {
        produces << contentType
        this
    }

    def route(String path, Closure closure) {
        RouteDSL.make(this, path, closure, cookies)
    }

    def get(String path = null, Closure closure) {
        makeRoute(path, HttpMethod.GET, closure)
    }

    def post(String path = null, Closure closure) {
        makeRoute(path, HttpMethod.POST, closure)
    }

    def put(String path = null, Closure closure) {
        makeRoute(path, HttpMethod.PUT, closure)
    }

    def delete(String path = null, Closure closure) {
        makeRoute(path, HttpMethod.DELETE, closure)
    }

    def options(String path, Closure closure) {
        makeRoute(path, HttpMethod.OPTIONS, closure)
    }

    def head(String path = null, Closure closure) {
        makeRoute(path, HttpMethod.HEAD, closure)
    }

    def connect(String path = null, Closure closure) {
        makeRoute(path, HttpMethod.CONNECT, closure)
    }

    def patch(String path = null, Closure closure) {
        makeRoute(path, HttpMethod.PATCH, closure)
    }

    def trace(String path = null, Closure closure) {
        makeRoute(path, HttpMethod.TRACE, closure)
    }

    def makeRoute(String path, HttpMethod method, Closure closure) {
        Route route
        if (!path) {
            def methodStr = method.toString().toLowerCase()
            if (cookies) {
                router."$methodStr"().handler(CookieHandler.create())
            }
            route = router."$methodStr"()
        } else {
            if (cookies) {
                router.route(method, path).handler(CookieHandler.create())
            }
            route = router.route(method, path)
        }
        consumes.each { route.consumes it }
        produces.each { route.produces it }
        route.handler { context ->
            closure.delegate = context
            closure.call(context)
        }
    }

    def methodMissing(String name, args) {
        if (args && args.size() == 1) {
            router."$name"(args[0])
        } else {
            router."$name"(args)
        }
    }
}