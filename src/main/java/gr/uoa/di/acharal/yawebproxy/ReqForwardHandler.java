package gr.uoa.di.acharal.yawebproxy;

import java.net.Socket;

/** 
 * Forwards the requests. Strong-validation for the cached objects.
 * @author angel
 *
 */
public class ReqForwardHandler extends ClientSocketHandler {

	public ReqForwardHandler(HttpProxy proxy, Socket socket) {
		super(proxy, socket);
	}
	
}
