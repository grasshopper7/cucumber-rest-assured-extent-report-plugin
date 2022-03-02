package tech.grasshopper.extent.reports;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.ReporterConfigurable;
import com.aventstack.extentreports.reporter.configuration.ViewName;

import tech.grasshopper.logging.ReportLogger;
import tech.grasshopper.pdf.extent.RestAssuredExtentPDFCucumberReporter;
import tech.grasshopper.properties.ReportProperties;

@Singleton
public class ReportInitializer {

	private ReportProperties reportProperties;

	private ReportLogger logger;

	@Inject
	public ReportInitializer(ReportProperties reportProperties, ReportLogger logger) {
		this.reportProperties = reportProperties;
		this.logger = logger;
	}

	public ExtentReports initialize() {
		ExtentReports extent = new ExtentReports();
		extent.setAnalysisStrategy(AnalysisStrategy.BDD);
		extent.setReportUsesManualConfiguration(true);

		addSystemInfoProperties(extent);

		if (reportProperties.isSparkGenerate())
			initializeSparkReport(extent);

		if (reportProperties.isPdfGenerate())
			initializePdfReport(extent);

		return extent;
	}

	private void addSystemInfoProperties(ExtentReports extent) {
		String systemInfoFilePath = reportProperties.getSystemInfoFilePath();

		if (systemInfoFilePath == null || systemInfoFilePath.indexOf('.') == -1
				|| !Files.exists(Paths.get(systemInfoFilePath)))
			return;

		Properties properties = new Properties();
		try {
			InputStream is = new FileInputStream(systemInfoFilePath);
			properties.load(is);
		} catch (IOException e) {
			logger.info("Unable to load system info properties. No system info data available.");
			return;
		}
		properties.forEach((k, v) -> extent.setSystemInfo(String.valueOf(k), String.valueOf(v)));
	}

	private ExtentSparkReporter initializeSparkReport(ExtentReports extent) {
		ExtentSparkReporter spark = new ExtentSparkReporter(
				Paths.get(reportProperties.getReportDirectory(), "SparkReport.html").toString());

		extent.attachReporter(spark);
		try {
			loadConfigFile(spark, reportProperties.getSparkConfigFilePath());
		} catch (Exception e) {
			logger.info("Unable to locate spark configuration. Creating report with default settings.");
		}
		customizeViewOrder(spark);
		return spark;
	}

	private void loadConfigFile(ReporterConfigurable report, String configFilePath) throws IOException {
		if (configFilePath == null || configFilePath.indexOf('.') == -1 || !Files.exists(Paths.get(configFilePath)))
			return;

		String configExt = configFilePath.substring(configFilePath.lastIndexOf('.') + 1);

		if (configExt.equalsIgnoreCase("xml"))
			report.loadXMLConfig(configFilePath);
		else if (configExt.equalsIgnoreCase("json"))
			report.loadJSONConfig(configFilePath);
	}

	private void customizeViewOrder(ExtentSparkReporter spark) {
		if (reportProperties.getSparkViewOrder() == null)
			return;

		try {
			List<ViewName> viewOrder = Arrays.stream(reportProperties.getSparkViewOrder().split(","))
					.map(v -> ViewName.valueOf(v.trim().toUpperCase())).collect(Collectors.toList());
			spark.viewConfigurer().viewOrder().as(viewOrder).apply();
		} catch (Exception e) {
			logger.info("Unable to customize Spark report view order. Creating report with default view order.");
		}
	}

	private RestAssuredExtentPDFCucumberReporter initializePdfReport(ExtentReports extent) {
		RestAssuredExtentPDFCucumberReporter pdf = new RestAssuredExtentPDFCucumberReporter(
				Paths.get(reportProperties.getReportDirectory(), "PdfReport.pdf").toString());

		extent.attachReporter(pdf);
		return pdf;
	}

}
