package com.github.aesteve.vertx.groovy.specs.ast

import com.github.aesteve.vertx.groovy.promise.Promise
import org.codehaus.groovy.runtime.CurriedClosure
import org.junit.Test
import services.FakeAsyncClient

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

class FakeAsyncClientSpec {

	FakeAsyncClient client = new FakeAsyncClient()
	
	@Test
	void testMethodExists() {
		Promise promise = client.someAsyncMethod('lala')
		assertNotNull promise
		assertTrue(promise.closure instanceof CurriedClosure)
	}
}
