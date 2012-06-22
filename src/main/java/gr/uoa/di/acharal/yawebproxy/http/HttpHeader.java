package gr.uoa.di.acharal.yawebproxy.http;

public class HttpHeader extends GeneralHeader {

	String version = "HTTP/1.1";
	String content = "";
	
	public String getHttpVersion()  { 
		return version;
	}

	public void setHttpVersion(String version) {
		this.version = version;
	}
	
	public String getContent() { 
		return content; 
	}
	
	public void setContent(String content) {
		this.content = content;
	}

}
