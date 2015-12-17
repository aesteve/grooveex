package com.github.aesteve.vertx.groovy.specs.ast

import org.junit.Test
import static org.junit.Assert.*

import services.FakeAsyncClient

class FakeAsyncClientSpec {

	FakeAsyncClient client = new FakeAsyncClient()
	
	@Test
	void testMethodExists() {
		println client.someAsyncMethod(true)
	}
}
