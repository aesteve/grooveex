package com.github.aesteve.vertx.groovy.specs

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.groovy.ext.unit.TestContext
import io.vertx.groovy.ext.web.Router
import org.junit.Test

import static io.vertx.core.http.HttpHeaders.LOCATION

class RoutingContextSpec extends TestBase {

	final static String KEY = 'dog'
	final static String VAL = 'Snoopy'
	final static String VAL2 = 'Charlie'
	final static String PATH = '/routingcontexttest'
	final static String FAILED_STATUS = '/failWithStatus'
	final static String FAILED_THROW = '/failWithThrowable'
	final static String ASYNC_SERVICE = '/asyncService'
	final static int STATUS = 400
	final static Throwable ex = new RuntimeException("ex")

	void fakeServiceMethod(Boolean fail, Handler<AsyncResult<String>> handler) {
		if (fail) {
			handler.handle Future.failedFuture(ex)
		} else {
			handler.handle Future.succeededFuture(VAL)
		}
	}

	void checkTokenAsync(String token, Handler<AsyncResult<String>> handler) {
		if (token != 'secret') {
			handler.handle Future.succeededFuture(null)
		} else {
			handler.handle Future.succeededFuture(VAL)
		}
	}

	@Override
	void router() {
		router = Router.router vertx
		router[PATH] = {
			it[KEY] = VAL
			it++
		}
		router[PATH] = {
			response << it[KEY]
		}
		router[FAILED_STATUS] = {
			it -= STATUS
		}
		router[FAILED_THROW] = {
			it -= ex
		}
		router[ASYNC_SERVICE] = {
			fakeServiceMethod request.params['fail'].toBoolean(), fail | response.&end
		}
		router[ASYNC_SERVICE + '/2'] = {
			fakeServiceMethod request.params['fail'].toBoolean(), fail | next
		}
		router[ASYNC_SERVICE + '/2'] = {
			response << VAL2
		}
		router['/eventbus'] = {
			eventBus['test-address'] << 'something'
			response << 'sent'
		}
		router['/checkToken'] = {
			checkTokenAsync params['token'], ensure({ it }) & { put 'user', it } | 401
		}
		router['/checkToken'] = {
			response << it['user']
		}
		router['/redirectWith302'] = {
			redirect 'http://vertx.io'
		}
		router['/redirectWith301'] = {
			redirect 'http://vertx.io', 301
		}
	}

	@Test
	void testGetPutAndCall(TestContext context) {
		context.async { async ->
			client.getNow PATH, { response ->
				response >>> {
					assertEquals it as String, VAL
					async++
				}
			}
		}
	}

	@Test
	void testFailWithStatus(TestContext context) {
		context.async { async ->
			client.getNow FAILED_STATUS, { response ->
				assertEquals response.statusCode, STATUS
				async++
			}
		}
	}

	@Test
	void testFailWithThrowable(TestContext context) {
		context.async { async ->
			client.getNow FAILED_THROW, { response ->
				assertEquals response.statusCode, 500
				async++
			}
		}
	}


	@Test
	void testFailCheckToken(TestContext context) {
		context.async { async ->
			client.getNow '/checkToken?token=invalid', { response ->
				assertEquals response.statusCode, 401
				async++
			}
		}
	}

	@Test
	void testSuccessCheckToken(TestContext context) {
		context.async { async ->
			client.getNow "/checkToken?token=secret", { response ->
				assertEquals response.statusCode, 200
				response >>> {
					assertEquals it as String, VAL
					async++
				}
			}
		}
	}

	@Test
	void testFailAsyncService(TestContext context) {
		context.async { async ->
			client.getNow "${ASYNC_SERVICE}?fail=true", { response ->
				assertEquals response.statusCode, 500
				async++
			}
		}
	}

	@Test
	void testSuccessAsyncService(TestContext context) {
		context.async { async ->
			client.getNow "${ASYNC_SERVICE}?fail=false", { response ->
				assertEquals response.statusCode, 200
				response >>> {
					assertEquals it as String, VAL
					async++
				}
			}
		}
	}

	@Test
	void testFailAsyncService2(TestContext context) {
		context.async { async ->
			client.getNow "${ASYNC_SERVICE}/2?fail=true", { response ->
				assertEquals response.statusCode, 500
				async++
			}
		}
	}

	@Test
	void testSuccessAsyncService2(TestContext context) {
		context.async { async ->
			client.getNow "${ASYNC_SERVICE}/2?fail=false", { response ->
				assertEquals response.statusCode, 200
				response >>> {
					assertEquals it as String, VAL2
					async++
				}
			}
		}
	}

	@Test
	void testEventBus(TestContext context) {
		context.async { async ->
			vertx.eventBus['test-address'] >> { msg ->
				assertEquals msg.body as String, 'something'
				async.complete()
			}
			client.getNow '/eventbus', { response ->
				assertEquals response.statusCode, 200
			}
		}
	}

	@Test
	void test302(TestContext context) {
		context.async { async ->
			client.getNow '/redirectWith302', { response ->
				assertEquals response.statusCode, 302
				assertEquals response.headers[LOCATION], 'http://vertx.io'
				async++
			}
		}
	}

	@Test
	void test301(TestContext context) {
		context.async { async ->
			client.getNow '/redirectWith301', { response ->
				assertEquals response.statusCode, 301
				assertEquals response.headers[LOCATION], 'http://vertx.io'
				async++
			}
		}
	}

}
