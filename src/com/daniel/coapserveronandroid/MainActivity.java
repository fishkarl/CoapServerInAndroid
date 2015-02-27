package com.daniel.coapserveronandroid;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import org.ws4d.coap.connection.BasicCoapChannelManager;
import org.ws4d.coap.interfaces.CoapChannelManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;




/**
 * @author Daniel
 * Run the Coap server on Android based on JCoap project For Sungman-chun
 * 2014.11.27
 * Ge Shuyuan
 */
public class MainActivity extends Activity {

	private static final int PORT = 5683;
	static int counter = 0;
	private TextView ipTV;
	private Button httpServerStartBtn, httpServerStopBtn,
			tellServerIpAddressBtn;
	public static String LOSS_WIFI_TIME = "", GET_WIFI_TIME = "";
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.e(this.getClass().getSimpleName(),"Receive ---> " + intent.getAction());
			if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())){
				Parcelable parecelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if(parecelableExtra != null){
					NetworkInfo networkInfo = (NetworkInfo)parecelableExtra;
					State state = networkInfo.getState();
					boolean isConnnected = state == State.CONNECTED;
					Log.e(this.getClass().getSimpleName(), " Wifi is Connected ---> " + isConnnected);
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
					Date curDate = new Date(System.currentTimeMillis());
					String str  = formatter.format(curDate);
					if(isConnnected){
					
						GET_WIFI_TIME = str;
						Log.e(this.getClass().getSimpleName(), "GET wifi time --> " + GET_WIFI_TIME);
						tellServerIpAddressBtn.performClick();
					}else{
						LOSS_WIFI_TIME = str;
						Log.e(this.getClass().getSimpleName(), "Loss wifi tiem --> " + LOSS_WIFI_TIME);
					}
				}
			}
		}
	};

	public static void StartCoapServer() {
		Log.i("BasicCoapServer", "Start CoAP Server on port " + PORT);
		BasicCoapServer server = new BasicCoapServer();

		CoapChannelManager channelManager = BasicCoapChannelManager
				.getInstance();
		channelManager.createServerListener(server, PORT);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log4jConfigure.configure();

		ipTV = (TextView) this.findViewById(R.id.ipText);
		try {
			ipTV.append("The IP Address of this phone is : "
					+ getLocalIPAddress() + "\n");
			getWifiAddress();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IntentFilter mFileter = new IntentFilter();
		mFileter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mFileter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		mFileter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mFileter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		registerReceiver(mReceiver, mFileter);

		httpServerStartBtn = (Button) this.findViewById(R.id.httpServerStart);
		httpServerStopBtn = (Button) this.findViewById(R.id.httpServerStop);
		tellServerIpAddressBtn = (Button) this
				.findViewById(R.id.tellServerIpAddress);
		httpServerStartBtn.setOnClickListener(onClick);
		httpServerStopBtn.setOnClickListener(onClick);
		tellServerIpAddressBtn.setOnClickListener(onClick);
	}

	View.OnClickListener onClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			switch (v.getId()) {
			case R.id.httpServerStart:

				StartCoapServer();
				break;
			case R.id.httpServerStop:

				break;
			case R.id.tellServerIpAddress:

				BasicCoapClient.tellIpAddr2Server("TestPhoneA;" + getGateWay(getApplicationContext()));

				break;

			default:
				break;
			}
		}
	};

	public static String getLocalIPAddress() throws SocketException {
		for (Enumeration<NetworkInterface> en = NetworkInterface
				.getNetworkInterfaces(); en.hasMoreElements();) {
			NetworkInterface intf = en.nextElement();
			for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
					.hasMoreElements();) {
				InetAddress inetAddress = enumIpAddr.nextElement();
				if (!inetAddress.isLoopbackAddress()
						&& (inetAddress instanceof Inet4Address)) {
					return inetAddress.getHostAddress().toString();
				}
			}
		}
		return "null";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void getWifiAddress() {
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		ipTV.append("HiddenSSID=" + info.getHiddenSSID() + "\n");
		ipTV.append("GateWayAddress=" + getGateWay(getApplicationContext())
				+ "\n");
		ipTV.append("LinkSpeed=" + info.getLinkSpeed() + "\n");
		ipTV.append("NetworkId=" + info.getNetworkId() + "\n");
		ipTV.append("Rssi=" + info.getRssi() + "\n");
		ipTV.append("SSID=" + info.getSSID() + "\n");
		ipTV.append("MacAddress=" + info.getMacAddress() + "\n");
	}

	public static String getGateWay(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();

		// dhcpInfo获取的是最后一次成功的相关信息，包括网关、ip等
		return formatIP4(dhcpInfo.gateway);
	}

	/** 将10进制整数形式转换成127.0.0.1形式的IP地址 */
	private static String formatIP4(long longIP) {
		StringBuffer sb = new StringBuffer("");
		sb.append(String.valueOf(longIP & 0x000000FF));
		sb.append(".");
		sb.append(String.valueOf((longIP & 0x0000FFFF) >>> 8));
		sb.append(".");
		sb.append(String.valueOf((longIP & 0x00FFFFFF) >>> 16));
		sb.append(".");
		sb.append(String.valueOf(longIP >>> 24));
		return sb.toString();
	}

}
