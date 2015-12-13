package com.github.aesteve.vertx.groovy.io

import groovy.transform.TypeChecked
import io.vertx.core.Handler
import io.vertx.groovy.ext.web.RoutingContext

interface Marshaller {

    String marshall(def obj)
    def unmarshall(String json, Class type)
}
