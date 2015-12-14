package extensions

import groovy.time.Duration
import io.vertx.groovy.core.http.HttpServerRequest
import io.vertx.groovy.ext.web.RoutingContext

class ThrottlingHandler {

    int amount
    Duration duration
    Map<String, List<Date>> clientAccesses = [:]

    void check(RoutingContext context) {
        HttpServerRequest request = context.request()
        String ip = request.remoteAddress().host()
        List<Date> accesses = clientAccesses[ip]
        if (!accesses) {
            accesses = []
            clientAccesses[ip] = accesses
        }
        List matching = accesses.collect { it >= duration.ago }
        if (matching.size() >= amount) {
            context.fail 420
            return
        }
        accesses << new Date()
        context++
    }
}
