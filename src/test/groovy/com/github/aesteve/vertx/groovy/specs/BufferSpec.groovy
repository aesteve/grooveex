package com.github.aesteve.vertx.groovy.specs

import io.vertx.groovy.core.buffer.Buffer
import org.junit.Test

class BufferSpec {

	@Test
	void testString() {
		def test = "something"
		Buffer b = test as Buffer
		assert b as String == test
	}

	@Test
	void testGString() {
		def name = "Snoopy"
		def test = "hello $name"
		Buffer b = test as Buffer
		assert b as String == test
	}
}
