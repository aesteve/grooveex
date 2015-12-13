package com.github.aesteve.vertx.groovy.specs

import io.vertx.core.http.HttpMethod
import io.vertx.groovy.ext.unit.TestContext
import io.vertx.groovy.ext.web.Route
import io.vertx.groovy.ext.web.Router
import org.junit.Test

class RegexSpec extends TestBase {

    @Override
    void router() {
        router = Router.router vertx
        Route route = router.route HttpMethod.GET, ~/.*foo/ // routeWithRegex
        route ~/\/([^\/]+)\/([^\/]+)/ // pathRegex
        route >> {
            response << "params: ${params['param0']},${params['param1']}"
        }
    }

    @Test
    void testRegex(TestContext context) {
        context.async { async ->
            client.getNow '/foo/bar', { response ->
                assertEquals response.statusCode, 200
                response >>> {
                    assertEquals it as String, 'params: foo,bar'
                    async++
                }
            }
        }
    }
}
