package com.github.aesteve.vertx.groovy

import groovy.lang.GString;import groovy.transform.TypeChecked
import io.vertx.groovy.core.buffer.Buffer;

@TypeChecked
class BufferStaticExtension {

	static Buffer getBuffer(Buffer self) {
		Buffer.buffer()
	}
	
}
