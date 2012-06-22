package gr.uoa.di.acharal.yawebproxy.http;

import java.io.*;
import java.net.*;

public class HttpConnection {

	private Socket sock;
	private HttpReader reader;
	private HttpWriter writer;

	private URL url;

	public HttpConnection(Socket sock) throws IOException {
		this.sock = sock;
		//reader = new HttpReader(new InputStreamReader(sock.getInputStream(), "ASCII"));
		//writer = new HttpWriter(new OutputStreamWriter(sock.getOutputStream(), "ASCII"));
		reader = new HttpReader(sock.getInputStream());
		writer = new HttpWriter(sock.getOutputStream());
	}
	
	public HttpConnection(String surl) throws IOException, MalformedURLException {
		url = new URL(surl);
		establish();
	}
	
	public void establish() throws IOException {
		if (sock == null || !sock.isConnected()) {
			int port = url.getPort();
			if (port == -1) port = 80;
			sock = new Socket(url.getHost(), port);
			reader = new HttpReader(sock.getInputStream());
			writer = new HttpWriter(sock.getOutputStream());
		}
	}
	
	public HttpReader getReader() { return reader; }
	
	public HttpWriter getWriter() { return writer; }

	public boolean isConnected() {
		return (sock != null) && (sock.isConnected());
	}

	public void close() throws IOException {
		if (sock != null)
			sock.close();
	}
	
	public HttpRequest readRequest() throws IOException, MalformedHeaderException {
		HttpRequestHeader hdr = getReader().readRequestHeader();
		return new HttpRequest(hdr, this);
	}

	public HttpResponse readResponse() throws IOException, MalformedHeaderException {
		HttpResponseHeader hdr = getReader().readResponseHeader();
		return new HttpResponse(hdr, this);
	}
}
