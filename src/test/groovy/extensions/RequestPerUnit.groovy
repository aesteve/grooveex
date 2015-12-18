package extensions

import groovy.time.TimeDuration

class RequestPerUnit {
	int nbRequests

	def div(TimeDuration duration) {
		return [nbRequests, duration]
	}
}
