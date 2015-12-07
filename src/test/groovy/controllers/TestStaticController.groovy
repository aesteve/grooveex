package controllers

import groovy.json.JsonBuilder
import io.vertx.groovy.ext.web.RoutingContext

class TestStaticController {
    static Closure testClosure = { RoutingContext context ->
        context.response().end(new JsonBuilder([result: "closure"]).toString())
    }
}