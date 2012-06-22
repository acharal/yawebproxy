package gr.uoa.di.acharal.yawebproxy.http;

public class HttpResponseHeader extends HttpHeader {
	
	private String statusCode;
	
	public String getStatusCode() { return statusCode; }
	
	public void setStatusCode(String code) {
		statusCode = code;
	}

	public String getFirstLine() { 
		return getStatusLine();
	}

	public String getStatusLine() {
		return getHttpVersion()+" "+
		       getStatusCode();
	}
	
}
