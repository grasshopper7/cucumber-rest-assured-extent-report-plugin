package tech.grasshopper.processor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import tech.grasshopper.extent.pojo.HttpDetails;
import tech.grasshopper.properties.ReportProperties;
import tech.grasshopper.ra.pojo.Result;

@Singleton
public class HttpDataProcessor {

	private AttachmentProcessor attachmentProcessor;
	private ReportProperties reportProperties;

	@Inject
	public HttpDataProcessor(ReportProperties reportProperties) {
		this.reportProperties = reportProperties;
	}

	public List<HttpDetails> process(List<Result> results) {
		deleteExistingAttachmentFiles();

		attachmentProcessor = AttachmentProcessor.builder()
				.allureResultsDirectory(reportProperties.getAllureResultsDirectory())
				.reportDirectory(reportProperties.getReportDirectory())
				.requestHeadersBlacklist(reportProperties.getRequestHeadersBlacklist())
				.responseHeadersBlacklist(reportProperties.getResponseHeadersBlacklist()).build();

		return results.stream().map(r -> transformResult(r)).collect(Collectors.toList());
	}

	private void deleteExistingAttachmentFiles() {
		Path path = Paths.get(reportProperties.getReportDirectory(), ReportProperties.EXTENT_REPORT_DATA_DIRECTORY);

		if (Files.exists(path))
			Arrays.stream(new File(path.toString()).listFiles()).forEach(File::delete);
	}

	private HttpDetails transformResult(Result result) {
		return HttpDetails.builder().name(result.getName()).uuid(result.getUuid())
				.dataLogs(attachmentProcessor.process(result.getAttachments())).build();
	}
}
