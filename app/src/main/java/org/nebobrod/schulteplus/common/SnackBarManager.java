/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.common;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import org.nebobrod.schulteplus.R;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SnackBarManager {
	private static final String TAG = "SplashViewModel";

	private Queue<MessageItem> messageQueue = new LinkedList<>();
	private boolean isPostponed = false;
	private boolean isShowingMessage = false;
	private Activity activity;
	private ExecutorService executorService;

	public SnackBarManager(Activity activity) {
		this.activity = activity;
		this.executorService = Executors.newSingleThreadExecutor();
	}

	public boolean isPostponed() {
		return isPostponed;
	}

	public SnackBarManager setPostponed(boolean postponed) {
		isPostponed = postponed;
		if (!isPostponed) {
			showAllQueue(null);
		}
		return this; // Return the current instance for chaining
	}

	public void showAllQueue(OnCompleteListener listener) {
		executorService.execute(() -> {
			Log.i(TAG, "showAllQueue, execute, isPostponed: " + isPostponed);
			while (!messageQueue.isEmpty() && !isPostponed) {
				showMessageFromQueue();
				// Pause to allow Snackbar to be displayed properly
				try {
					Thread.sleep(500); // Adjust sleep duration as needed
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (listener != null) {
				listener.onComplete();
			}
		});
	}

	public Queue<MessageItem> getMessageQueue() {
		return messageQueue;
	}

	private static class MessageItem {
		String message;
		View.OnClickListener onClickListener;

		MessageItem(String message, View.OnClickListener onClickListener) {
			this.message = message;
			this.onClickListener = onClickListener;
		}

		@Override
		public String toString() {
			return "MessageItem{" +
					"message='" + message + '\'' +
					", onClickListener=" + onClickListener +
					'}';
		}
	}

	public void queueMessage(String message, View.OnClickListener onClickListener) {
		messageQueue.add(new MessageItem(message, onClickListener));
		if (!isShowingMessage && !isPostponed) {
			showMessageFromQueue();
		}
	}

	private void showMessageFromQueue() {
		Log.i(TAG, "showMessageFromQueue, isPostponed: " + isPostponed + " isShowingMessage:" + isShowingMessage);
		if (isPostponed || isShowingMessage) {
			return;
		}

		if (!messageQueue.isEmpty()) {
			isShowingMessage = true;
			MessageItem item = messageQueue.poll();
			Log.i(TAG, "showMessageFromQueue: " + item);

			activity.runOnUiThread(() -> {
				showSnackBarConfirmation(activity, item.message, view -> {
					if (item.onClickListener != null) {
						item.onClickListener.onClick(view);
					}
					isShowingMessage = false;
					showMessageFromQueue();
				});
			});
		} else {
			isShowingMessage = false;
		}
	}

	public static void showSnackBarConfirmation(Activity activity, String message, @Nullable View.OnClickListener okListener) {
		View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
		if (rootView == null) {
			Log.w("SnackBarManager", "showSnackBarConfirmation: Root view is null");
			return;
		}
		if (!rootView.isShown()) {
			Log.w("showSnackBarConfirmation", "Root view is not shown");
			return;
		}

		Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_INDEFINITE);

		/*if (okListener == null) {
			okListener = view -> snackbar.dismiss();
		}*/
		snackbar.setAction(activity.getString(R.string.lbl_ok), okListener);

		View snackbarView = snackbar.getView();
		TextView tv = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
		tv.setMaxLines(7);

		snackbar.show();
		Log.d(TAG, "Snackbar shown: " + message.substring(15) + " !!! " + snackbar.isShownOrQueued());
	}

	public interface OnCompleteListener {
		void onComplete();
	}
}
