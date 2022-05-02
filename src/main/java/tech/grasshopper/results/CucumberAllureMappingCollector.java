package tech.grasshopper.results;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import tech.grasshopper.exception.CucumberRestAssuredExtentReportPluginException;

@Singleton
public class CucumberAllureMappingCollector {

	public Map<String, String> retrieveMapping(String mappingFile) {
		Map<String, String> cucumberAllureMap = new HashMap<>();

		Gson gson = new GsonBuilder().create();
		try {
			Type mapType = new TypeToken<Map<String, String>>() {
			}.getType();
			cucumberAllureMap = gson.fromJson(Files.newBufferedReader(Paths.get(mappingFile)), mapType);
		} catch (IOException e) {
			throw new CucumberRestAssuredExtentReportPluginException(
					"Unable to access/parse cucumber to allure mapping json file. Stopping report creation. Check the 'extentreport.cucumberAllureMappingFile' plugin configuration.");
		}
		return cucumberAllureMap;
	}
}
