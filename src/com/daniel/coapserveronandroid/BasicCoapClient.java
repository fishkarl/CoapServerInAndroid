package com.daniel.coapserveronandroid;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ws4d.coap.Constants;
import org.ws4d.coap.connection.BasicCoapChannelManager;
import org.ws4d.coap.interfaces.CoapChannelManager;
import org.ws4d.coap.interfaces.CoapClient;
import org.ws4d.coap.interfaces.CoapClientChannel;
import org.ws4d.coap.interfaces.CoapRequest;
import org.ws4d.coap.interfaces.CoapResponse;
import org.ws4d.coap.messages.CoapEmptyMessage;
import org.ws4d.coap.messages.CoapMediaType;
import org.ws4d.coap.messages.CoapRequestCode;

import android.util.Log;

/**
 * @author Christian Lerche <christian.lerche@uni-rostock.de>
 */

public class BasicCoapClient implements CoapClient {
    private static final String SERVER_ADDRESS = "155.230.18.189";
    private static final int PORT = Constants.COAP_DEFAULT_PORT;
    static int counter = 0;
    CoapChannelManager channelManager = null;
    CoapClientChannel clientChannel = null;

    public  static void tellIpAddr2Server(String ipAddress) {
        System.out.println("Tell Ip address to server");
        SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy.MM.dd HH:mm:ss SSS");
		Date curDate = new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);
        Log.e("BasicCoapClient", "Time of sending address ---> " + str);
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
					Date curDate = new Date(System.currentTimeMillis());
					String str  = formatter.format(curDate);
					Log.e("BasicCoapClient", "Current time in mobile ---> " + str);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
        BasicCoapClient client = new BasicCoapClient();
        client.channelManager = BasicCoapChannelManager.getInstance();
        client.runTestClient(ipAddress);
    }
    
    public void runTestClient(String ipAddress){
    	try {
			clientChannel = channelManager.connect(this, InetAddress.getByName(SERVER_ADDRESS), PORT);
			CoapRequest coapRequest = clientChannel.createRequest(true, CoapRequestCode.PUT);
			coapRequest.setContentType(CoapMediaType.octet_stream);
//			coapRequest.setToken("ABCD".getBytes());
//			coapRequest.setUriHost("123.123.123.123");
//			coapRequest.setUriPort(1234);
			coapRequest.setUriPath("/newIpAddress");
			coapRequest.setPayload(ipAddress);
//			coapRequest.setUriQuery("a=1&b=2&c=3");
//			coapRequest.setProxyUri("http://proxy.org:1234/proxytest");
			clientChannel.sendMessage(coapRequest);
			System.out.println("Sent Request");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} 
    }

	@Override
	public void onConnectionFailed(CoapClientChannel channel, boolean notReachable, boolean resetByServer) {
		System.out.println("Connection Failed");
	}

	@Override
	public void onResponse(CoapClientChannel channel, CoapResponse response) {
		System.out.println("Received response : " + response.toString());
	}
}
