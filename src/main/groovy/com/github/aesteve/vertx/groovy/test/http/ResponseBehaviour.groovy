package com.github.aesteve.vertx.groovy.test.http

import io.vertx.groovy.core.http.HttpClientResponse
import io.vertx.groovy.ext.unit.Async
import io.vertx.groovy.ext.unit.TestContext

class ResponseBehaviour {

	List<Closure> expectations
	TestContext context
	Async async
	BodyCheckHandler bodyCheck
	HttpClientResponse response

	public ResponseBehaviour(List<Closure> expectations, TestContext context, Async async) {
		this.expectations = expectations
		this.context = context
		this.async = async
		expectations.each {
			it.delegate = this
			it(null)
		}
	}

	void check(HttpClientResponse response) {
		this.response = response
		if (bodyCheck != null) { // CAUTION : asBoolean !!
			response.bodyHandler {
				bodyCheck.buffer = it
				checkResponse(response)
			}
		} else {
			checkResponse(response)
		}
	}

	def checkResponse(HttpClientResponse response) {
		expectations.each {
			boolean res = it(response)?.asBoolean()
			context.assertTrue(res)
		}
		async.complete()
	}

	def getBody() {
		if (!bodyCheck) {
			bodyCheck = new BodyCheckHandler()
		}
		bodyCheck
	}

	def propertyMissing(String name) {
		if (response) {
			return response."$name"
		}
	}
}
