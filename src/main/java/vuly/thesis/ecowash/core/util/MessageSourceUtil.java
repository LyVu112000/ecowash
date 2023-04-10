package vuly.thesis.ecowash.core.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import vuly.thesis.ecowash.core.constant.Common;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Locale;

@Component
public class MessageSourceUtil {

	private static ResourceBundleMessageSource staticErrorMessageSource;
	private static ResourceBundleMessageSource staticInfoMessageSource;

	private ResourceBundleMessageSource errorMessageSource;
	private ResourceBundleMessageSource infoMessageSource;

	public static String getInfoMessage(int code, List<Object> params) {
		Locale locale = LocaleContextHolder.getLocale();
		String infoMsg;

		if (params != null) {
			infoMsg = staticInfoMessageSource.getMessage(
					Common.KEY_INFO_MESSAGE + code,
					params.toArray(),
					locale);
		} else {
			infoMsg = staticInfoMessageSource.getMessage(
					Common.KEY_INFO_MESSAGE + code,
					null,
					locale);
		}

		return infoMsg;
	}

	public static String getErrorMessage(int code, List<Object> params) {
		Locale locale = LocaleContextHolder.getLocale();
		String errorMsg;

		if (params != null) {
			errorMsg = staticErrorMessageSource.getMessage(
					Common.KEY_ERROR_MESSAGE + code,
					params.toArray(),
					locale);
		} else {
			errorMsg = staticErrorMessageSource.getMessage(
					Common.KEY_ERROR_MESSAGE + code,
					null,
					locale);
		}

		return errorMsg;
	}

	@PostConstruct
	public void init() {
		staticErrorMessageSource = errorMessageSource;
		staticInfoMessageSource = infoMessageSource;
	}

	@Autowired
	public void setErrorMessageSource(ResourceBundleMessageSource errorMessageSource) {
		this.errorMessageSource = errorMessageSource;
	}

	@Autowired
	public void setInfoMessageSource(ResourceBundleMessageSource infoMessageSource) {
		this.infoMessageSource = infoMessageSource;
	}
}
