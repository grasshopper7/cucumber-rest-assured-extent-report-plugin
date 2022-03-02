package tech.grasshopper.extent.reports;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.aventstack.extentreports.ExtentReports;

import tech.grasshopper.pojo.Feature;
import tech.grasshopper.processor.AdditionalInformationProcessor.DefaultAddInfoProcessor;
import tech.grasshopper.tests.ExtentTestHeirarchy;

@Singleton
public class ReportCreator {

	private ReportInitializer reportInitializer;

	@Inject
	public ReportCreator(ReportInitializer reportInitializer) {
		this.reportInitializer = reportInitializer;
	}

	public void generate(List<Feature> features) {
		ExtentReports extent = reportInitializer.initialize();

		ExtentTestHeirarchy.builder().extent(extent).features(features)
				.scenarioAddInfoProcessor(DefaultAddInfoProcessor.builder().build()).build().createTestHeirarchy();

		extent.flush();
	}
}
