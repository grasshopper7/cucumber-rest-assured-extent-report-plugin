package tech.grasshopper.results;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import tech.grasshopper.exception.CucumberRestAssuredExtentReportPluginException;
import tech.grasshopper.logging.ReportLogger;
import tech.grasshopper.pojo.Feature;
import tech.grasshopper.pojo.Result;
import tech.grasshopper.processor.deserializer.ResultDeserializer;

@Singleton
public class CucumberResultsCollector {

	private ReportLogger logger;

	@Inject
	public CucumberResultsCollector(ReportLogger logger) {
		this.logger = logger;
	}

	public List<Feature> retrieveFeatures(String jsonDirectory) {
		List<Path> jsonFilePaths = retrievePaths(jsonDirectory);
		Gson gson = new GsonBuilder().registerTypeAdapter(Result.class, new ResultDeserializer()).create();

		List<Feature> features = new ArrayList<>();
		Feature[] parsedFeatures = null;

		for (Path jsonFilePath : jsonFilePaths) {

			try {
				parsedFeatures = gson.fromJson(Files.newBufferedReader(jsonFilePath), Feature[].class);
			} catch (JsonSyntaxException | JsonIOException | IOException e) {
				logger.warn(String.format(
						"Skipping json report at '%s', as unable to parse json report file to Feature pojo.",
						jsonFilePath));
				continue;
			}

			if (parsedFeatures == null || parsedFeatures.length == 0) {
				logger.warn(String.format(
						"Skipping json report at '%s', parsing json report file returned no Feature pojo.",
						jsonFilePath));
				continue;
			}
			features.addAll(Arrays.asList(parsedFeatures));
		}

		if (features.size() == 0)
			throw new CucumberRestAssuredExtentReportPluginException(
					"No Feature found in report. Stopping report creation. "
							+ "Check the 'extentreport.cucumberJsonReportDirectory' plugin configuration.");

		if (!features.stream().flatMap(f -> f.getElements().stream())
				.filter(s -> !s.getKeyword().equalsIgnoreCase("Background")
						&& (s.getStartTimestamp() == null || s.getStartTimestamp().isEmpty()))
				.collect(Collectors.toList()).isEmpty())
			throw new CucumberRestAssuredExtentReportPluginException(
					"Start timestamp data of scenario is essential but is missing in json report. "
							+ "Plugin only generates report for Cucumber-JVM 4.3.0 and above. "
							+ "If Cucumber version is in the valid range, do submit an issue.");
		return features;
	}

	private List<Path> retrievePaths(String jsonDirectory) {
		List<Path> jsonFilePaths = null;
		try {
			jsonFilePaths = Files.walk(Paths.get(jsonDirectory)).filter(Files::isRegularFile)
					.filter(this::filterJsonFileName).collect(Collectors.toList());
		} catch (IOException e) {
			throw new CucumberRestAssuredExtentReportPluginException(
					"Unable to navigate Cucumber Json report folders. Stopping report creation. "
							+ "Check the 'extentreport.cucumberJsonReportDirectory' plugin configuration.");
		}
		if (jsonFilePaths == null || jsonFilePaths.size() == 0)
			throw new CucumberRestAssuredExtentReportPluginException(
					"No Cucumber Json Report found. Stopping report creation. "
							+ "Check the 'extentreport.cucumberJsonReportDirectory' plugin configuration.");
		return jsonFilePaths;
	}

	private boolean filterJsonFileName(Path path) {
		String pathStr = path.toString().toLowerCase();

		if (pathStr.endsWith(".json") && !(pathStr.endsWith("-result.json") || pathStr.endsWith("-attachment.json")))
			return true;
		return false;
	}
}
