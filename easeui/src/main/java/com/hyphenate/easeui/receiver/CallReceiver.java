/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hyphenate.easeui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.ui.VideoCallActivity;
import com.hyphenate.util.EMLog;

public class CallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(!EMClient.getInstance().isLoggedInBefore())
		    return;
		//username
		String from = intent.getStringExtra("from");
		//call type
		String type = intent.getStringExtra("type");
		if("video".equals(type)){ //video call
			Intent it = new Intent(context, VideoCallActivity.class);
			it.putExtra("username", from);
			it.putExtra("isComingCall", true);
			it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    context.startActivity(it);
		}
		EMLog.d("CallReceiver", "app received a incoming call");
	}

}
