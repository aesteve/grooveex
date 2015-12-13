package com.github.aesteve.vertx.groovy.builder

import groovy.transform.TypeChecked
import io.vertx.core.Handler
import io.vertx.groovy.ext.web.RoutingContext

@TypeChecked
class Checker {

    Closure check
    int status

    Checker or(int status) {
        this.status = status
        this
    }

    def asType(Class c) {
        if (c == Handler) {
            return { RoutingContext ctx ->
                check.delegate = ctx
                boolean result = check(ctx)?.asBoolean()
                if (result) {
                    ctx++
                } else {
                    ctx.fail status
                }
            } as Handler
        }
    }
}
