package com.github.aesteve.vertx.groovy

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.groovy.ext.web.RoutingContext

class FailOr {

    RoutingContext ctx

    Handler<AsyncResult> or(Closure clos) {
        return { AsyncResult res ->
            if (res.failed()) {
                ctx.fail res.cause()
            } else {
                clos.delegate = ctx
                clos res.result()
            }
        } as Handler
    }
}
