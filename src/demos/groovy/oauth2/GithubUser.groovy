package oauth2

import io.vertx.groovy.ext.auth.common.User

class GithubUser extends User {

	public GithubUser(JGithubUser java) {
		super(java)
	} 

	String getUsername() {
		getDelegate().username
	}
}
