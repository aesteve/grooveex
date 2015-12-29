import oauth2.GithubUser

import static io.vertx.core.http.HttpHeaders.*

router {
	extension('findUsername') { mapName ->
		return { ctx ->
			String token = user?.principal()?.access_token
			if (token) {
				def tokens = vertx.sharedData[mapName]
				def username = tokens[token]
				if (username) {
					user = new GithubUser(user, username)
					ctx++
					return
				}
				def client = vertx.createHttpClient([defaultHost: 'api.github.com', ssl: true, defaultPort: 443])
				def req = client.get '/user', { response ->
					if (response.statusCode < 300) {
						response >>> {
							def userInfos = it as Map
							username = userInfos.login
							tokens[token] = username
							user = new GithubUser(user, username)
							ctx++
						}
					} else {
						println "Error while retrieving user infos $response.statusCode $response.statusMessage"
						ctx++
					}
				}
				req[USER_AGENT] = 'Vert.x Groovy Sugar Extension'
				req[AUTHORIZATION] = "token $token"
				req[ACCEPT] = 'application/vnd.github.v3+json'
				req++
			} else ctx++
		}
	}
	oauth2 {
		domain 'http://localhost:9000'
		clientID '26e529e1a7637e236322'
		clientSecret System.getEnv('GITHUB_CLIENT_SECRET')
		site 'https://github.com/login'
		tokenPath '/oauth/access_token'
		authorizationPath '/oauth/authorize'
		callback '/login/github'
	}
	route('/protected') {
		usesBody = false
		authority '*'
		findUsername 'user_tokens'
		get {
			response << "Hello ${user.username}"
		}
	}

}