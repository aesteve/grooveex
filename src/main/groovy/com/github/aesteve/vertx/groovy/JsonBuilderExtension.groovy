package com.github.aesteve.vertx.groovy

import groovy.json.JsonBuilder
import groovy.transform.TypeChecked
import io.vertx.groovy.core.buffer.Buffer

@TypeChecked
class JsonBuilderExtension {

    static def asType(JsonBuilder self, Class c) {
        if (c == String.class) return self.toString()
		if (c == Buffer.class) return Buffer.buffer(self.toString())
    }

}
