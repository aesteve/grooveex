package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.groovy.ext.unit.Async
import io.vertx.groovy.ext.unit.TestContext

@TypeChecked
class TestContextExtension {

    static Closure asyncAssertSuccess(TestContext self, Handler handler = null) {
        Async async = self.async()
        return { AsyncResult res ->
            if (res.succeeded()) {
                try {
                    if (handler) {
                        handler.handle(res.result())
                    }
                    async.complete()
                } catch (all) {
                    self.fail(all)
                }
            } else {
                self.fail(res.cause())
            }
        }
    }

    static Closure asyncAssertFailure(TestContext self, Handler handler = null) {
        Async async = self.async()
        return { AsyncResult res ->
            if (res.failed()) {
                try {
                    handler.handle(res.cause())
                    async.complete()
                } catch (all) {
                    self.fail(all)
                }
            } else {
                self.fail("Was expecting a failure instead of success")
            }
        }
    }

    static void async(TestContext self, Closure clos) {
        clos.delegate = self
        clos self.async()
    }
}
