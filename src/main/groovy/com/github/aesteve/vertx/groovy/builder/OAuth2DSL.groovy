package com.github.aesteve.vertx.groovy.builder

import groovy.transform.TypeChecked
import io.vertx.core.http.HttpMethod
import io.vertx.ext.auth.oauth2.OAuth2FlowType
import io.vertx.groovy.core.Vertx
import io.vertx.groovy.ext.auth.oauth2.OAuth2Auth
import io.vertx.groovy.ext.web.Route
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
	String callbackPath
	HttpMethod callbackMethod = HttpMethod.GET
	Set<String> authorities = []
	OAuth2FlowType flowType = OAuth2FlowType.AUTH_CODE
	
	Map getOptions() {
		[
			clientID: clientID,
			clientSecret: clientSecret,
			site: site,
			tokenPath: tokenPath,
			authorizationPath: authorizationPath
		]
	}
	
	OAuth2AuthHandler make(Closure clos) {
		clos.delegate = this
		clos()
		OAuth2Auth provider = OAuth2Auth.create vertx, flowType, options
		OAuth2AuthHandler handler = OAuth2AuthHandler.create provider, domain
		Route callbackRoute = router.route callbackMethod, callbackPath
		handler.setupCallback callbackRoute
	}
	
	def authority(String authority) {
		authorities << authority
	}
	
	def missingMethod(String name, def args) {
		if (args.size() != 1) throw new MissingMethodException(name, this.class, args)
		try {
			this.setProperty(name, args[0])
		} catch(all) {
			throw new MissingMethodException(name, this.class, args)
		}
	}
}
