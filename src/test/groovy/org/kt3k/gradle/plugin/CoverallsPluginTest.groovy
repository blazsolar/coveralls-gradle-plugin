package org.kt3k.gradle.plugin

import com.github.blazsolar.gradle.CoverallsPlugin
import org.junit.Test
import static org.junit.Assert.*

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder

import com.github.blazsolar.gradle.coveralls.CoverallsTask

class CoverallsPluginTest {

	@Test
	void testApply() {

		// build dummy project
		Project project = ProjectBuilder.builder().build()

		new CoverallsPlugin().apply(project)

		Task task = project.tasks.getByName('coveralls')

		assertTrue(task instanceof CoverallsTask)
	}

}
