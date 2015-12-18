package com.github.aesteve.vertx.groovy.specs

import io.vertx.core.DeploymentOptions
import io.vertx.core.json.JsonObject
import org.junit.Test

class MapSpec {

	@Test
	public void deploymentOptions() {
		DeploymentOptions options = new DeploymentOptions()
		def config = new JsonObject()
		def ha = true
		def instances = 5
		def isolationGroup = "isolation"
		def multi = true
		def worker = true
		def extraCp = ["test"]
		def isolated = ["classes"]
		options.setConfig config
		options.setHa ha
		options.setInstances instances
		options.setIsolationGroup isolationGroup
		options.setMultiThreaded multi
		options.setWorker worker
		options.setExtraClasspath extraCp
		options.setIsolatedClasses isolated

		def map = [
			config         : config,
			ha             : ha,
			instances      : instances,
			isolationGroup : isolationGroup,
			multiThreaded  : multi,
			worker         : worker,
			extraClasspath : extraCp,
			isolatedClasses: isolated
		]
		DeploymentOptions created = map as DeploymentOptions
		assert created == options
	}

}
