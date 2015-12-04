package com.github.aesteve.vertx.groovy.specs

import io.vertx.core.buffer.Buffer
import org.junit.Test

class BufferSpec {

	@Test
	void testString() {
		String test = "something"
		Buffer b = test as Buffer
		// assert b.toString() == test
	}
}
