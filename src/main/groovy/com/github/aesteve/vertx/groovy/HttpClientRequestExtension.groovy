package com.github.aesteve.vertx.groovy

import groovy.json.JsonBuilder
import groovy.transform.TypeChecked
import io.vertx.groovy.core.MultiMap
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.HttpClientRequest

@TypeChecked
class HttpClientRequestExtension {

    static void next(HttpClientRequest self) {
        self.end()
    }

    static void leftShift(HttpClientRequest self, Buffer buff) {
        self.end buff
    }

    static void leftShift(HttpClientRequest self, String s) {
        self.end s
    }

    static void leftShift(HttpClientRequest self, JsonBuilder json) {
        self.end json.toString()
    }

    static MultiMap getHeaders(HttpClientRequest self) {
        self.headers()
    }

}
