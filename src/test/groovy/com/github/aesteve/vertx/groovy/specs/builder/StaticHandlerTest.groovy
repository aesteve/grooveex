package com.github.aesteve.vertx.groovy.specs.builder

import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

class StaticHandlerTest extends BuilderTestBase {

	@Test
	void asset(TestContext context) {
		context.async { async ->
			client.getNow '/assets/static-file.txt', { response ->
				assertEquals 200, response.statusCode
				response >>> {
					assertEquals it as String, 'just a static resource'
					async++
				}
			}
		}
	}

	@Test
	void instrumentedAsset(TestContext context) {
		context.async { async ->
			client.getNow '/instrumented-assets/static-file.txt', { response ->
				assertEquals 200, response.statusCode
				assertEquals response.headers['X-Custom-Header'], 'instrumented'
				response >>> {
					assertEquals it as String, 'just a static resource'
					async++
				}
			}
		}
	}

	@Test
	void subRouterAsset(TestContext context) {
		context.async { async ->
			client.getNow '/sub/assets/another-file.txt', { response ->
				assertEquals 200, response.statusCode
				response >>> {
					assertEquals it as String, 'just another file'
					async++
				}
			}
		}
	}

}
