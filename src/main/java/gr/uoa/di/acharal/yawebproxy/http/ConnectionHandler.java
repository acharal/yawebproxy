package gr.uoa.di.acharal.yawebproxy.http;

import java.net.MalformedURLException;
import java.io.IOException;

public interface ConnectionHandler {

	public HttpConnection getConnection(String url) throws IOException, MalformedURLException;

	public void releaseConnection(HttpConnection conn) throws IOException;
}
