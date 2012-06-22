package gr.uoa.di.acharal.yawebproxy;

import gr.uoa.di.acharal.yawebproxy.http.HttpTransferable;
import gr.uoa.di.acharal.yawebproxy.http.HttpConnection;
import java.io.IOException;

public class TransferHandler implements Runnable {

	private HttpTransferable obj;
	private HttpConnection conn;
	
	public TransferHandler(HttpConnection conn, HttpTransferable obj) { 
		this.conn = conn;
		this.obj = obj;
	}
	
	public void run() { 
		try {
			obj.transferTo(conn);
		} catch (IOException e) {
			System.err.println(e);
		}
	}

}
