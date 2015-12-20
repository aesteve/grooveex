package com.github.aesteve.vertx.groovy.specs.ast

import com.github.aesteve.vertx.groovy.promise.Promise
import com.github.aesteve.vertx.groovy.promise.PromiseTransformation
import io.vertx.groovy.ext.unit.TestContext
import io.vertx.groovy.ext.unit.junit.VertxUnitRunner
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.tools.ast.TransformTestHelper
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(VertxUnitRunner)
class TestPromiseTransformation extends GroovyTestCase {

	def client

	@Before
	void createObservable() {
		def pkg = this.class.package.name.replaceAll '[.]', '/'
		def file = new File("src/test/groovy/${pkg}/FakeAsyncClient.groovy")
		assert file.exists()

		def invoker = new TransformTestHelper(new PromiseTransformation(), CompilePhase.SEMANTIC_ANALYSIS)

		def clazz = invoker.parse(file)
		client = clazz.newInstance()
	}

	@Test
	void testObservableSuccess(TestContext context) {
		String test = 'something'
		Promise obs = client.someAsyncMethod(test)
		assertNotNull obs
		context.async { async ->
			obs.subscribe {
				context.assertEquals it, test
				async++
			}
		}
	}

}
