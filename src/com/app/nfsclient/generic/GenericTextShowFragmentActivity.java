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

import android.os.Bundle;

import com.app.nfsclient.AppState;

public class GenericTextShowFragmentActivity extends GenericFragmentActivity {
    private static final String TAG = "GenericTextShowFragmentActivity";

    public static final String WINDOW_TITLE_KEY = AppState.APP_PACKAGE_NAME + TAG + ".windowTitleKey";
    public static final String WINDOW_TEXT_STRING_KEY = AppState.APP_PACKAGE_NAME + TAG + ".windowTextStringKey";
    public static final String WINDOW_TEXT_RESID_KEY = AppState.APP_PACKAGE_NAME + TAG + ".windowTextResidKey";
    public static final String EXPORT_DIALOG_TITLE_KEY = AppState.APP_PACKAGE_NAME + TAG +
    	".exportDialogTitleKey";
    public static final String EXPORT_DIALOG_MESSAGE_KEY = AppState.APP_PACKAGE_NAME + TAG +
    	".exportDialogMessageKey";
    public static final String EXPORT_DIALOG_FILENAME_KEY = AppState.APP_PACKAGE_NAME + TAG +
       	".exportDialogFilenameKey";
    public static final String EXPORT_DIALOG_ID_KEY = AppState.APP_PACKAGE_NAME + TAG +
        ".exportDialogRequestCodeKey";
    public static final String EXPORT_DIALOG_FORMAT_OPTIONS_KEY = AppState.APP_PACKAGE_NAME + TAG +
        ".exportDialogFormatOptionsKey";
    
    private GenericTextShowFragment showFragment;
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
    	fragment = null;
    	super.onCreate(savedInstanceState);
    	AppState.log(TAG, "onCreate");

    	if (savedInstanceState == null) {
    		showFragment = new GenericTextShowFragment();
    		showFragment.setArguments(getIntent().getExtras());
    		getSupportFragmentManager().beginTransaction().add(
    			GenericFragmentActivity.DEFAULT_FRAGMENT_CONTAINER_ID, showFragment).commit();
    	}
    }
/*
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		AppState.logX(TAG, String.format("onActivityResult: requestCode = 0x%x", requestCode));

		String textBodyText = getIntent().getStringExtra(GenericTextShowFragmentActivity.WINDOW_TEXT_STRING_KEY);
		int provider = requestCode & GenericListItem.ACTIVITY_RESULT_FILE_SELECTION_PROVIDER_MASK;
		int type = requestCode & GenericListItem.ACTIVITY_RESULT_FILE_SELECTION_TYPE_SUPER_MASK;
		switch(provider) {
		case GenericListItem.ACTIVITY_RESULT_FILE_SELECTION_PROVIDER_LOCAL_STORAGE:
			AppState.logX(TAG, "onActivityResult: file selection local storage result");
			if (resultCode == Activity.RESULT_OK && data != null) {
				String filename = data.getStringExtra(FileManagerIntents.ACTION_PICK_FILE);
				String format = data.getStringExtra(FileManagerIntents.EXTRA_FILE_FORMAT);
				
				AppState.logX(TAG, String.format("onActivityResult: filename = %s, format = %s, textBodyText " +
					"= %s", filename, format, textBodyText));

				if (!TextUtils.isEmpty(filename))
					GenericFileExportAsyncTask.fileExportConfirm(this, filename, format, textBodyText);
				else
					Utils.alertDialogShow(this, "Invalid file name", "A file name must be specified");
			}
			break;

		case GenericListItem.ACTIVITY_RESULT_FILE_SELECTION_PROVIDER_DROPBOX_CHOOSER:
    	    AppState.logX(TAG, "onActivityResult: dropbox chooser");

    		if (resultCode == Activity.RESULT_OK) {
    			DbxChooser.Result result = new DbxChooser.Result(data);
    			String filename = data.getStringExtra(FileManagerIntents.ACTION_PICK_FILE);
				String format = data.getStringExtra(FileManagerIntents.EXTRA_FILE_FORMAT);
				
    			AppState.logX(TAG, String.format("onActivityResult: import result: name = %s, link = %s, size" +
    			    " = %d", result.getName(), result.getLink(), result.getSize()));
    		}
    		break;

    	case GenericListItem.ACTIVITY_RESULT_FILE_SELECTION_PROVIDER_DROPBOX_LOGIN:
    	    AppState.logX(TAG, "onActivityResult: dropbox login");

    		if (resultCode == Activity.RESULT_OK) {
    			String loginName = data.getStringExtra(GenericListItem
    				.ACTIVITY_RESULT_FILE_SELECTION_LOGIN_NAME_KEY);
    			String filename = data.getStringExtra(GenericListItem
    				.ACTIVITY_RESULT_FILE_SELECTION_FILE_NAME_KEY);
				String format = data.getStringExtra(FileManagerIntents.EXTRA_FILE_FORMAT);
				GenericStorage account = AppState.dropboxAccountsGetByName(this, loginName);
    			if (account != null) {
    				// export
    				
    				AppState.logX(TAG, String.format("onActivityResult: filename = %s, format = %s, " +
    					"textBodyText = %s", filename, format, textBodyText));
    			}
    			
    			AppState.logX(TAG, String.format("onActivityResult: dropbox login: login = %s, filename = %s",
    				loginName, filename));
    		}
    		break;
            
		default:
			AppState.logX(TAG, "onActivityResult: default"); 
			break;
		}
	}
*/
}