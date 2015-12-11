package com.github.aesteve.vertx.groovy.builder

import groovy.transform.TypeChecked
import io.vertx.core.Handler
import io.vertx.groovy.ext.web.RoutingContext

@TypeChecked
class Expectation {
	
	Object value
	Object expected
	Class expectedType
	boolean shouldExist = false
	
	def toEqual(Object expected) {
		this.expected = expected
	}
	
	def getToExist() {
		shouldExist = true
	}
	
	def toBeOfType(Class type) {
		expectedType = type
	}
	
	def asType(Class c) {
		if (c == Handler) {
			return { RoutingContext ctx ->
				if (shouldExist && !value) {
					ctx.fail 400
					return
				}
				if (expected && value != expected) {
					ctx.fail 400
					return
				}
				if (expectedType && !(value.class.isAssignableFrom(expectedType))) {
					ctx.fail 400
					return
				}
				ctx++
			}
		}
	}
}
