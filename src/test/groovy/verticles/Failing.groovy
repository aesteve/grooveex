package verticles

import io.vertx.core.AbstractVerticle

class Failing extends AbstractVerticle {
	@Override
	public void start() {
		throw new RuntimeException("no")
	}
}
