package com.github.aesteve.vertx.groovy.io

interface Marshaller {

	String marshall(def obj)

	def unmarshall(String json, Class type)
}
