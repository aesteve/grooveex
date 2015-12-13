package com.github.aesteve.vertx.groovy

import io.vertx.groovy.core.MultiMap

class MultiMapExtension {

    static Object getAt(MultiMap self, CharSequence key) {
        self.get key.toString()
    }

    static void putAt(MultiMap self, CharSequence key, Object value) {
        self.add key.toString(), value
    }
}
