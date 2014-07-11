package org.kt3k.gradle.plugin.coveralls
import com.squareup.okhttp.*
import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction
import org.kt3k.gradle.plugin.coveralls.domain.*
/**
 * `coveralls` task class
 */
class CoverallsTask extends DefaultTask {

	/** environmental variable */
	Map<String, String> env = [:]

	/** the logger */
	Logger logger = Logging.getLogger('coveralls-logger')

	/** source report factory mapping */
	Map<String, SourceReportFactory> sourceReportFactoryMap = [:]

	/**
	 * Posts JSON string to the url (as a multipart HTTP POST).
	 *
	 * @param json the JSON string to post
	 * @param url the url to post
	 * @param logger the logger to use
	 */
	void postJsonToUrl(String json, String url) {

        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addPart(
                    Headers.of("Content-Disposition", "form-data; name=\"json\""),
                    RequestBody.create(MediaType.parse("application/json"), json))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();

        this.logger.info response.code().toString();
        this.logger.info response.headers().toString()
        System.out << response.body().string();

	}

	/**
	 * Main procedure of `coveralls` task.
	 *
	 * @param env environmental variables
	 * @param project project to deal with
	 * @param apiEndpoint API endpoint to post resulting report
	 * @param sourceReportFactoryMap the mapping of sourceReportFactories to use
	 * @param logger the logger to use
	 */
	@TaskAction
	void coverallsAction() {

		// create service info from environmental variables
		ServiceInfo serviceInfo = ServiceInfoFactory.createFromEnvVar this.env

		if (serviceInfo == null) {
			this.logger.error 'no available CI service'

			return
		}

		this.logger.warn 'service name: ' + serviceInfo.serviceName
		this.logger.warn 'service job id: ' + serviceInfo.serviceJobId
		if (serviceInfo.repoToken != null) {
			this.logger.warn 'repo token: present (not shown for security)'
		} else {
			this.logger.warn 'repo token: null'
		}


		// add factories
		this.sourceReportFactoryMap[this.project.projectDir.path + '/' + this.project.extensions.coveralls.coberturaReportPath] = new CoberturaSourceReportFactory()
		this.sourceReportFactoryMap[this.project.projectDir.path + '/' + this.project.extensions.coveralls.jacocoReportPath] = new JacocoSourceReportFactory()


		// search the coverage file
		Map.Entry<String, SourceReportFactory> entry = this.sourceReportFactoryMap.find { Map.Entry<String, SourceReportFactory> entry ->
			String coverageFile = entry.key
			return new File(coverageFile).exists()
		}

		if (entry == null) {
			this.logger.error 'No report file available: ' + sourceReportFactoryMap.keySet()
			return
		}

		String reportFilePath = entry.key
		File reportFile = new File(reportFilePath)
		SourceReportFactory sourceReportFactory = entry.value

		this.logger.info 'Report file: ' + reportFile

		List<SourceReport> sourceReports = sourceReportFactory.createReportList project, reportFile

		// if report size is zero then do nothing
		if (sourceReports.size == 0) {
			this.logger.error 'No source file found on the project: "' + project.name + '"'
			this.logger.error 'With coverage file: ' + reportFilePath
			return
		}

		Report rep = new Report(serviceInfo, sourceReports)

		String json = rep.toJson()
		this.logger.info json
	}

}
