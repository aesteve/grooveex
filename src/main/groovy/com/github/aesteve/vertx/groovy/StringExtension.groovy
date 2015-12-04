package com.github.aesteve.vertx.groovy;

import groovy.transform.TypeChecked;
import io.vertx.groovy.core.buffer.Buffer;

@TypeChecked
class StringExtension {

	static Object asType(String self, Class c) {
		if (c == Buffer.class) {
			return Buffer.buffer(self)
		}
		// FIXME ! delegate ?
		throw new ClassCastException("Cannot cast String as $c")
	}
	
	static Object asType(GString self, Class c) {
		if (c == Buffer.class) {
			return Buffer.buffer(self.toString())
		}
		// FIXME ! delegate ?
		throw new ClassCastException("Cannot cast GString as $c")
	}
}
