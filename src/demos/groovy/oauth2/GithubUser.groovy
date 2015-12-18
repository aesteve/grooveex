package oauth2

import io.vertx.groovy.ext.auth.User

class GithubUser extends User {

	String username
	User original

	GithubUser(User original, String username) {
		super(original.getDelegate())
		this.original = original
		this.username = username
	}

	def getDelegate() {
		original.getDelegate()
	}
}
