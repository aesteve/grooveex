package com.github.aesteve.vertx.groovy.test

import com.github.aesteve.vertx.groovy.test.http.ResponseBehaviour
import io.vertx.core.http.HttpMethod
import io.vertx.groovy.core.http.HttpClientRequest
import io.vertx.groovy.ext.unit.Async
import io.vertx.groovy.ext.unit.TestContext

/**
 * Wraps a test method to evaluate the DSL
 *
 * Created by aesteve on 31/12/2015.
 */
class MethodWrapper {

	Closure closure

	Closure whenClause
	List<Closure> expectations

	public MethodWrapper(Closure when, Closure... expectations) {
		whenClause = when
		this.expectations = expectations
	}

	void call(TestContext context) {
		runTest(context)
	}

	private void runTest(TestContext context) {
		Async async = context.async()
		def stream = whenClause()
		if (!stream) {
			context.fail("The 'when' clause should return an object")
			return
		}
		createCallback(stream, context, async)
	}

	private void createCallback(def stream, TestContext context, Async async) {
		if (stream instanceof HttpClientRequest) {
			stream.handler new ResponseBehaviour(expectations, context, async).&check
			stream.end()
		} else {
			context.fail("Unknown type of object : ${stream.class}")
		}
	}

	def methodMissing(String methodName, args) {
		println "methodMissing $methodName"
		def http = HttpMethod.valueOf(methodName.toUpperCase())
		if (http) {
			// TODO
		}
		throw new MissingMethodException(methodName, this.class, args)
	}

}
