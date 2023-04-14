package vuly.thesis.ecowash.core.exception;

public class AppExceptionParser {
	public static String firstLine(Exception e) {
		StackTraceElement[] stackTrace = e.getStackTrace();
		if (stackTrace.length > 0) {
			return stackTrace[0].toString();
		} else {
			return null;
		}
	}
}
