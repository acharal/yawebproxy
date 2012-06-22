package gr.uoa.di.acharal.yawebproxy.http;

import java.io.*;

public class HttpRequest extends HttpEntity {

	HttpRequest(HttpHeader hdr, HttpConnection conn) {
		super(hdr, conn);
	}
	
	public void transferTo(HttpConnection to) throws IOException {
		HttpWriter w = to.getWriter();
		HttpRequestHeader h = (HttpRequestHeader)getHeader();
		w.writeRequestHeader(h);
		w.flush();

		if (getConnection() == null || 
				!getConnection().isConnected())
			return;

		if (h.getMethod().equals("POST")) {
			if (getLength() != null) {
					int len = getLength().intValue();
					transferContentBytes(to, len);
			} else {
					transferUntilEOF(to);
			}
		}
	}
	
}
