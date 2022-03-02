package tech.grasshopper.extent.pojo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.grasshopper.pojo.HttpLogData;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HttpDetails {

	private String name;

	private String uuid;

	@Default
	private List<HttpLogData> dataLogs = new ArrayList<>();

	public List<Map<String, String>> convertHttpDetails() {
		List<Map<String, String>> data = new ArrayList<>();

		for (HttpLogData log : dataLogs) {
			Map<String, String> details = new LinkedHashMap<>();

			log.getHttpRequestData().addPropertiesDisplay(details);
			log.getHttpResponseData().addPropertiesDisplay(details);
			log.getHttpRequestData().addHttpContentFilesDisplay(details);
			log.getHttpResponseData().addHttpContentFilesDisplay(details);

			log.getHttpRequestData().addHeadersContentFileLink("Request", details);
			log.getHttpRequestData().addCookiesContentFileLink("Request", details);
			log.getHttpRequestData().addBodyContentFileLink("Request", details);

			log.getHttpResponseData().addHeadersContentFileLink("Response", details);
			log.getHttpResponseData().addCookiesContentFileLink("Response", details);
			log.getHttpResponseData().addBodyContentFileLink("Response", details);

			data.add(details);
		}
		return data;
	}
}
