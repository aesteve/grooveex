package com.github.aesteve.vertx.groovy.specs.builder

import com.github.aesteve.vertx.groovy.builder.RouterBuilder
import com.github.aesteve.vertx.groovy.specs.TestBase

abstract class BuilderTestBase extends TestBase {
	@Override
	void router() {
		router = RouterBuilder.buildRouter vertx, new File("src/test/resources/routes.groovy")
	}
}
