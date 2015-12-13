package com.github.aesteve.vertx.groovy.builder

import com.github.aesteve.vertx.groovy.io.Marshaller
import io.vertx.core.http.HttpMethod
import io.vertx.groovy.core.Vertx
import io.vertx.groovy.ext.web.Route
import io.vertx.groovy.ext.web.Router
import io.vertx.groovy.ext.web.RoutingContext
import io.vertx.groovy.ext.web.handler.*
import io.vertx.groovy.ext.web.handler.sockjs.SockJSHandler
import io.vertx.groovy.ext.web.templ.TemplateEngine

import java.util.regex.Pattern

public class RouterDSL {

    Vertx vertx
    Router router
    Router parent
    String path
    Set<String> consumes = []
    Set<String> produces = []
    Map<String, Marshaller> marshallers = [:]
    boolean cookies
    private StaticHandler staticHandler
    private LinkedHashSet<MethodAndPath> toFinalize = [] as LinkedHashSet
    private Set<RouteDSL> children = [] as Set

    def make(Closure closure) {
        router = Router.router(vertx)
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = this
        closure()
        if (parent && path) {
            parent.mountSubRouter path, router
        }
    }

    def call(Closure closure) {
        make closure
    }

    def subRouter(String path, Closure closure) {
        RouterDSL dsl = new RouterDSL(vertx: vertx, parent: router, path: path)
        dsl.call(closure)
        children << dsl
        dsl
    }

    def staticHandler(String path, String webroot, Closure closure = null) {
        if (!staticHandler) {
            if (webroot) {
                staticHandler = StaticHandler.create webroot
            } else {
                staticHandler = StaticHandler.create()
            }
        }
        if (closure) {
            RouteDSL dsl = RouteDSL.make(this, path, cookies)
            dsl(closure)
            children << dsl
        }
        router.route(path).handler(staticHandler)
    }

    def staticHandler(String path, Closure closure = null) {
        staticHandler(path, null, closure)
    }

    def templateHandler(String path, TemplateEngine engine, Closure closure = null) {
        TemplateHandler tplHandler = TemplateHandler.create(engine)
        if (closure) {
            RouteDSL dsl = RouteDSL.make(this, path, cookies)
            dsl(closure)
            children << dsl
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

    def marshaller(String contentType, Marshaller marshaller) {
        marshallers[contentType] = marshaller
    }

    def route(String path, Closure clos) {
        RouteDSL dsl = RouteDSL.make(this, path, cookies)
        dsl(clos)
        children << dsl
    }

    def route(Pattern path, Closure clos) {
        RouteDSL dsl = RouteDSL.make(this, path, cookies)
        dsl(clos)
        children << dsl
    }

    private createRoute(method, path) {
        if (!path) {
            def methodStr = method.toString().toLowerCase()
            return router."$methodStr"()
        } else return router.route(method, path)
    }

    def makeRoute(def path, HttpMethod method, Closure closure = null) {
        createRoute(method, path).handler BodyHandler.create()
        if (cookies) {
            createRoute(method, path).handler CookieHandler.create()
        }
        createRoute(method, path).handler { ctx ->
            ctx.marshallers = marshallers
            ctx++
        }
        Route route = createRoute(method, path)
        consumes.each { route.consumes it }
        produces.each { route.produces it }
        if (closure) {
            route.handler { context ->
                closure.delegate = context
                closure.call(context)
            }
        }
        if (marshallers.size() > 0) {
            toFinalize << new MethodAndPath(method: method, path: path)
        }
        route
    }

    Router finish() {
        toFinalize.each {
            createRoute(it.method, it.path).handler { ctx ->
                Marshaller m = ctx.marshaller
                if (!m) {
                    m = marshallers.find().value
                }
                def payload = ctx.getPayload()
                if (!payload) {
                    ctx.response.end()
                    return
                }
                ctx.response().end(m.marshall(payload))
            }

        }
        children.each { it.finish() }
        router
    }

    def methodMissing(String name, args) {
        HttpMethod method
        try {
            method = HttpMethod.valueOf name?.toUpperCase()
        } catch (all) {}
        if (method) {
            if (args.size() == 1) {
                return makeRoute(args[0], method)
            } else {
                makeRoute(args[0], method, args[1])
            }
        } else {
            if (args && args.size() == 1) {
                router."$name"(args[0])
            } else {
                router."$name"(args)
            }
        }
    }
}