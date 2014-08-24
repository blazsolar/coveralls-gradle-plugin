package com.github.blazsolar.gradle.coveralls.domain

import org.junit.Test
import static org.junit.Assert.*

class SourceReportTest {

	@Test
	void testConstructor() {
		SourceReport sourceReport = new SourceReport('test.java', 'class A {\n  String a = "a";\n}', [1, 0, null])

		assertNotNull sourceReport

		assertEquals 'test.java', sourceReport.name
		assertEquals 'class A {\n  String a = "a";\n}', sourceReport.source
		assertEquals([1, 0, null], sourceReport.coverage)
	}

}
