package gr.uoa.di.acharal.yawebproxy.http;

import java.net.*;

public class HttpRequestHeader extends HttpHeader {

	private String url;
	private String method;

	public HttpRequestHeader() { 
		
	}
	
	public String getURL() {
		try {
			URL url2 = new URL(url);
			return url2.toString();
		}catch (MalformedURLException e) {
			String host = getHeader("Host");
			if (host == null)
				return url;
			else
				return "http://"+host+"/"+url;
		}
	}
	
	public String getMethod() {
		return method;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}
	
	public void setURL(String url) {
		this.url = url;
	}
}
