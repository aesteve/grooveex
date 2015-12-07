package com.github.aesteve.vertx.groovy;

import groovy.lang.GString
import groovy.transform.TypeChecked
import io.vertx.groovy.core.buffer.Buffer
import org.codehaus.groovy.runtime.StringGroovyMethods

@TypeChecked
class StringExtension {

	static Object asType(String self, Class c) {
		if (c == Buffer.class) {
			return Buffer.buffer(self)
		}
		StringGroovyMethods.asType(self, c)
	}

	static Object asType(GString self, Class c) {
		if (c == Buffer.class) {
			return Buffer.buffer(self.toString())
		}
		StringGroovyMethods.asType(self.toString(), c)
	}
}
