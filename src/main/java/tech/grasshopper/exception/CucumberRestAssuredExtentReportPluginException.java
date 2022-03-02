package tech.grasshopper.exception;

public class CucumberRestAssuredExtentReportPluginException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CucumberRestAssuredExtentReportPluginException(String message) {
		super(message);
	}

	public CucumberRestAssuredExtentReportPluginException(String message, Exception exception) {
		super(message, exception);
	}
}
