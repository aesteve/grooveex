package oauth2

import com.github.aesteve.vertx.groovy.builder.ServerBuilder
import io.vertx.groovy.core.Vertx

def builder = new ServerBuilder(vertx: Vertx.vertx)
def server = builder.buildServer new File('src/demos/groovy/oauth2/server.groovy')
server.listen()