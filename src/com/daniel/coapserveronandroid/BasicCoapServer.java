/* Copyright [2011] [University of Rostock]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *****************************************************************************/

package com.daniel.coapserveronandroid;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.ws4d.coap.interfaces.CoapMessage;
import org.ws4d.coap.interfaces.CoapRequest;
import org.ws4d.coap.interfaces.CoapServer;
import org.ws4d.coap.interfaces.CoapServerChannel;
import org.ws4d.coap.messages.CoapMediaType;
import org.ws4d.coap.messages.CoapResponseCode;
import android.annotation.SuppressLint;
import android.util.Log;

/**
 * @author Christian Lerche <christian.lerche@uni-rostock.de>
 */

public class BasicCoapServer implements CoapServer {

	@Override
	public CoapServer onAccept(CoapRequest request) {
		Log.i("BasicCoapServer", "Accept connection...");
		return this;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onRequest(CoapServerChannel channel, CoapRequest request) {
		Log.i("BasicCoapServer", "Received message: " + request.toString()
				+ " URI: " + request.getUriPath());

		CoapMessage response = channel.createResponse(request,
				CoapResponseCode.Content_205);
		response.setContentType(CoapMediaType.text_plain);
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy.MM.dd HH:mm:ss SSS");
		Date curDate = new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);
		response.setPayload(("98")
				.getBytes());

		if (request.getObserveOption() != null) {
			Log.i("BasicCoapServer", "Client wants to observe this resource.");
		}

		response.setObserveOption(1);

		channel.sendMessage(response);
	}

	@Override
	public void onSeparateResponseFailed(CoapServerChannel channel) {
		Log.i("BasicCoapServer", "Separate response transmission failed.");

	}
}
