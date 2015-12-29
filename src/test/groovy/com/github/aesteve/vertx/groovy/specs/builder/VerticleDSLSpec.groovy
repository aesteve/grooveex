package com.github.aesteve.vertx.groovy.specs.builder

import com.github.aesteve.vertx.groovy.builder.VerticleBuilder
import com.github.aesteve.vertx.groovy.builder.VerticlesDSL
import io.vertx.groovy.core.Vertx
import io.vertx.groovy.ext.unit.TestContext
import io.vertx.groovy.ext.unit.junit.VertxUnitRunner
import org.junit.Test
import org.junit.runner.RunWith

import java.util.concurrent.atomic.AtomicInteger

@RunWith(VertxUnitRunner)
class VerticleDSLSpec {

	static AtomicInteger verticlesStarted = new AtomicInteger()

	static verticleStarted() {
		verticlesStarted.incrementAndGet()
	}

	@Test
	void createVerticles(TestContext ctx) {
		ctx.async { async ->
			VerticlesDSL dsl = new VerticleBuilder(vertx: Vertx.vertx).build new File('src/test/resources/verticles.groovy')
			assertEquals dsl.verticles.size(), 1
			def verticles = dsl.verticles.keySet()
			def vertName = verticles[0]
			assertEquals vertName, 'groovy:verticles.TestVerticle'
			assertEquals dsl.verticles[vertName], [instances: 2]
			verticlesStarted.set(0)
			dsl.start { res ->
				assertEquals 2, verticlesStarted.get()
				assertTrue res.succeeded()
				async++
			}
		}
	}

	@Test
	void createFailingVerticles(TestContext ctx) {
		ctx.async { async ->
			VerticlesDSL dsl = new VerticleBuilder(vertx: Vertx.vertx).build new File('src/test/resources/failing-verticles.groovy')
			assertEquals 2, dsl.verticles.size()
			def verticles = dsl.verticles.keySet()
			def vertName = verticles[0]
			assertEquals vertName, 'groovy:verticles.Failing'
			assertEquals dsl.verticles[vertName], [:]
			verticlesStarted.set(0)
			dsl.start { res ->
				assertFalse res.succeeded()
				assertNotNull res.cause()
				async++
			}
		}
	}
}
