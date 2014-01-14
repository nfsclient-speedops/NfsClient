/*
 * Copyright (c) 2014 SpeedOps
 * All rights reserved.
 *
 * SpeedOps is not responsible for any use or misuse of this product.
 * In using this software you agree to hold harmless SpeedOps and any other
 * contributors to this project from any damages or liabilities which might result 
 * from its use.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.app.nfsclient;

import com.app.nfsclient.generic.GenericAlertDialog;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

public class Eula {
	private static final String TAG = "Eula";
	
	private static final String EULA_PREFIX = "eula_";
    private static final String EULA_KEY = AppState.APP_PACKAGE_NAME + "." + TAG + EULA_PREFIX;
    
	private Activity activity; 
    private Handler callbackHandler;
    private int callbackHandlerWhatAccept;
    private int callbackHandlerWhatDecline;
    private Object callbackHandlerObj;
    
	public Eula(Activity activity, Handler callbackHandler, int callbackHandlerWhatAccept,
		int callbackHandlerWhatDecline, Object callbackHandlerObj) {
		this.activity = activity;
		this.callbackHandler = callbackHandler;
		this.callbackHandlerWhatAccept = callbackHandlerWhatAccept;
		this.callbackHandlerWhatDecline = callbackHandlerWhatDecline;
		this.callbackHandlerObj = callbackHandlerObj;
	}

	public static PackageInfo packageInfoGet(Activity activity) {
		PackageInfo pi = null;
		
		try {
			pi = activity.getPackageManager().getPackageInfo(activity.getPackageName(),
				PackageManager.GET_ACTIVITIES);
		} catch (PackageManager.NameNotFoundException e) {
            AppState.log(TAG, "packageInfoGet: PackageManager.NameNotFoundException");
		}
		
		return pi;
	}

	public static String eulaKeyGet(Activity activity) {
		return EULA_KEY + packageInfoGet(activity).versionCode;
	}
	
	public void show() {
		// the eulaKey changes every time the version number in the AndroidManifest.xml is incremented
		boolean hasBeenShown = AppState.booleanGet(activity, eulaKeyGet(activity), false);
		
		// show the eula
		if (!hasBeenShown) {
			// include any updates message
			String eulaMessage = activity.getString(R.string.eulaText);
			
			final GenericAlertDialog dialog = new GenericAlertDialog(activity);
			dialog
		        .titleSet(activity.getString(R.string.aboutPreferencesEulaTitleLong))
			    .messageSet(eulaMessage)
	    	    .cancelableSet(false)
			    .positiveButtonSet(R.string.genericAcceptButtonLabel, new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// mark this version as read
					    AppState.booleanSet(activity, eulaKeyGet(activity), true);
					    dialog.dismiss();
					    Message.obtain(callbackHandler, callbackHandlerWhatAccept, callbackHandlerObj)
					        .sendToTarget();
					}
				})
			    .negativeButtonSet(R.string.genericDeclineButtonLabel, new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// mark this version as read
					    AppState.booleanSet(activity, eulaKeyGet(activity), false);
					    dialog.dismiss();
					    Message.obtain(callbackHandler, callbackHandlerWhatDecline, callbackHandlerObj)
					        .sendToTarget();
					}
				});
			dialog.show();

		} else {
			Message.obtain(callbackHandler, callbackHandlerWhatAccept, callbackHandlerObj).sendToTarget();
		}
	}
}