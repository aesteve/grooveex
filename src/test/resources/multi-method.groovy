router {
	route('/multimethods') {
		options | get | post {
			response << 'get | post | options'
		}
		put {
			response << 'put'
		}
	}
}