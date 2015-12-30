router {
	route('/allInline') {
		blocking = true
		all {
			response.headers['X-Date-Before'] = new Date().time
			sleep(500)
			it++
		}
		get {
			it++
		}
		post {
			it++
		}
		all {
			response.headers['X-Date-After'] = new Date().time
			response.ok << 'Done'
		}
	}
	route('/allMulti') {
		blocking = true
		all {
			response.headers['X-Date-Before'] = new Date().time
			sleep(500)
			it++
		}
		get & post {
			it++
		}
		all {
			response.headers['X-Date-After'] = new Date().time
			response.ok << 'Done'
		}
	}
}