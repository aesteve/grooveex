package com.github.aesteve.vertx.groovy.builder

import io.vertx.core.http.HttpMethod
import io.vertx.ext.auth.oauth2.OAuth2FlowType
import io.vertx.groovy.core.Vertx
import io.vertx.groovy.ext.auth.oauth2.OAuth2Auth
import io.vertx.groovy.ext.web.Router
import io.vertx.groovy.ext.web.handler.OAuth2AuthHandler

class OAuth2DSL {

	Router router
	Vertx vertx
	String domain
	String clientID
	String clientSecret
	String site
	String authorizationPath
	String tokenPath
	HttpMethod callbackMethod = HttpMethod.GET
	Set<String> authorities = []
	OAuth2FlowType flowType = OAuth2FlowType.AUTH_CODE
	OAuth2AuthHandler handler
	Closure callback
	String callbackPath

	Map getOptions() {
		[
			clientID         : clientID,
			clientSecret     : clientSecret,
			site             : site,
			tokenPath        : tokenPath,
			authorizationPath: authorizationPath
		]
	}

	OAuth2AuthHandler make(Closure clos) {
		clos.delegate = this
		clos.resolveStrategy = Closure.DELEGATE_FIRST
		clos()
		OAuth2Auth provider = OAuth2Auth.create vertx, flowType, options
		handler = OAuth2AuthHandler.create provider, domain
		if (callback) {
			router.get(callbackPath).handler callback
		}
		handler.setupCallback router.get(callbackPath)
	}

	def callback(String path, Closure clos = null) {
		callbackPath = path
		if (clos) {
			callback = { ctx ->
				clos.delegate = ctx
				clos(ctx)
			}
		}
	}

	def authority(String authority) {
		authorities << authority
	}

	def methodMissing(String name, def args) {
		if (args.size() != 1) throw new MissingMethodException(name, this.class, args)
		try {
			this.setProperty(name, args[0])
		} catch (all) {
			throw new MissingMethodException(name, this.class, args)
		}
	}
}
