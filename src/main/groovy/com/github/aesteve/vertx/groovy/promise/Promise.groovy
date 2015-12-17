package com.github.aesteve.vertx.groovy.promise

import org.codehaus.groovy.runtime.CurriedClosure

class Promise {

    CurriedClosure closure

    public Promise(CurriedClosure closure) {
        this.closure = closure
    }
}
