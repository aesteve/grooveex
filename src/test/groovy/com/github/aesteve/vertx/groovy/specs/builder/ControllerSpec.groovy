package com.github.aesteve.vertx.groovy.specs.builder

import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

class ControllerSpec extends BuilderTestBase {

    @Test
    void testGet(TestContext context) {
        context.async { async ->
            client.getNow '/rest', { response ->
                assertEquals response.statusCode, 200
                response >>> {
                    assertEquals it as String, 'get'
                    async++
                }
            }
        }
    }

    @Test
    void testPost(TestContext context) {
        context.async { async ->
            def req = client.post '/rest', { response ->
                assertEquals response.statusCode, 200
                response >>> {
                    assertEquals it as String, 'post'
                    async++
                }
            }
            req++
        }
    }


    @Test
    void testPut(TestContext context) {
        context.async { async ->
            def req = client.put '/rest', { response ->
                assertEquals response.statusCode, 200
                response >>> {
                    assertEquals it as String, 'put'
                    async++
                }
            }
            req++
        }
    }

    @Test
    void testDelete(TestContext context) {
        context.async { async ->
            def req = client.delete '/rest', { response ->
                assertEquals response.statusCode, 200
                response >>> {
                    assertEquals it as String, 'delete'
                    async++
                }
            }
            req++
        }
    }

}
