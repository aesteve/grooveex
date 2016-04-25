package com.github.aesteve.vertx.groovy.specs

import io.vertx.core.Future
import io.vertx.groovy.core.Vertx
import io.vertx.groovy.ext.unit.Async
import io.vertx.groovy.ext.unit.TestContext
import io.vertx.groovy.ext.unit.junit.VertxUnitRunner
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(VertxUnitRunner.class)
class FutureTest {

	private Vertx vertx

	@Before
	void setUp() {
		vertx = Vertx.vertx
	}


	@Test
	void testCompleteFuture(TestContext context) {
		Async async = context.async()
		Future global = Future.getFuture()
		String result = "OK"
		global.handler = {
			context.assertFalse it.failed
			context.assertFalse(-it)
			context.assertTrue it.succeeded
			context.assertTrue(+it)
			context.assertEquals it.result, result

			if (!it) {
				throw new RuntimeException("Expecting if comparison to return true")
			} else {
				async.complete()
			}
		}
		vertx.executeBlocking({ f ->
			try {
				Thread.sleep(100)
			} catch (all) {
			}
			f += result
		}, global.completer())
	}

	@Test
	void testFailFuture(TestContext context) {
		Async async = context.async()
		Future global = Future.getFuture()
		String cause = "cause"
		global.handler = {
			context.assertTrue it.failed
			context.assertTrue(-it)
			context.assertFalse it.succeeded
			context.assertFalse(+it)
			context.assertNull it.result
			context.assertEquals it.cause.message, cause

			if (it) {
				throw new RuntimeException("Expecting if comparison to be false")
			} else {
				async.complete()
			}

		}
		vertx.executeBlocking({ f ->
			try {
				Thread.sleep(100)
			} catch (all) {
			}
			f -= cause
		}, global.completer())
	}

	@Test
	void testCompleteNoResult(TestContext context) {
		Async async = context.async()
		Future global = Future.getFuture()
		global.handler = {
			context.assertFalse it.failed
			context.assertFalse(-it)
			context.assertTrue it.succeeded
			context.assertTrue(+it)
			context.assertNull it.result
			context.assertNull it.cause

			if (it) {
				async.complete()
			} else {
				throw new RuntimeException("Expecting if comparison to return true")
			}

		}
		vertx.executeBlocking({ f ->
			try {
				Thread.sleep(100)
			} catch (all) {
			}
			f++
		}, global.completer())
	}

}
