package tech.grasshopper.results;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import tech.grasshopper.exception.CucumberRestAssuredExtentReportPluginException;
import tech.grasshopper.logging.ReportLogger;

@Singleton
public class CucumberAllureMappingCollector {

	private ReportLogger logger;

	@Inject
	public CucumberAllureMappingCollector(ReportLogger logger) {
		this.logger = logger;
	}

	public Map<String, String> retrieveMapping(List<String> mappingFiles) {
		Map<String, String> cucumberAllureMap = new HashMap<>();

		Gson gson = new GsonBuilder().create();
		Type mapType = new TypeToken<Map<String, String>>() {
		}.getType();

		for (String mappingFile : mappingFiles) {
			try {
				cucumberAllureMap
						.putAll(gson.fromJson(Files.newBufferedReader(Paths.get(mappingFile.trim())), mapType));
			} catch (Exception e) {
				logger.warn("Skipping cucumber to allure mapping json file. Unable to access/parse  file at location - "
						+ mappingFile + " Check the 'extentreport.cucumberAllureMappingFiles' plugin configuration.");
			}
		}

		if (cucumberAllureMap.isEmpty())
			throw new CucumberRestAssuredExtentReportPluginException(
					"No cucumber to allure mapping available. Stopping report creation. Check the 'extentreport.cucumberAllureMappingFiles' plugin configuration.");

		return cucumberAllureMap;
	}
}
