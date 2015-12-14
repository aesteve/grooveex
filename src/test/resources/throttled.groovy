import extensions.RequestPerUnit
import extensions.ThrottlingHandler
import groovy.time.TimeDuration

Integer.metaClass.getReq = {
    new RequestPerUnit(nbRequests: delegate)
}
def hour = new TimeDuration(1, 0, 0, 0)

router {
    extension('throttled') { amount, duration ->
        new ThrottlingHandler(amount: amount, duration: duration).&check
    }
    route('/limited') {
        throttled 10.req / hour
        get {
            response << "everything's fine for now"
        }
    }
}