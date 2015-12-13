package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.groovy.core.Vertx
import io.vertx.groovy.core.eventbus.EventBus

@TypeChecked
class VertxExtension {

    static EventBus getEventBus(Vertx self) {
        self.eventBus()
    }

    static io.vertx.groovy.core.file.FileSystem getFileSystem(Vertx self) {
        self.fileSystem()
    }


}
