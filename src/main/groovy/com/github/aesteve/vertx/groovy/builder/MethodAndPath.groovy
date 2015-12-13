package com.github.aesteve.vertx.groovy.builder

import groovy.transform.EqualsAndHashCode
import io.vertx.core.http.HttpMethod

@EqualsAndHashCode
class MethodAndPath {
    HttpMethod method
    String path
}
