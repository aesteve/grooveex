package com.github.aesteve.vertx.groovy.specs.dsl

import com.github.aesteve.vertx.groovy.builder.ServerDSL
import org.junit.Test

import static org.junit.Assert.assertTrue

class RoutingFilesSpec {

	@Test
	void testFilesMatch() {
		String dir = 'src/test/resources/routingfiles'
		String windowsDir = dir.replaceAll '\\/', '\\\\'
		ServerDSL dsl = new ServerDSL()
		dsl.make {
			routingFiles dir
		}
		List<String> files = dsl.routingFiles
		assertTrue files.size() > 0
		files.each {
			int dirIdx = Math.max it.indexOf(dir), it.indexOf(windowsDir)
			assertTrue dirIdx > 0 // the file is within the specified directory
			assertTrue it.indexOf('routes') > it.lastIndexOf('/') // routes is in the file name
			assertTrue it.indexOf('routes') > it.lastIndexOf('\\') // routes is in the file name (Windows)
			assertTrue it.endsWith('.groovy') // '.groovy' extension
		}
	}

}
