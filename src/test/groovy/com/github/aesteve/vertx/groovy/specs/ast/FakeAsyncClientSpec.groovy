package com.github.aesteve.vertx.groovy.specs.ast

import com.github.aesteve.vertx.groovy.promise.Promise
import io.vertx.groovy.ext.unit.TestContext
import io.vertx.groovy.ext.unit.junit.VertxUnitRunner
import org.codehaus.groovy.runtime.CurriedClosure
import org.junit.Test
import org.junit.runner.RunWith
import services.FakeAsyncClient

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

@RunWith(VertxUnitRunner)
class FakeAsyncClientSpec {

	FakeAsyncClient client = new FakeAsyncClient()
	
	@Test
	void testMethodExists(TestContext ctx) {
		String test = 'lala'
		Promise promise = client.someAsyncMethod test
		assertNotNull promise
		assertTrue(promise.closure instanceof CurriedClosure)
		ctx.async { async ->
			promise.onSucceed {
				ctx.assertEquals it, test
				async++
			}
			promise()
		}
	}

	@Test
	void testVoidMethodExists(TestContext ctx) {
		Promise promise = client.someAsyncMethod()
		assertNotNull promise
		assertTrue(promise.closure instanceof CurriedClosure)
		ctx.async { async ->
			promise.onSucceed {
				ctx.assertEquals it, 'void'
				async++
			}
			promise()
		}
	}
}
