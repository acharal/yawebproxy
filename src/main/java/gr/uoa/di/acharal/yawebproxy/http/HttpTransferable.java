package gr.uoa.di.acharal.yawebproxy.http;

import java.io.IOException;

public interface HttpTransferable {

	public void transferTo(HttpConnection con) throws IOException;

}
