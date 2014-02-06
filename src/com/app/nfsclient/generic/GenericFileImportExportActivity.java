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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

import com.app.nfsclient.AppState;
import com.app.nfsclient.Utils;
import com.app.nfsclient.filemanager.intents.FileManagerIntents;

public class GenericFileImportExportActivity extends SherlockActivity {
    private static final String TAG = "GenericFileImportExportSelectionActivity";
    
    public static final String TITLE_KEY = AppState.APP_PACKAGE_NAME + "." + TAG + ".titleKey";
	public static final String FILE_NAME_KEY = AppState.APP_PACKAGE_NAME + "." + TAG + ".fileNameKey";
	public static final String FILE_TEXT_KEY = AppState.APP_PACKAGE_NAME + "." + TAG + ".fileTextKey";
	public static final String FILE_BYTES_KEY = AppState.APP_PACKAGE_NAME + "." + TAG + ".fileBytesKey";
	public static final String FILE_FORMAT_KEY = AppState.APP_PACKAGE_NAME + "." + TAG + ".fileFormatKey";
	public static final String FORMATS_KEY = AppState.APP_PACKAGE_NAME + "." + TAG + ".formatsKey";
	public static final String BUTTON_LABEL_KEY = AppState.APP_PACKAGE_NAME + "." + TAG + ".buttonLabelKey";
	public static final String SELECTION_INFO_KEY = AppState.APP_PACKAGE_NAME + "." + TAG + ".selectionInfoKey";
	public static final String TRANSACTION_ID_KEY = AppState.APP_PACKAGE_NAME + "." + TAG + ".transactionIdKey";
	public static final String EXTRA_STRING_KEY = AppState.APP_PACKAGE_NAME + "." + TAG + ".echoStringKey";
			
	private Bundle bundle;
    private String title;
    private String[] formats;
    private String fileText;
    private String buttonLabel;
    private int selectionType;
	private long transactionId;
	private String extraString;
	
    public void dataInit() {
    	AppState.logX(TAG, "dataInit");

    	title = bundle.getString(TITLE_KEY);
    	formats = bundle.getStringArray(FORMATS_KEY);
    	fileText = bundle.getString(FILE_TEXT_KEY);
    	buttonLabel = bundle.getString(BUTTON_LABEL_KEY);
    	selectionType = bundle.getInt(SELECTION_INFO_KEY);
    	transactionId = bundle.getLong(TRANSACTION_ID_KEY);
    	extraString = bundle.getString(EXTRA_STRING_KEY);
    	
    	AppState.logX(TAG, String.format("dataInit: title = %s, formats = %s, buttonLabel = %s, selectionType " +
    		"= 0x%x, fileText = %s, transactionId = %d, extraString = %s", title, formats, buttonLabel,
    		selectionType, fileText, transactionId, extraString));
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	AppState.logX(TAG, String.format("onCreate: savedInstanceState = %s", savedInstanceState));
        
    	if (savedInstanceState == null)
    		bundle = getIntent().getExtras();
    	else
    		bundle = savedInstanceState;
    }

	@Override
	public void onStart() {
		super.onStart();
		AppState.logX(TAG, "onStart");
		
		dataInit();
    	new GenericFileImportExportDialog(this, title, formats, buttonLabel, selectionType).show();
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		AppState.logX(TAG, "onSaveInstanceState");

		savedInstanceState.putAll(bundle);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		AppState.logX(TAG, String.format("onActivityResult: requestCode = 0x%x, resultCode = %d", requestCode,
			resultCode));

    	Intent intent = getIntent();
		int result = RESULT_OK;

		if (data != null) {
			String filename = data.getStringExtra(FileManagerIntents.ACTION_PICK_FILE);
			String format = data.getStringExtra(FileManagerIntents.EXTRA_FILE_FORMAT);

			int provider = requestCode & GenericListItem.ACTIVITY_RESULT_FILE_SELECTION_PROVIDER_MASK;
			int selectionTypeAll = requestCode & GenericListItem.ACTIVITY_RESULT_FILE_SELECTION_TYPE_ALL_MASK;
			int selectionTypeSuper = requestCode & GenericListItem
				.ACTIVITY_RESULT_FILE_SELECTION_TYPE_SUPER_MASK;
			int selectionTypeSub = requestCode & GenericListItem.ACTIVITY_RESULT_FILE_SELECTION_TYPE_SUB_MASK;
			
			AppState.logX(TAG, String.format("onActivityResult: provider = 0x%x, selectionType = 0x%x, " +
				"selectionSuperType = 0x%x, selectionSubType = 0x%x", provider, selectionTypeAll,
				selectionTypeSuper, selectionTypeSub));
			
			switch(provider) {
			case GenericListItem.ACTIVITY_RESULT_FILE_SELECTION_PROVIDER_LOCAL_STORAGE:
				AppState.logX(TAG, "onActivityResult: file selection local storage result");
				
				if (resultCode == Activity.RESULT_OK && data != null) {
					AppState.logX(TAG, String.format("onActivityResult: filename = %s, format = %s", filename,
						format));

					if (selectionTypeSuper == GenericListItem.ACTIVITY_RESULT_FILE_SELECTION_TYPE_IMPORT) {
						AppState.logX(TAG, "onActivityResult: file selection local import");

						try {
							String mimeType = Utils.mimeTypeGet(this, filename);

							AppState.logX(TAG, String.format("onActivityResult: mimeType = %s", mimeType));

							/* if (mimeType.toString().equals(getString(R.string.mime_file_txt))) */ {
								AppState.logX(TAG, "onActivityResult: text file");

								// treat as a text file
								BufferedReader br = new BufferedReader(new FileReader(filename));
								StringBuffer sb = new StringBuffer();
								int n;
								char[] buf = new char[512];
								while ((n = br.read(buf)) > 0)
									sb.append(buf, 0, n);

								br.close();
								fileText = sb.toString();
							}
						} catch (FileNotFoundException e) {
							AppState.log(TAG, "doInBackground: FileNotFoundException");
						} catch (IOException e) {
							AppState.log(TAG, "doInBackground: IOException");
						}

						intent.putExtra(FILE_NAME_KEY, filename);
						intent.putExtra(FILE_TEXT_KEY, fileText);
						intent.putExtra(FILE_FORMAT_KEY, format);

						AppState.logX(TAG, String.format("onActivityResult: fileText = %s", fileText));
					} else if (selectionTypeSuper == GenericListItem.ACTIVITY_RESULT_FILE_SELECTION_TYPE_EXPORT)
				        {
						AppState.logX(TAG, "onActivityResult: file selection local export");

						//GenericFileExportAsyncTask.fileExportConfirm(this, filename, format, fileText);
						new GenericFileExportAsyncTask(this, filename, format, fileText).executeOnExecutor(
							GenericAsyncTask.THREAD_POOL_EXECUTOR);
						intent.putExtra(FILE_NAME_KEY, filename);
					}
				}
				break;

			default:
				AppState.logX(TAG, "onActivityResult: default"); 
				break;
			}
		} else {
			result = RESULT_CANCELED;
		}
		
		AppState.logX(TAG, "onActivityResult: return"); 
		intent.putExtra(GenericFileImportExportActivity.SELECTION_INFO_KEY, requestCode);
		setResult(result, intent);
		finish();
	}
}
