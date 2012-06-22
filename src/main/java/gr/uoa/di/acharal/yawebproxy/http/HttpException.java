package gr.uoa.di.acharal.yawebproxy.http;

public class HttpException extends Exception {

	HttpResponse  response;

	public HttpException(HttpResponse response) {
		this.response = response;
	}
	
	public HttpResponse getResponse() { 
		return response;
	}

}
