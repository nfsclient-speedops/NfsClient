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

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

import com.app.nfsclient.generic.GenericPreferencesActivity;
import com.app.nfsclient.generic.GenericTextShowFragmentActivity;
import com.app.nfsclient.servers.ServersFragmentActivity;

public class App extends GenericPreferencesActivity {
    private static final String TAG = "App";
        	
	public static final String DATA_CACHE_ENABLE_KEY = AppState.APP_PACKAGE_NAME + "." + TAG +
	    ".dataCacheEnableKey";
	public static final boolean DEFAULT_CACHE_ENABLE = true;

	public static final String COLOR_THEME_KEY = AppState.APP_PACKAGE_NAME +  "." + TAG + ".colorThemeKey";

	private String serversPreferenceKey;
	private Preference serversPreference;
	
	private String aboutPreferenceKey;
	private Preference aboutPreference;
	    
    private static ProgressDialog progressDialog = null;
  
    @Override
    public void dataInit() {
    	// set the window layout
    	contentViewResId = R.layout.generic_preferences_layout_no_buttons;
    	preferencesLayout = R.layout.app_preferences;
    	
    	// set the sql data cache size
    	Utils.defaultSqlCacheSizeSet(0);
    	
    	// initialize the app state
    	AppState.stateInit(this);
    }
    
    @Override
    public void viewsInit() {
    	super.viewsInit();
    	AppState.androidLogX(TAG, "viewsInit");

    	// the servers preference
    	serversPreferenceKey = getString(R.string.appPreferencesNfsServersKey);
    	serversPreference = (Preference)findPreference(serversPreferenceKey);
    	serversPreference.setIntent(new Intent(this, ServersFragmentActivity.class));
    			
		// the about preference
		aboutPreferenceKey = getString(R.string.appPreferencesAboutKey);
		aboutPreference = (Preference)findPreference(aboutPreferenceKey);
		aboutPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
		        Intent intent = new Intent(App.this, GenericTextShowFragmentActivity.class);
		    	intent.putExtra(GenericTextShowFragmentActivity.WINDOW_TITLE_KEY, getString(
		    		R.string.appPreferencesAboutTitle));
		    	intent.putExtra(GenericTextShowFragmentActivity.WINDOW_TEXT_STRING_KEY, getString(
		    		R.string.copyrightsText));
		    	startActivity(intent);
				return false;
			}
			
		});
    }
    
    @Override
    protected void init() {
    	// display the eula
        new Eula(this, startupHandler, EULA_ACCEPT, EULA_DECLINE, this).show();
    }
    
    public static final int EULA_ACCEPT = 0;
    public static final int EULA_DECLINE = 1;
    public static final Handler startupHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	AppState.androidLogX(TAG, "startupHandler");
        	
        	final App activity = (App)msg.obj;
            switch(msg.what) {
            case EULA_ACCEPT:
            	startupInit(activity);
            	break;
            case EULA_DECLINE:
                activity.finish();
                break;
            }
        }
    };
    
    private static void startupInit(App activity) {
        activity.dataInit();
        activity.viewsInit();
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	AppState.androidLogX(TAG, "onSaveInstanceState");
    	super.onSaveInstanceState(savedInstanceState);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	AppState.androidLogX(TAG, "onCreateDialog");

    	progressDialog = new ProgressDialog(this);
    	    	
    	progressDialog.setTitle(Utils.EMPTY_STRING);
    	progressDialog.setIndeterminate(true);
    	progressDialog.setCancelable(true);
        
        switch(id) {
        default:
        	progressDialog = null;
        }
        
        return progressDialog;
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        AppState.logX(TAG, String.format("onActivityResult: requestCode = 0x%x", requestCode));
		
        switch(requestCode) {
        default:
       		break;
        }
	}
	
    @Override
	public void onBackPressed() {
		super.onBackPressed();

		AppState.androidLogX(TAG, "onBackPressed");

        finish();
	}
    
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	}
}