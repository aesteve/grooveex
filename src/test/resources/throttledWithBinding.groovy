import extensions.RequestPerUnit
import extensions.ThrottlingHandler

Integer.metaClass.getReq = {
	new RequestPerUnit(nbRequests: delegate)
}

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