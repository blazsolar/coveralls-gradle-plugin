package com.github.blazsolar.gradle.coveralls.domain

import org.gradle.api.Project

/**
 * The interface for factory classes of SourceReport.
 */
interface SourceReportFactory {
	List<SourceReport> createReportList(Project project, File file)
}
