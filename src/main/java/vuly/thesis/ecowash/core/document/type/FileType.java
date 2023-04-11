package vuly.thesis.ecowash.core.document.type;

public enum FileType {
	PDF("PDF"),
	XLSX("XLSX");
	private String code;

	FileType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
