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

package com.app.nfsclient.generic;

import java.lang.reflect.Field;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import com.app.nfsclient.App;
import com.app.nfsclient.AppState;
import com.app.nfsclient.Background;
import com.app.nfsclient.R;

public abstract class GenericPreferencesActivity extends SherlockPreferenceActivity {
	private static final String TAG = "GenericPreferencesActivity";

	protected static final int DIALOG_LOADING = 0;
    protected static final int DIALOG_APPLYING = 1;

	protected int contentViewResId = R.layout.generic_preferences_layout;
	protected int preferencesLayout = 0;
	
	protected LinearLayout buttonLayout;
	protected int buttonLayoutResid = R.id.genericPreferencesButtonLayout;
	
    protected Button applyButton;
    protected int applyButtonResid = R.id.genericPreferencesRightButton;
    protected int applyButtonLabel = R.string.genericApplyButtonLabel;

    protected Button cancelButton;
    protected int cancelButtonResid = R.id.genericPreferencesLeftButton;
    protected int cancelButtonLabel = R.string.genericCancelButtonLabel;
    
	protected void dataInit() {
		AppState.logX(TAG, "dataInit");
	}
	
	protected void viewsInit() {
		AppState.logX(TAG, "viewsInit");

        // initialize the action bar
     	getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
       	AppState.actionBarSet(this);

     	// initialize the content view
		setContentView(contentViewResId);
    	setTheme(Background.preferencesThemeGet());
        getListView().setBackgroundColor(Background.windowBackgroundGet(this));
    	addPreferencesFromResource(preferencesLayout);

		try {
	        ViewConfiguration config = ViewConfiguration.get(this);
	        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	        
	        if (menuKeyField != null) {
	            menuKeyField.setAccessible(true);
	            menuKeyField.setBoolean(config, false);
	        }
	    } catch (Exception ex) {
	        AppState.log(TAG, "viewsInit: Exception");
	    }
	}
	
	protected void init() {
		AppState.log(TAG, "init");

		
		// initialize the data
		dataInit();

		// initialize the views
		viewsInit();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppState.log(TAG, "onCreate");
	
		// initialize
		init();
		
		// initialize the action bar
    	AppState.actionBarSet(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		AppState.log(TAG, "onOptionsItemSelected");
		
		boolean value = false;
		switch (item.getItemId()) {
		case android.R.id.home:
			AppState.log(TAG, "onOptionsItemSelected: home selected");

			Intent intent = new Intent(this, App.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

			value = true;
			break;
		default:
			value = super.onOptionsItemSelected(item);
		}
		
		return value;
	}
}