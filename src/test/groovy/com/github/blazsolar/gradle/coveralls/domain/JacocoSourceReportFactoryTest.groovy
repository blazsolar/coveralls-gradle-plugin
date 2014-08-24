package com.github.blazsolar.gradle.coveralls.domain

import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.scala.ScalaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import org.junit.Before
import com.github.blazsolar.gradle.CoverallsPluginExtension

import static org.junit.Assert.*

class JacocoSourceReportFactoryTest {

	Project project

	@Before
	public void setUp() {

		// fake a project
		project = ProjectBuilder.builder().build()

		// create coveralls extension
		project.extensions.create('coveralls', CoverallsPluginExtension)
	}

	@Test
	public void testCreateReport() throws Exception {
		// test with single (not multi) project jacoco report
		List<SourceReport> reports = JacocoSourceReportFactory.createReportList([new File('src/test/fixture')], new File('src/test/fixture/jacocoTestReport.xml'))

		assertNotNull reports

		assertEquals 6, reports.size()

		// sort reports for assertion
		reports.sort { it.name }

		assertEquals 'src/test/fixture/org/kt3k/gradle/plugin/CoverallsPlugin.groovy', reports[0].name
		assertEquals 'src/test/fixture/org/kt3k/gradle/plugin/coveralls/Application.groovy', reports[1].name
		assertEquals 'src/test/fixture/org/kt3k/gradle/plugin/coveralls/domain/Report.groovy', reports[2].name
		assertEquals 'src/test/fixture/org/kt3k/gradle/plugin/coveralls/domain/ServiceInfo.groovy', reports[3].name
		assertEquals 'src/test/fixture/org/kt3k/gradle/plugin/coveralls/domain/ServiceInfoFactory.groovy', reports[4].name
		assertEquals 'src/test/fixture/org/kt3k/gradle/plugin/coveralls/domain/SourceReport.groovy', reports[5].name
	}


	@Test
	public void testCreateReportWithMultiProjectReport() {
		// test with multi-project jacoco report
		List<SourceReport> reports = JacocoSourceReportFactory.createReportList([new File('src/test/fixture')], new File('src/test/fixture/jacocoReportWithMultipleGroups.xml'))

		assertNotNull reports

		assertEquals 2, reports.size()

		// sort reports for assertion
		reports.sort { it.name }

		assertEquals 'src/test/fixture/org/kt3k/gradle/plugin/CoverallsPlugin.groovy', reports[0].name
		assertEquals 'src/test/fixture/org/kt3k/gradle/plugin/coveralls/Application.groovy', reports[1].name
	}


	@Test
	public void testCreateReportList() throws Exception {
		project.plugins.apply(GroovyPlugin)

		List<SourceReport> reports = new JacocoSourceReportFactory().createReportList(project, new File('src/test/fixture/jacocoTestReport.xml'))

		assertNotNull(reports)
	}


	@Test
	public void testCreateReportForJavaPlugin() throws Exception {

		project.plugins.apply(JavaPlugin)

		List<File> srcDirs = JacocoSourceReportFactory.createTargetSrcDirs(project)

		srcDirs.each {
			assertTrue it.absolutePath.endsWith("src/main/java")
		}
	}


	@Test
	public void testCreateReportForGroovyPlugin() throws Exception {

		project.plugins.apply(GroovyPlugin)

		List<File> srcDirs = JacocoSourceReportFactory.createTargetSrcDirs(project)

		srcDirs.each {
			assertTrue it.absolutePath.endsWith("src/main/java") || it.absolutePath.endsWith('src/main/groovy')
		}
	}


	@Test
	public void testCreateReportForScalaPlugin() throws Exception {

		project.plugins.apply(ScalaPlugin)

		List<File> srcDirs = JacocoSourceReportFactory.createTargetSrcDirs(project)

		srcDirs.each {
			assertTrue it.absolutePath.endsWith("src/main/java") || it.absolutePath.endsWith('src/main/scala')
		}
	}
}
