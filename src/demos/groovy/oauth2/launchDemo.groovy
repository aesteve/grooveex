package oauth2

import io.vertx.groovy.core.Vertx

import com.github.aesteve.vertx.groovy.builder.ServerBuilder

 def builder = new ServerBuilder(vertx: Vertx.vertx)
 def server = builder.buildServer new File('src/demos/groovy/oauth2/server.groovy')
 server.listen()