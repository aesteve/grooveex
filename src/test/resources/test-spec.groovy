router {
	route('/api/test') {
		get {
			response << 'OK!'
		}
	}
}