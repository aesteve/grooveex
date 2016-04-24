package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.groovy.core.Context

@TypeChecked
class ContextExtension {

  static Map getConfig(Context context) {
    context.config()
  }

}
