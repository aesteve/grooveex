package com.github.aesteve.vertx.groovy.specs.test

import com.github.aesteve.vertx.groovy.annot.BehaviourDriven
import com.github.aesteve.vertx.groovy.builder.RouterBuilder
import com.github.aesteve.vertx.groovy.specs.TestBase
import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

class AsyncSpec extends TestBase {

	@Test
	@BehaviourDriven
	void test(TestContext context) {
		when { client.get('/api/test') }
		then {
			statusCode == 200
			body as String == 'KO!'
		}
	}

	@Override
	void router() {
		router = new RouterBuilder(vertx: vertx)(new File('src/test/resources/test-spec.groovy'))
	}
}
