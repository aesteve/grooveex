package com.github.aesteve.vertx.groovy

import io.vertx.groovy.core.streams.WriteStream;

class WriteStreamExtension {

    static <T> WriteStream plus(WriteStream self, T data) {
        self.write data
    }

}
