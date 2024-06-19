/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.common;

import static org.nebobrod.schulteplus.Utils.showSnackBarConfirmation;

import android.app.Activity;
import android.view.View;

import java.util.LinkedList;
import java.util.Queue;

public class SnackBarManager {

	private Queue<MessageItem> messageQueue = new LinkedList<>();
	private boolean isShowingMessage = false;
	private Activity activity;

	public SnackBarManager(Activity activity) {
		this.activity = activity;
	}

	public void queueMessage(String message, View.OnClickListener onClickListener) {
		messageQueue.add(new MessageItem(message, onClickListener));
		if (!isShowingMessage) {
			showMessageFromQueue();
		}
	}

	private void showMessageFromQueue() {
		if (!messageQueue.isEmpty()) {
			isShowingMessage = true;
			MessageItem item = messageQueue.poll();
			showSnackBarConfirmation(activity, item.message, view -> {
				if (item.onClickListener != null) {
					item.onClickListener.onClick(view);
				}
				isShowingMessage = false;
				showMessageFromQueue();
			});
		} else {
			isShowingMessage = false;
		}
	}


	private static class MessageItem {
		String message;
		View.OnClickListener onClickListener;

		MessageItem(String message, View.OnClickListener onClickListener) {
			this.message = message;
			this.onClickListener = onClickListener;
		}
	}
}
