package com.github.aesteve.vertx.groovy.specs.ast

import com.github.aesteve.vertx.groovy.promise.Promise
import com.github.aesteve.vertx.groovy.promise.PromiseTransformation
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.tools.ast.TransformTestHelper

class TestPromiseTransformation extends GroovyTestCase {

	public void testInvokeUnitTest() {
		def pkg = this.class.package.name.replaceAll '[.]', '/'
		def file = new File("src/test/groovy/${pkg}/FakeAsyncClient.groovy")
		assert file.exists()

		def invoker = new TransformTestHelper(new PromiseTransformation(), CompilePhase.SEMANTIC_ANALYSIS)

		def clazz = invoker.parse(file)
		def client = clazz.newInstance()
		Promise promise = client.someAsyncMethod()
		assertNotNull promise
		promise = client.someAsyncMethod('some param')
		assertNotNull promise
	}

}
