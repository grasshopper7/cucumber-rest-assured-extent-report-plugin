package tech.grasshopper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import tech.grasshopper.exception.CucumberRestAssuredExtentReportPluginException;
import tech.grasshopper.extent.pojo.HttpDetails;
import tech.grasshopper.extent.reports.ReportCreator;
import tech.grasshopper.logging.ReportLogger;
import tech.grasshopper.pojo.Feature;
import tech.grasshopper.processor.CucumberAllureDataProcessor;
import tech.grasshopper.processor.HttpDataProcessor;
import tech.grasshopper.properties.ReportProperties;
import tech.grasshopper.ra.pojo.Result;
import tech.grasshopper.results.AllureResultsCollector;
import tech.grasshopper.results.CucumberAllureMappingCollector;
import tech.grasshopper.results.CucumberResultsCollector;

@Mojo(name = "extentreport")
public class CucumberRestAssuredExtentReportPlugin extends AbstractMojo {

	@Parameter(property = "extentreport.cucumberReportsDirectory", defaultValue = ReportProperties.CUCUMBER_REPORTS_DIRECTORY)
	private String cucumberReportsDirectory;

	@Parameter(property = "extentreport.allureResultsDirectory", defaultValue = ReportProperties.ALLURE_RESULTS_DIRECTORY)
	private String allureResultsDirectory;

	@Parameter(property = "extentreport.cucumberAllureMappingFile", defaultValue = ReportProperties.CUCUMBER_ALLURE_MAPPING_FILE)
	private String cucumberAllureMappingFile;

	@Parameter(property = "extentreport.reportDirectory", defaultValue = ReportProperties.REPORT_DIRECTORY)
	private String reportDirectory;

	@Parameter(property = "extentreport.reportDirectoryTimeStamp", defaultValue = ReportProperties.REPORT_DIRECTORY_TIMESTAMP)
	private String reportDirectoryTimeStamp;

	@Parameter(property = "extentreport.sparkGenerate", defaultValue = ReportProperties.SPARK_REPORT_GENERATE)
	private boolean sparkGenerate;

	@Parameter(property = "extentreport.sparkConfigFilePath", defaultValue = ReportProperties.SPARK_REPORT_CONFIG_FILE)
	private String sparkConfigFilePath;

	@Parameter(property = "extentreport.pdfGenerate", defaultValue = ReportProperties.PDF_REPORT_GENERATE)
	private boolean pdfGenerate;

	@Parameter(property = "extentreport.pdfConfigFilePath", defaultValue = ReportProperties.PDF_REPORT_CONFIG_FILE)
	private String pdfConfigFilePath;

	@Parameter(property = "extentreport.systemInfoFilePath", defaultValue = ReportProperties.REPORT_SYSTEM_INFO_FILE)
	private String systemInfoFilePath;

	@Parameter(property = "extentreport.sparkViewOrder")
	private String sparkViewOrder;

	private CucumberResultsCollector cucumberResultsCollector;
	private AllureResultsCollector allureResultsCollector;
	private CucumberAllureMappingCollector cucumberAllureMappingCollector;
	private CucumberAllureDataProcessor cucumberAllureDataProcessor;
	private HttpDataProcessor httpDataProcessor;
	private ReportCreator reportCreator;
	private ReportProperties reportProperties;

	private ReportLogger logger;

	@Inject
	public CucumberRestAssuredExtentReportPlugin(CucumberResultsCollector cucumberResultsCollector,
			AllureResultsCollector allureResultsCollector,
			CucumberAllureMappingCollector cucumberAllureMappingCollector,
			CucumberAllureDataProcessor cucumberAllureDataProcessor, HttpDataProcessor httpDataProcessor,
			ReportCreator reportCreator, ReportProperties reportProperties, ReportLogger logger) {

		this.cucumberResultsCollector = cucumberResultsCollector;
		this.allureResultsCollector = allureResultsCollector;
		this.cucumberAllureMappingCollector = cucumberAllureMappingCollector;
		this.cucumberAllureDataProcessor = cucumberAllureDataProcessor;
		this.httpDataProcessor = httpDataProcessor;
		this.reportCreator = reportCreator;
		this.reportProperties = reportProperties;
		this.logger = logger;
	}

	public void execute() {
		try {

			logger.initializeLogger(getLog());
			logger.info("STARTING EXTENT REPORT GENERATION");

			setReportProperties();
			if (!reportProperties.isSparkGenerate() && !reportProperties.isPdfGenerate()) {
				logger.info("STOPPING EXTENT REPORT GENERATION - No report type selected.");
				return;
			}
			createAttachmentFolder();

			List<Feature> features = cucumberResultsCollector
					.retrieveFeatures(reportProperties.getCucumberReportsDirectory());
			List<Result> results = allureResultsCollector.retrieveResults(reportProperties.getAllureResultsDirectory());
			List<HttpDetails> httpDetailsData = httpDataProcessor.process(results);
			Map<String, String> mapping = cucumberAllureMappingCollector.retrieveMapping(cucumberAllureMappingFile);

			cucumberAllureDataProcessor.process(features, httpDetailsData, mapping);

			reportCreator.generate(features);

			logger.info("EXTENT REPORT SUCCESSFULLY GENERATED");
		} catch (Throwable t) {
			// Report will not result in build failure.
			t.printStackTrace();
			logger.error(String.format("STOPPING EXTENT REPORT GENERATION - %s", t.getMessage()));
		}
	}

	private void setReportProperties() {
		reportProperties.setCucumberReportsDirectory(cucumberReportsDirectory);
		reportProperties.setAllureResultsDirectory(allureResultsDirectory);
		reportProperties.setReportDirectory(reportDirectory, reportDirectoryTimeStamp);
		reportProperties.setSystemInfoFilePath(systemInfoFilePath);

		reportProperties.setSparkGenerate(sparkGenerate);
		reportProperties.setSparkConfigFilePath(sparkConfigFilePath);
		reportProperties.setSparkViewOrder(sparkViewOrder);

		reportProperties.setPdfGenerate(pdfGenerate);
		reportProperties.setPdfConfigFilePath(pdfConfigFilePath);
	}

	private void createAttachmentFolder() {
		try {
			Files.createDirectories(
					Paths.get(reportProperties.getReportDirectory(), ReportProperties.EXTENT_REPORT_DATA_DIRECTORY));
		} catch (IOException e) {
			throw new CucumberRestAssuredExtentReportPluginException("Unable to create report attachments directory.", e);
		}
	}
}
