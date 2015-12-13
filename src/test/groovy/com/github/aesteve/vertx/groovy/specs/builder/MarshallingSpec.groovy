package com.github.aesteve.vertx.groovy.specs.builder

import io.vertx.core.json.JsonObject
import io.vertx.groovy.ext.unit.TestContext
import org.junit.Test

import static io.vertx.core.http.HttpHeaders.ACCEPT
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE

class MarshallingSpec extends BuilderTestBase {


    @Test
    void testMarshalling(TestContext context) {
        String sent = new JsonObject().put('name', 'Snoopy').put('breed', 'Beagle').toString()
        context.async { async ->
            def req = client.post('/marshall/dog', { response ->
                assertEquals response.statusCode, 200
                response >>> {
                    assertEquals it as String, sent
                    async ++
                }
            })
            req[CONTENT_TYPE] = "application/json"
            req[ACCEPT] = "application/json"
            req << sent
        }
    }

}
