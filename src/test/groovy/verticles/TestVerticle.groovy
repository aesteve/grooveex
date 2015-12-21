package verticles

import com.github.aesteve.vertx.groovy.specs.builder.VerticleDSLSpec
import io.vertx.core.AbstractVerticle

class TestVerticle extends AbstractVerticle {

	@Override
	void start() {
		VerticleDSLSpec.verticleStarted()
	}
}
