package oauth2

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User


class JGithubUser implements User {

	String username
	User original
	
	public JGithubUser(User original, String username) {
		this.username = username
	}
	
	@Override
	public User isAuthorised(String authority, Handler<AsyncResult<Boolean>> resultHandler) {
		return original.isAuthorised(authority, resultHandler)
	}

	@Override
	public User clearCache() {
		return original.clearCache()
	}

	@Override
	public JsonObject principal() {
		return original.principal()
	}

	@Override
	public void setAuthProvider(AuthProvider authProvider) {
		original.setAuthProvider(authProvider)
	}
	
}
