router {
	extension('dateHeader') { String header, Boolean finalize = false ->
		return {
			response.headers[header] = new Date().time
			if (finalize) response++
			else it++
		}
	}
	route('/extensions/with/order') {
		dateHeader 'X-Date-Before'
		get {
			sleep(500)
			it++
		}
		dateHeader 'X-Date-After', true
	}
}