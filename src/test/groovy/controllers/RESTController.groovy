package controllers

import io.vertx.groovy.ext.web.RoutingContext

class RESTController {

    void get(RoutingContext ctx) {
        ctx.response << 'get'
    }

    void put(RoutingContext ctx) {
        ctx.response << 'put'
    }

    void post(RoutingContext ctx) {
        ctx.response << 'post'
    }

    void delete(RoutingContext ctx) {
        ctx.response << 'delete'
    }

    // ...
}
