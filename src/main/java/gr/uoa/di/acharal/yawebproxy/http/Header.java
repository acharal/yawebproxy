package gr.uoa.di.acharal.yawebproxy.http;

public class Header {

	private String type;
	private String value;
	
	public Header(String type, String value) {
		this.type = type;
		this.value = value;
	}
	
	public String getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
}
