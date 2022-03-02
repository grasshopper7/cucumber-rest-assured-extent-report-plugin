package tech.grasshopper.processor;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import tech.grasshopper.extent.pojo.HttpDetails;
import tech.grasshopper.pojo.Feature;
import tech.grasshopper.pojo.Scenario;

@Singleton
public class CucumberAllureDataProcessor {

	public void process(List<Feature> features, List<HttpDetails> httpDetailsData, Map<String, String> mapping) {

		Map<String, HttpDetails> uuidToHttpDetails = httpDetailsData.stream()
				.collect(Collectors.toMap(HttpDetails::getUuid, Function.identity()));

		for (Feature feature : features) {
			String uri = feature.getUri();

			for (Scenario scenario : feature.getElements()) {
				int line = scenario.getLine();
				String uriLine = uri + ":" + line;

				if (mapping.containsKey(uriLine)) {
					String uuid = mapping.get(uriLine);
					scenario.addInfo("Rest Assured", uuidToHttpDetails.get(uuid).convertHttpDetails());
				}
			}
		}
	}
}
