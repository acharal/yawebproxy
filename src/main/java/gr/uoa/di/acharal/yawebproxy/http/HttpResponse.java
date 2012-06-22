package gr.uoa.di.acharal.yawebproxy.http;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class HttpResponse extends HttpEntity {

	public HttpResponse(HttpHeader hdr) {
		super(hdr, null);
	}

	HttpResponse(HttpHeader hdr, HttpConnection conn) {
		super(hdr, conn);
	}

	public void transferTo(HttpConnection to) throws IOException {
		HttpWriter w = to.getWriter();
		HttpResponseHeader h = (HttpResponseHeader)getHeader();
		w.writeResponseHeader(h);
		w.flush();
		
		if (to == null || !to.isConnected())
			return;

		if (h.getStatusCode().equals("200 OK")) {
		if (getLength() != null) {
			int len = getLength().intValue();
			transferContentBytes(to, len);
			//transferUntilEOF(to);
		} else {
			transferUntilEOF(to);
		}
		}
		w.flush();
	}
}
