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
/* 
 * Copyright (C) 2008 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Based on AndDev.org's file browser V 2.0.
 */

package com.app.nfsclient.filemanager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.actionbarsherlock.internal.widget.IcsAdapterView;
import com.actionbarsherlock.internal.widget.IcsAdapterView.OnItemSelectedListener;
import com.app.nfsclient.AppState;
import com.app.nfsclient.Background;
import com.app.nfsclient.R;
import com.app.nfsclient.Utils;
import com.app.nfsclient.filemanager.distribution.DistributionLibraryListActivity;
import com.app.nfsclient.filemanager.intents.FileManagerIntents;
import com.app.nfsclient.filemanager.util.FileUtils;
import com.app.nfsclient.filemanager.util.MimeTypeParser;
import com.app.nfsclient.filemanager.util.MimeTypes;
import com.app.nfsclient.generic.GenericAsyncTask;
import com.app.nfsclient.generic.GenericDialogContextMenu;
import com.app.nfsclient.generic.GenericFileInterface;
import com.app.nfsclient.generic.GenericFileSystem;
import com.app.nfsclient.generic.GenericIcsSpinner;
import com.app.nfsclient.generic.GenericListItemList;
import com.app.nfsclient.generic.GenericSpinnerAdapter;
import com.app.nfsclient.images.ImagesUtils;

import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FileManagerActivity extends DistributionLibraryListActivity { 
    private static final String TAG = "FileManagerActivity";

	private static final String NOMEDIA_FILE = ".nomedia";

	/**
	 * @since 2011-03-23
	 */
	private static final Character FILE_EXTENSION_SEPARATOR = '.';

	private int mState;
    
	private static final int STATE_BROWSE = 1;
	private static final int STATE_PICK_FILE = 2;
	private static final int STATE_PICK_DIRECTORY = 3;
	private static final int STATE_MULTI_SELECT = 4;
	private static final int STATE_PICK_FILE_DROPBOX = 5;

	protected static final int REQUEST_CODE_MOVE = 1;
	protected static final int REQUEST_CODE_COPY = 2;

	/**
	 * @since 2011-02-11
	 */
	private static final int REQUEST_CODE_MULTI_SELECT = 3;

	private static final int MENU_PREFERENCES = Menu.FIRST + 3;
	private static final int MENU_NEW_FOLDER = Menu.FIRST + 4;
	private static final int MENU_DELETE = Menu.FIRST + 5;
	private static final int MENU_RENAME = Menu.FIRST + 6;
	private static final int MENU_SEND = Menu.FIRST + 7;
	private static final int MENU_OPEN = Menu.FIRST + 8;
	private static final int MENU_MOVE = Menu.FIRST + 9;
	private static final int MENU_COPY = Menu.FIRST + 10;
	private static final int MENU_INCLUDE_IN_MEDIA_SCAN = Menu.FIRST + 11;
	private static final int MENU_EXCLUDE_FROM_MEDIA_SCAN = Menu.FIRST + 12;
	private static final int MENU_SETTINGS = Menu.FIRST + 13;
	private static final int MENU_MULTI_SELECT = Menu.FIRST + 14;
	private static final int MENU_DISTRIBUTION_START = Menu.FIRST + 100; // MUST BE LAST

	private static final int DIALOG_NEW_FOLDER = 1;
	private static final int DIALOG_DELETE = 2;
	private static final int DIALOG_RENAME = 3;

	/**
	 * @since 2011-02-12
	 */
	private static final int DIALOG_MULTI_DELETE = 4;

	private static final int DIALOG_DISTRIBUTION_START = 100; // MUST BE LAST

	private static final int COPY_BUFFER_SIZE = 32 * 1024;

	private static final String BUNDLE_CURRENT_DIRECTORY = "current_directory";
	private static final String BUNDLE_CONTEXT_FILE = "context_file";
	private static final String BUNDLE_CONTEXT_TEXT = "context_text";
	private static final String BUNDLE_SHOW_DIRECTORY_INPUT = "show_directory_input";
	private static final String BUNDLE_STEPS_BACK = "steps_back";

	/** Contains directories and files together */
	private ArrayList<IconifiedText> directoryEntries = new ArrayList<IconifiedText>();

	/** Dir separate for sorting */
	List<IconifiedText> mListDir = new ArrayList<IconifiedText>();

	/** Files separate for sorting */
	List<IconifiedText> mListFile = new ArrayList<IconifiedText>();

	/** SD card separate for sorting */
	List<IconifiedText> mListSdCard = new ArrayList<IconifiedText>();

	// There's a ".nomedia" file here
	private boolean mNoMedia;

	private GenericFileInterface currentDirectory; 

	private String mFileSystemPath = "";

	private MimeTypes mMimeTypes;

	private String mContextText;
	private GenericFileInterface mContextFile;
	private Drawable mContextIcon;

	/** How many steps one can make back using the back key. */
	private int mStepsBack;

    private EditText mEditFilename;
	private Button mButtonPick;
	private FrameLayout mDirectoryLayout;
	private LinearLayout mDirectoryButtons;

	private LinearLayout mFilenameLayout;
	private LinearLayout mFileFormatLayout;
	private GenericIcsSpinner mFileFormatSpinner;
	private boolean mFileFormat = false;
    private TextView mFileFormatLabel;
	private String[] mFileFormatOptions = null;
	private GenericSpinnerAdapter mFileFormatSpinnerAdapter;
    private FrameLayout mListViewLayout;
    
	/**
	 * @since 2011-02-11
	 */
	private Button mButtonMove;

	/**
	 * @since 2011-02-11
	 */
	private Button mButtonCopy;

	/**
	 * @since 2011-02-11
	 */
	private Button mButtonDelete;

	private LinearLayout mDirectoryInput;
	private EditText mEditDirectory;
	private ImageButton mButtonDirectoryPick;

	/**
	 * @since 2011-02-11
	 */
	private LinearLayout mActionNormal;

	/**
	 * @since 2011-02-11
	 */
	private LinearLayout mActionMultiselect;

	private TextView mEmptyText;
	private ProgressBar mProgressBar;

	private GenericFileSystem mFileSystem;
	private String mFileSystemType;
	private String mFileSystemId;
	
	private DirectoryScanner mDirectoryScanner;
	private GenericFileInterface mPreviousDirectory;
	private ThumbnailLoader mThumbnailLoader;

	private MenuItem mExcludeMediaScanMenuItem;
	private MenuItem mIncludeMediaScanMenuItem;

	private Handler currentHandler;

	private boolean mWritableOnly;

	private IconifiedText[] mDirectoryEntries;

	static final public int MESSAGE_SHOW_DIRECTORY_CONTENTS = 500;	// List of contents is ready, obj = DirectoryContents
	static final public int MESSAGE_SET_PROGRESS = 501;	// Set progress bar, arg1 = current value, arg2 = max value
	static final public int MESSAGE_ICON_CHANGED = 502;	// View needs to be redrawn, obj = IconifiedText

	/** Called when the activity is first created. */ 
	@Override 
	public void onCreate(Bundle icicle) { 
        super.onCreate(icicle); 

        // get the file system type
        mFileSystemType = getIntent().getStringExtra(FileManagerIntents.EXTRA_FILE_SYSTEM_TYPE);
        mFileSystemId = getIntent().getStringExtra(FileManagerIntents.EXTRA_FILE_SYSTEM_ID);
        AppState.logX(TAG, String.format("onCreate: type = %s, id = %s", mFileSystemType, mFileSystemId));
        if (mFileSystemType.equalsIgnoreCase(GenericFileSystem.ITEM_TYPE_NFS_SERVER)) {
        	mFileSystem = AppState.serversGetByHostName(this, mFileSystemId);
        }
    	currentDirectory = mFileSystem.fileInstanceGet("");
    	mContextFile = mFileSystem.fileInstanceGet("");

		mDistribution.setFirst(MENU_DISTRIBUTION_START, DIALOG_DISTRIBUTION_START);

		currentHandler = new Handler() {
			public void handleMessage(Message msg) {
				FileManagerActivity.this.handleMessage(msg);
			}
		};
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.filelist2);

		// initialize the action bar
    	AppState.actionBarSet(this);
    	
		mEmptyText = (TextView) findViewById(R.id.empty_text);
		mProgressBar = (ProgressBar) findViewById(R.id.scan_progress);

		getListView().setOnCreateContextMenuListener(this);
		getListView().setEmptyView(findViewById(R.id.empty));
		getListView().setTextFilterEnabled(true);
		getListView().requestFocus();
		getListView().requestFocusFromTouch();

		mDirectoryLayout = (FrameLayout) findViewById(R.id.directory_layout);
		if (mDirectoryLayout != null)
		    mDirectoryLayout.setBackgroundColor(Background.windowBackgroundGet(this));

		mDirectoryButtons = (LinearLayout) findViewById(R.id.directory_buttons);
		
		mActionNormal = (LinearLayout) findViewById(R.id.action_normal);
		
		mActionMultiselect = (LinearLayout) findViewById(R.id.action_multiselect);
		
		mEditFilename = (EditText) findViewById(R.id.filename);
		if (mEditFilename != null)
            mEditFilename.setTextColor(Background.textColorGet(this));
        
		mButtonPick = (Button) findViewById(R.id.button_pick);
		mButtonPick.setTextColor(Background.textColorGet(this));
		mButtonPick.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				pickFileOrDirectory();
			}
		});

		mFilenameLayout = (LinearLayout) findViewById(R.id.filename_layout);
		if (mFilenameLayout != null)
			mFilenameLayout.setBackgroundColor(Background.windowBackgroundGet(this));

		mFileFormatLayout = (LinearLayout) findViewById(R.id.file_format_layout);
		if (mFilenameLayout != null)
			mFileFormatLayout.setBackgroundColor(Background.windowBackgroundGet(this));
		
		mFileFormatLabel = (TextView) findViewById(R.id.file_format_label);
		if (mFileFormatLabel != null)
			mFileFormatLabel.setBackgroundColor(Background.windowBackgroundGet(this));
		
		mFileFormatSpinner = (GenericIcsSpinner) findViewById(R.id.file_format_spinner);
		
		mListViewLayout = (FrameLayout) findViewById(R.id.list_view_layout);
		if (mListViewLayout != null)
            mListViewLayout.setBackgroundColor(Background.windowBackgroundGet(this));

		// Initialize only when necessary:
		mDirectoryInput = null;

		// Create a map of extensions:
		getMimeTypes();

		getSdCardPath();

		mState = STATE_BROWSE;

		Intent intent = getIntent();
		String action = intent.getAction();

		GenericFileInterface browseto = mFileSystem.fileInstanceGet("/");

		if (!TextUtils.isEmpty(mFileSystemPath))
		    browseto = mFileSystem.fileInstanceGet(mFileSystemPath);

		// Default state
		mState = STATE_BROWSE;
		mWritableOnly = false;

		if (action != null) {
		    if (action.equals(FileManagerIntents.ACTION_PICK_FILE)) {
				mState = STATE_PICK_FILE;
			} else if (action.equals(FileManagerIntents.ACTION_PICK_FILE_DROPBOX)) {
				mState = STATE_PICK_FILE_DROPBOX;
			} else if (action.equals(FileManagerIntents.ACTION_PICK_DIRECTORY)) {
				mState = STATE_PICK_DIRECTORY;	          
				mWritableOnly = intent.getBooleanExtra(FileManagerIntents.EXTRA_WRITEABLE_ONLY, false);

				// Remove edit text and make button fill whole line
				mEditFilename.setVisibility(View.GONE);
				mButtonPick.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
			} else if (action.equals(FileManagerIntents.ACTION_MULTI_SELECT)) {
				mState = STATE_MULTI_SELECT;        		          		          

				// Remove buttons
				mDirectoryButtons.setVisibility(View.GONE);
				mActionNormal.setVisibility(View.GONE);

				// Multi select action: move
				mButtonMove = (Button) findViewById(R.id.button_move);
				mButtonMove.setOnClickListener(new View.OnClickListener() {

					public void onClick(View arg0) {
						if (checkSelection())
							promptDestinationAndMoveFile();
					}
				});

				// Multi select action: copy
				mButtonCopy = (Button) findViewById(R.id.button_copy);
				mButtonCopy.setTextColor(Background.textColorGet(this));
				mButtonCopy.setOnClickListener(new View.OnClickListener() {
					public void onClick(View arg0) {
						if (checkSelection())
							promptDestinationAndCopyFile();
					}
				});

				// Multi select action: delete
				mButtonDelete = (Button) findViewById(R.id.button_delete);
				mButtonDelete.setOnClickListener(new View.OnClickListener() {
					public void onClick(View arg0) {
						if (checkSelection())
							showDialog(DIALOG_MULTI_DELETE);
					}
				});
			}
		} 

		if (mState == STATE_BROWSE) {
			// Remove edit text and button.
			mEditFilename.setVisibility(View.GONE);
			mButtonPick.setVisibility(View.GONE);
		}

		if (mState != STATE_MULTI_SELECT) {
			// Remove multiselect action buttons
			mActionMultiselect.setVisibility(View.GONE);
		}

		// Set current directory and file based on intent data.
		GenericFileInterface file = FileUtils.getFile(mFileSystem, intent.getData());
		if (file != null) {
			GenericFileInterface dir = FileUtils.getPathWithoutFilename(mFileSystem, file);
			
			if (dir.isDirectory())
				browseto = dir;

			if (!file.isDirectory())
				mEditFilename.setText(file.getName());
		}

		String title = intent.getStringExtra(FileManagerIntents.EXTRA_TITLE);
		if (title != null) {
			// setTitle(title);
			getSupportActionBar().setTitle(title);
		}
		
		String buttontext = intent.getStringExtra(FileManagerIntents.EXTRA_BUTTON_TEXT);
		if (buttontext != null)
			mButtonPick.setText(buttontext);

		mStepsBack = 0;

		// configure the export file format
		mFileFormatOptions = intent.getStringArrayExtra(FileManagerIntents.EXTRA_FILE_FORMAT);
        mFileFormat = mFileFormatOptions != null;
		if (mFileFormat) {
			mFileFormatLayout.setBackgroundColor(Background.windowBackgroundGet(this));
			mFileFormatLabel.setBackgroundColor(Background.windowBackgroundGet(this));
			mFileFormatLabel.setTextColor(Background.textColorGet(this));

			GenericListItemList mFileFormatOptionsList = new GenericListItemList();
			mFileFormatOptionsList.add(mFileFormatOptions);
			mFileFormatSpinnerAdapter = new GenericSpinnerAdapter(this, mFileFormatOptionsList);
	        mFileFormatSpinner.setAdapter(mFileFormatSpinnerAdapter);
	        mFileFormatSpinner.setSelection(0);
	        mFileFormatSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	        	@Override
				public void onItemSelected(IcsAdapterView<?> parent, View view, int position, long id) {}

				@Override
				public void onNothingSelected(IcsAdapterView<?> parent) {}
	        });
		} else {
			mFileFormatLayout.setVisibility(View.GONE);
		}
		
		if (icicle != null) {
			browseto = mFileSystem.fileInstanceGet(icicle.getString(BUNDLE_CURRENT_DIRECTORY));
			mContextFile = mFileSystem.fileInstanceGet(icicle.getString(BUNDLE_CONTEXT_FILE));
			mContextText = icicle.getString(BUNDLE_CONTEXT_TEXT);

			boolean show = icicle.getBoolean(BUNDLE_SHOW_DIRECTORY_INPUT);
			showDirectoryInput(show);

			mStepsBack = icicle.getInt(BUNDLE_STEPS_BACK);
		}

//XXX    	getListView().setOnItemLongClickListener(new ContextMenuDialog());

		browseTo(browseto);
	}

    public void onDestroy() {
	    super.onDestroy();

		// Stop the scanner.
		DirectoryScanner scanner = mDirectoryScanner;

		if (scanner != null)
			scanner.cancel = true;

		mDirectoryScanner = null;

		ThumbnailLoader loader = mThumbnailLoader;

		if (loader != null) {
			loader.cancel = true;
			mThumbnailLoader = null;
		}
	}

	private void handleMessage(Message message) {
		// Log.v(TAG, "Received message " + message.what);

		switch (message.what) {
		case MESSAGE_SHOW_DIRECTORY_CONTENTS:
			showDirectoryContents((DirectoryContents) message.obj);
			break;

		case MESSAGE_SET_PROGRESS:
			setProgress(message.arg1, message.arg2);
			break;

		case MESSAGE_ICON_CHANGED:
			notifyIconChanged((IconifiedText) message.obj);
			break;
		}
	}

	private void notifyIconChanged(IconifiedText text) {
		if (getListAdapter() != null)
			((BaseAdapter) getListAdapter()).notifyDataSetChanged();
	}

	private void setProgress(int progress, int maxProgress) {
		mProgressBar.setMax(maxProgress);
		mProgressBar.setProgress(progress);
		mProgressBar.setVisibility(View.VISIBLE);
	}

	private void showDirectoryContents(DirectoryContents contents) {
		mDirectoryScanner = null;

		mListSdCard = contents.listSdCard;
		mListDir = contents.listDir;
		mListFile = contents.listFile;
		mNoMedia = contents.noMedia;

		directoryEntries.ensureCapacity(mListSdCard.size() + mListDir.size() + mListFile.size());

		addAllElements(directoryEntries, mListSdCard);
		addAllElements(directoryEntries, mListDir);
		addAllElements(directoryEntries, mListFile);

		mDirectoryEntries = directoryEntries.toArray(new IconifiedText[0]); 

		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this); 
		itla.setListItems(directoryEntries, getListView().hasTextFilter());          
		setListAdapter(itla); 
		getListView().setTextFilterEnabled(true);

		selectInList(mPreviousDirectory);
		refreshDirectoryPanel();
		setProgressBarIndeterminateVisibility(false);

		mProgressBar.setVisibility(View.GONE);
		mEmptyText.setVisibility(View.VISIBLE);

		mThumbnailLoader = new ThumbnailLoader(mFileSystem, currentDirectory, mListFile, currentHandler, this, mMimeTypes);
		mThumbnailLoader.start();
	}

	private void onCreateDirectoryInput() {
		mDirectoryInput = (LinearLayout) findViewById(R.id.directory_input);
		mEditDirectory = (EditText) findViewById(R.id.directory_text);
		mButtonDirectoryPick = (ImageButton) findViewById(R.id.button_directory_pick);

		mButtonDirectoryPick.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				goToDirectoryInEditText();
			}
		});
	}

	//private boolean mHaveShownErrorMessage;
	private GenericFileInterface mHaveShownErrorMessageForFile = null;

	private void goToDirectoryInEditText() {
		GenericFileInterface browseto = mFileSystem.fileInstanceGet(mEditDirectory.getText().toString());

		if (browseto.equals(currentDirectory)) {
			showDirectoryInput(false);
		} else {
			if (mHaveShownErrorMessageForFile != null &&
				mHaveShownErrorMessageForFile.equals(browseto)) {
				// Don't let user get stuck in wrong directory.
				mHaveShownErrorMessageForFile = null;
				showDirectoryInput(false);
			} else {
				if (!browseto.exists())
					// browseTo() below will show an error message,
					// because file does not exist.
					// It is ok to show this the first time.
					mHaveShownErrorMessageForFile = browseto;

				browseTo(browseto);
			}
		}
	}

	/**
	 * Show the directory line as input box instead of button row.
	 * If Directory input does not exist yet, it is created.
	 * Since the default is show == false, nothing is created if
	 * it is not necessary (like after icicle).
	 * @param show
	 */
	private void showDirectoryInput(boolean show) {
		if (show) {
			if (mDirectoryInput == null)
				onCreateDirectoryInput();
		}
		
		if (mDirectoryInput != null) {
			mDirectoryInput.setVisibility(show ? View.VISIBLE : View.GONE);
			mDirectoryButtons.setVisibility(show ? View.GONE : View.VISIBLE);
		}

		refreshDirectoryPanel();
	}

	/**
	 * 
	 */
	private void refreshDirectoryPanel() {
		if (isDirectoryInputVisible()) {
			// Set directory path
			String path = currentDirectory.getAbsolutePath();
			mEditDirectory.setText(path);

			// Set selection to last position so user can continue to type:
			mEditDirectory.setSelection(path.length());
		} else {
			setDirectoryButtons();
		}
	} 
	/*
     @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	 */


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);

		// remember file name
		outState.putString(BUNDLE_CURRENT_DIRECTORY, currentDirectory.getAbsolutePath());
		outState.putString(BUNDLE_CONTEXT_FILE, mContextFile.getAbsolutePath());
		outState.putString(BUNDLE_CONTEXT_TEXT, mContextText);
		boolean show = isDirectoryInputVisible();
		outState.putBoolean(BUNDLE_SHOW_DIRECTORY_INPUT, show);
		outState.putInt(BUNDLE_STEPS_BACK, mStepsBack);
	}

	/**
	 * @return
	 */
	private boolean isDirectoryInputVisible() {
		return ((mDirectoryInput != null) && (mDirectoryInput.getVisibility() == View.VISIBLE));
	}

	private void pickFileOrDirectory() {
		GenericFileInterface file = null;
		
		if (mState == STATE_PICK_FILE) {
			String filename = mEditFilename.getText().toString();
			file = FileUtils.getFile(mFileSystem, currentDirectory.getAbsolutePath(), filename);
		} else if (mState == STATE_PICK_DIRECTORY) {
			file = currentDirectory;
		}

		AppState.logX(TAG, String.format("pickFileOrDirectory: file = %s", file.getAbsolutePath()));
		Intent intent = getIntent();
		intent.setData(FileUtils.getUri(file));
		intent.putExtra(FileManagerIntents.ACTION_PICK_FILE, file.getAbsolutePath());
		
		if (mFileFormat) {
			intent.putExtra(FileManagerIntents.EXTRA_FILE_FORMAT, mFileFormatSpinner.getSelectedItem()
				.toString());
		}
		
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * 
	 */
    private void getMimeTypes() {
        MimeTypeParser mtp = new MimeTypeParser();
		XmlResourceParser in = getResources().getXml(R.xml.mimetypes);

		try {
		    mMimeTypes = mtp.fromXmlResource(in);
		} catch (XmlPullParserException e) {
			Log.e(TAG, "PreselectedChannelsActivity: XmlPullParserException", e);
			throw new RuntimeException("PreselectedChannelsActivity: XmlPullParserException");
		} catch (IOException e) {
			Log.e(TAG, "PreselectedChannelsActivity: IOException", e);
			throw new RuntimeException("PreselectedChannelsActivity: IOException");
	    }
    }

    /** 
	  * This function browses up one level 
	  * according to the field: currentDirectory 
	  */ 
    private void upOneLevel() {
	    if (mStepsBack > 0)
			mStepsBack--;

		if (currentDirectory.getParent() != null) 
			browseTo(currentDirectory.getParentFile()); 
	} 

	/**
	 * Jump to some location by clicking on a 
	 * directory button.
	 * 
	 * This resets the counter for "back" actions.
	 * 
	 * @param aDirectory
	 */
    private void jumpTo(final GenericFileInterface aDirectory) {
		mStepsBack = 0;
		browseTo(aDirectory);
	}

	/**
	 * Browse to some location by clicking on a list item.
	 * @param aDirectory
	 */
    private void browseTo(final GenericFileInterface aDirectory){ 
		// setTitle(aDirectory.getAbsolutePath());

		if (aDirectory.isDirectory()) {
			if (aDirectory.equals(currentDirectory)) {
				// Switch from button to directory input
				showDirectoryInput(true);
			} else {
				mPreviousDirectory = currentDirectory;
				currentDirectory = aDirectory;
				refreshList();
				 //	               selectInList(previousDirectory);
				 //               refreshDirectoryPanel();
			}
		} else { 
			if (mState == STATE_BROWSE || mState == STATE_PICK_DIRECTORY) {
				// Lets start an intent to View the file, that was clicked... 
				openFile(aDirectory); 
			} else if (mState == STATE_PICK_FILE) {
				// Pick the file
				mEditFilename.setText(aDirectory.getName());
			}
		} 
	}

    private void openFile(GenericFileInterface aFile) { 
	    if (!aFile.exists()) {
			Toast.makeText(this, R.string.error_file_does_not_exists, Toast.LENGTH_SHORT).show();
			return;
		}

		Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
		Uri data = FileUtils.getUri(aFile);
		String type = mMimeTypes.getMimeType(aFile.getName());
		intent.setDataAndType(data, type);

		// Were we in GET_CONTENT mode?
		Intent originalIntent = getIntent();

		if (originalIntent != null && originalIntent.getAction() != null &&
			originalIntent.getAction().equals(Intent.ACTION_GET_CONTENT)) {
			// In that case, we should probably just return the requested data.
			intent.setData(Uri.parse(FileManagerProvider.FILE_PROVIDER_PREFIX + aFile));
			setResult(RESULT_OK, intent);
			finish();
			return;
		}

		try {
			startActivity(intent); 
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, R.string.application_not_available, Toast.LENGTH_SHORT).show();
	    };
    } 

    public void refreshList() {
	    boolean directoriesOnly = mState == STATE_PICK_DIRECTORY;

		// Cancel an existing scanner, if applicable.
		DirectoryScanner scanner = mDirectoryScanner;

		if (scanner != null)
			scanner.cancel = true;

		ThumbnailLoader loader = mThumbnailLoader;

		if (loader != null) {
			loader.cancel = true;
			mThumbnailLoader = null;
		}

		directoryEntries.clear(); 
		mListDir.clear();
		mListFile.clear();
		mListSdCard.clear();

		setProgressBarIndeterminateVisibility(true);

		// Don't show the "folder empty" text since we're scanning.
		mEmptyText.setVisibility(View.GONE);

		// Also DON'T show the progress bar - it's kind of lame to show that
		// for less than a second.
		mProgressBar.setVisibility(View.GONE);
		setListAdapter(null); 

		mDirectoryScanner = new DirectoryScanner(mFileSystem, currentDirectory, this, currentHandler, mMimeTypes,
            mFileSystemPath, mWritableOnly, directoriesOnly);
		mDirectoryScanner.start();

		// Add the "." == "current directory" 
		/*directoryEntries.add(new IconifiedText( 
                   getString(R.string.current_dir), 
                    getResources().getDrawable(R.drawable.ic_launcher_folder)));        */
		// and the ".." == 'Up one level' 
		/*
        if(currentDirectory.getParent() != null) 
            directoryEntries.add(new IconifiedText( 
                         getString(R.string.up_one_level), 
                         getResources().getDrawable(R.drawable.ic_launcher_folder_open))); 
		*/
    } 

	private void selectInList(GenericFileInterface selectFile) {
		String filename = selectFile.getName();
		IconifiedTextListAdapter la = (IconifiedTextListAdapter) getListAdapter();
		int count = la.getCount();
		
		for (int i = 0; i < count; i++) {
			IconifiedText it = (IconifiedText) la.getItem(i);
			
			if (it.getText().equals(filename)) {
				getListView().setSelection(i);
				break;
			}
		}
	}

	private void addAllElements(List<IconifiedText> addTo, List<IconifiedText> addFrom) {
		int size = addFrom.size();
		
		for (int i = 0; i < size; i++)
			addTo.add(addFrom.get(i));
	}

	private void setDirectoryButtons() {
		String[] parts = currentDirectory.getAbsolutePath().split("/");

		mDirectoryButtons.removeAllViews();

		int WRAP_CONTENT = LinearLayout.LayoutParams.WRAP_CONTENT;

		// Add home button separately
		ImageButton ib = new ImageButton(this);
		if (mState == STATE_PICK_FILE_DROPBOX)
			ib.setImageDrawable(ImagesUtils.dropboxDefaultDrawableImageGet(this));
		else
		    ib.setImageResource(R.drawable.ic_launcher_home_small);
		ib.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
		ib.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				jumpTo(mFileSystem.fileInstanceGet("/"));
			}
		});
		mDirectoryButtons.addView(ib);

		// Add other buttons
		String dir = "";
		for (int i = 1; i < parts.length; i++) {
			dir += "/" + parts[i];
			if (dir.equals(mFileSystemPath)) {
				// Add SD card button
				ib = new ImageButton(this);
				ib.setImageResource(R.drawable.icon_sdcard_small);
				ib.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
				ib.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						jumpTo(mFileSystem.fileInstanceGet(mFileSystemPath));
					}
				});
				mDirectoryButtons.addView(ib);
			} else {
				Button b = new Button(this);
				b.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
				b.setText(parts[i]);
				b.setTextColor(Background.textColorGet(this));
				b.setTag(dir);
				b.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						String dir = (String) view.getTag();
						jumpTo(mFileSystem.fileInstanceGet(dir));
					}
				});
				mDirectoryButtons.addView(b);
			}
		}

		checkButtonLayout();
    }

	 private void checkButtonLayout() {
		 // Let's measure how much space we need:
		 int spec = View.MeasureSpec.UNSPECIFIED;
		 mDirectoryButtons.measure(spec, spec);
		 int count = mDirectoryButtons.getChildCount();

		 int requiredwidth = mDirectoryButtons.getMeasuredWidth();
		 int width = getWindowManager().getDefaultDisplay().getWidth();

		 if (requiredwidth > width) {
			 int WRAP_CONTENT = LinearLayout.LayoutParams.WRAP_CONTENT;

			 // Create a new button that shows that there is more to the left:
			 ImageButton ib = new ImageButton(this);
			 ib.setImageResource(R.drawable.ic_menu_back_small);
			 ib.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
			 // 
			 ib.setOnClickListener(new View.OnClickListener() {
				 public void onClick(View view) {
					 // Up one directory.
					 upOneLevel();
				 }
			 });
			 mDirectoryButtons.addView(ib, 0);

			 // New button needs even more space
			 ib.measure(spec, spec);
			 requiredwidth += ib.getMeasuredWidth();

			 // Need to take away some buttons
			 // but leave at least "back" button and one directory button.
			 while (requiredwidth > width && mDirectoryButtons.getChildCount() > 2) {
				 View view = mDirectoryButtons.getChildAt(1);
				 requiredwidth -= view.getMeasuredWidth();

				 mDirectoryButtons.removeViewAt(1);
			 }
		 }
	 }

	 @Override 
	 protected void onListItemClick(ListView l, View v, int position, long id) { 
		 super.onListItemClick(l, v, position, id); 

		 IconifiedTextListAdapter adapter = (IconifiedTextListAdapter) getListAdapter();

		 if (adapter == null) {
			 return;
		 }

		 IconifiedText text = (IconifiedText) adapter.getItem(position);

		 if (mState == STATE_MULTI_SELECT) {
			 text.setSelected(!text.isSelected());
			 adapter.notifyDataSetChanged();
			 return;
		 }

		 String file = text.getText(); 
		 /*
          if (selectedFileString.equals(getString(R.string.up_one_level))) { 
               upOneLevel(); 
          } else { 
		  */
		 String curdir = currentDirectory 
				 .getAbsolutePath() ;
		 GenericFileInterface clickedFile = FileUtils.getFile(mFileSystem, curdir, file);
		 if (clickedFile != null) {
			 if (clickedFile.isDirectory()) {
				 // If we click on folders, we can return later by the "back" key.
				 mStepsBack++;
			 }
			 browseTo(clickedFile);
		 }
		 /*
          } 
		  */
	 }

	 private void getSdCardPath() {
		 mFileSystemPath = android.os.Environment
				 .getExternalStorageDirectory().getAbsolutePath();
	 }

	 private class ContextMenuDialog implements OnItemLongClickListener {
         private static final String TAG = FileManagerActivity.TAG + ":ContextMenuListener";
         
         @Override
         public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long rowId) {
        	 List<GenericDialogContextMenu.GenericDialogContextMenuItem> items =
        	     new ArrayList<GenericDialogContextMenu.GenericDialogContextMenuItem>();
        	 
        	 IconifiedTextListAdapter adapter = (IconifiedTextListAdapter) getListAdapter();
    		 if (adapter == null)
    			 return true;

    		 IconifiedText it = (IconifiedText) adapter.getItem(position);

    		 items.add(new GenericDialogContextMenu.GenericDialogContextMenuItem(getString(
    			 R.string.menu_rename)));
    		 items.add(new GenericDialogContextMenu.GenericDialogContextMenuItem(getString(
			     R.string.menu_delete)));
    		 
    		 final GenericDialogContextMenu dialog = new GenericDialogContextMenu(FileManagerActivity.this,
    		     it.getText(), items, null);
    		 dialog.itemClickListenerSet(new ContextMenuItemClickListener(it, dialog));
    		 dialog.show();
 				
    		 return true;
         }
	 }
     private class ContextMenuItemClickListener implements AdapterView.OnItemClickListener {
	     private static final String TAG = FileManagerActivity.TAG + ":ContextMenuItemClickListener";

         private IconifiedText it;
		 private GenericDialogContextMenu dialog;
         
		 public ContextMenuItemClickListener(IconifiedText it, GenericDialogContextMenu dialog) {
			 this.it = it;
			 this.dialog = dialog;
		 }

		 @Override
		 public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
			 AppState.logZ(TAG, String.format("onItemClick: arg1 = %s, arg2 = %d, arg3 = %d", arg1, arg2,
			     arg3));

			 switch(arg2) {
			 case 0: // rename
				 AppState.logX(TAG, "onContextItemSelected: ");

				 showDialog(DIALOG_RENAME);
				 break;

			 case 1: // delete
				 AppState.logX(TAG, "onContextItemSelected: ");

				 showDialog(DIALOG_DELETE);
				 break;

			 }
			 dialog.dismiss();

		 }
	 }
/*
	 @Override
	 public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		 AdapterView.AdapterContextMenuInfo info;
		 try {
			 info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		 } catch (ClassCastException e) {
			 Log.e(TAG, "bad menuInfo", e);
			 return;
		 }
		 
		 IconifiedTextListAdapter adapter = (IconifiedTextListAdapter) getListAdapter();

		 if (adapter == null) {
			 return;
		 }

		 IconifiedText it = (IconifiedText) adapter.getItem(info.position);
		 menu.setHeaderTitle(it.getText());
		 menu.setHeaderIcon(it.getIcon());
		 File file = FileUtils.getFile(currentDirectory, it.getText());


		 if (!file.isDirectory()) {
			 if (mState == STATE_PICK_FILE) {
				 // Show "open" menu
				 menu.add(0, MENU_OPEN, 0, R.string.menu_open);
			 }
			 menu.add(0, MENU_SEND, 0, R.string.menu_send);
		 }
		 menu.add(0, MENU_MOVE, 0, R.string.menu_move);

		 if (!file.isDirectory()) {
			 menu.add(0, MENU_COPY, 0, R.string.menu_copy);
		 }

		 menu.add(0, MENU_RENAME, 0, R.string.menu_rename);
		 menu.add(0, MENU_DELETE, 0, R.string.menu_delete);

		 //if (!file.isDirectory()) {
		 Uri data = Uri.fromFile(file);
		 Intent intent = new Intent(null, data);
		 String type = mMimeTypes.getMimeType(file.getName());

		 intent.setDataAndType(data, type);
		 //intent.addCategory(Intent.CATEGORY_SELECTED_ALTERNATIVE);

		 Log.v(TAG, "Data=" + data);
		 Log.v(TAG, "Type=" + type);

		 if (type != null) {
			 // Add additional options for the MIME type of the selected file.
			 menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
					 new ComponentName(this, FileManagerActivity.class), null, intent, 0, null);
		 }
		 //}
	 }

	 @Override
	 public boolean onContextItemSelected(MenuItem item) {
		 super.onContextItemSelected(item);
		 AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();

		 // Remember current selection
		 IconifiedTextListAdapter adapter = (IconifiedTextListAdapter) getListAdapter();

		 if (adapter == null) {
			 return false;
		 }

		 IconifiedText ic = (IconifiedText) adapter.getItem(menuInfo.position);
		 mContextText = ic.getText();
		 mContextIcon = ic.getIcon();
		 mContextFile = FileUtils.getFile(currentDirectory, ic.getText());

		 switch (item.getItemId()) {
		 case MENU_OPEN:
			 openFile(mContextFile); 
			 return true;

		 case MENU_MOVE:
			 promptDestinationAndMoveFile();
			 return true;

		 case MENU_COPY:
			 promptDestinationAndCopyFile();
			 return true;

		 case MENU_DELETE:
			 showDialog(DIALOG_DELETE);
			 return true;

		 case MENU_RENAME:
			 showDialog(DIALOG_RENAME);
			 return true;

		 case MENU_SEND:
			 sendFile(mContextFile);
			 return true;
		 }

		 return false;
	 }
*/
	 @Override
	 protected Dialog onCreateDialog(int id) {
		 switch (id) {
		 case DIALOG_NEW_FOLDER:
			 LayoutInflater inflater = LayoutInflater.from(this);
			 View view = inflater.inflate(R.layout.dialog_new_folder, null);
			 final EditText et = (EditText) view
					 .findViewById(R.id.foldername);
			 et.setText("");
			 return new AlertDialog.Builder(this)
			 .setIcon(android.R.drawable.ic_dialog_alert)
			 .setTitle(R.string.create_new_folder).setView(view).setPositiveButton(
					 android.R.string.ok, new OnClickListener() {

						 public void onClick(DialogInterface dialog, int which) {
							 createNewFolder(et.getText().toString());
						 }

					 }).setNegativeButton(android.R.string.cancel, new OnClickListener() {

						 public void onClick(DialogInterface dialog, int which) {
							 // Cancel should not do anything.
						 }

					 }).create();


		 case DIALOG_DELETE:
			 return new AlertDialog.Builder(this).setTitle(getString(R.string.really_delete, mContextText))
					 .setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(
							 android.R.string.ok, new OnClickListener() {

								 public void onClick(DialogInterface dialog, int which) {
									 deleteFileOrFolder(mContextFile);
								 }

							 }).setNegativeButton(android.R.string.cancel, new OnClickListener() {

								 public void onClick(DialogInterface dialog, int which) {
									 // Cancel should not do anything.
								 }

							 }).create();

		 case DIALOG_RENAME:
			 inflater = LayoutInflater.from(this);
			 view = inflater.inflate(R.layout.dialog_new_folder, null);
			 final EditText et2 = (EditText) view
					 .findViewById(R.id.foldername);
			 return new AlertDialog.Builder(this)
			 .setTitle(R.string.menu_rename).setView(view).setPositiveButton(
					 android.R.string.ok, new OnClickListener() {

						 public void onClick(DialogInterface dialog, int which) {

							 renameFileOrFolder(mContextFile, et2.getText().toString());
						 }

					 }).setNegativeButton(android.R.string.cancel, new OnClickListener() {

						 public void onClick(DialogInterface dialog, int which) {
							 // Cancel should not do anything.
						 }

					 }).create();

		 case DIALOG_MULTI_DELETE:
			 String contentText = null;
			 int count = 0;
			 for (IconifiedText it : mDirectoryEntries) {
				 if (!it.isSelected()) {
					 continue;
				 }

				 contentText = it.getText();
				 count++;
			 }
			 String string;
			 if (count == 1) {
				 string = getString(R.string.really_delete, contentText);
			 } else {
				 string = getString(R.string.really_delete_multiselect, count);
			 }
			 return new AlertDialog.Builder(this).setTitle(string)
					 .setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(
							 android.R.string.ok, new OnClickListener() {

								 public void onClick(DialogInterface dialog, int which) {
									 deleteMultiFile();

									 Intent intent = getIntent();
									 setResult(RESULT_OK, intent);
									 finish();
								 }

							 }).setNegativeButton(android.R.string.cancel, new OnClickListener() {

								 public void onClick(DialogInterface dialog, int which) {
									 // Cancel should not do anything.
								 }

							 }).create();

		 }
		 return super.onCreateDialog(id);

	 }

	 @Override
	 protected void onPrepareDialog(int id, Dialog dialog) {
		 super.onPrepareDialog(id, dialog);

		 switch (id) {
		 case DIALOG_NEW_FOLDER:
			 EditText et = (EditText) dialog.findViewById(R.id.foldername);
			 et.setText("");
			 break;

		 case DIALOG_DELETE:
			 ((AlertDialog) dialog).setTitle(getString(R.string.really_delete, mContextText));
			 break;

		 case DIALOG_RENAME:
			 et = (EditText) dialog.findViewById(R.id.foldername);
			 et.setText(mContextText);
			 TextView tv = (TextView) dialog.findViewById(R.id.foldernametext);
			 if (mContextFile.isDirectory()) {
				 tv.setText(R.string.file_name);
			 } else {
				 tv.setText(R.string.file_name);
			 }
			 ((AlertDialog) dialog).setIcon(mContextIcon);
			 break;

		 case DIALOG_MULTI_DELETE:
			 break;

		 }
	 }

	 private void includeInMediaScan() {
		 // Delete the .nomedia file.
		 GenericFileInterface file = FileUtils.getFile(mFileSystem, currentDirectory, NOMEDIA_FILE);
		 if (file.delete()) {
			 Toast.makeText(this, getString(R.string.media_scan_included), Toast.LENGTH_LONG).show();
			 mNoMedia = false;
		 } else {
			 // That didn't work.
			 Toast.makeText(this, getString(R.string.error_generic), Toast.LENGTH_LONG).show();
		 }
	 }

	 private void excludeFromMediaScan() {
		 // Create the .nomedia file.
		 GenericFileInterface file = FileUtils.getFile(mFileSystem, currentDirectory, NOMEDIA_FILE);
		 try {
			 if (file.createNewFile()) {
				 mNoMedia = true;
				 Toast.makeText(this, getString(R.string.media_scan_excluded), Toast.LENGTH_LONG).show();
			 } else {
				 Toast.makeText(this, getString(R.string.error_media_scan), Toast.LENGTH_LONG).show();
			 }
		 } catch (IOException e) {
			 // That didn't work.
			 Toast.makeText(this, getString(R.string.error_generic) + e.getMessage(), Toast.LENGTH_LONG).show();
		 }
	 }

	 private boolean checkSelection() {
		 for (IconifiedText it : mDirectoryEntries) {
			 if (!it.isSelected()) {
				 continue;
			 }

			 return true;
		 }

		 Toast.makeText(this, R.string.error_selection, Toast.LENGTH_SHORT).show();

		 return false;
	 }

	 private void promptDestinationAndMoveFile() {

		 Intent intent = new Intent(FileManagerIntents.ACTION_PICK_DIRECTORY);

		 intent.setData(FileUtils.getUri(currentDirectory));

		 intent.putExtra(FileManagerIntents.EXTRA_TITLE, getString(R.string.move_title));
		 intent.putExtra(FileManagerIntents.EXTRA_BUTTON_TEXT, getString(R.string.move_button));
		 intent.putExtra(FileManagerIntents.EXTRA_WRITEABLE_ONLY, true);

		 startActivityForResult(intent, REQUEST_CODE_MOVE);
	 }

	 private void promptDestinationAndCopyFile() {
		 Intent intent = new Intent(FileManagerIntents.ACTION_PICK_DIRECTORY);

		 intent.setData(FileUtils.getUri(currentDirectory));

		 intent.putExtra(FileManagerIntents.EXTRA_TITLE, getString(R.string.copy_title));
		 intent.putExtra(FileManagerIntents.EXTRA_BUTTON_TEXT, getString(R.string.copy_button));
		 intent.putExtra(FileManagerIntents.EXTRA_WRITEABLE_ONLY, true);

		 startActivityForResult(intent, REQUEST_CODE_COPY);
	 }

	 /**
	  * Starts activity for multi select.
	  */
	 private void promptMultiSelect() {
		 Intent intent = new Intent(FileManagerIntents.ACTION_MULTI_SELECT);

		 intent.setData(FileUtils.getUri(currentDirectory));

		 intent.putExtra(FileManagerIntents.EXTRA_TITLE, getString(R.string.multiselect_title));
		 //intent.putExtra(FileManagerIntents.EXTRA_BUTTON_TEXT, getString(R.string.move_button));

		 startActivityForResult(intent, REQUEST_CODE_MULTI_SELECT);
	 }

	 private void createNewFolder(String foldername) {
		 if (!TextUtils.isEmpty(foldername)) {
			 GenericFileInterface file = FileUtils.getFile(mFileSystem, currentDirectory, foldername);
			 if (file.mkdirs()) {

				 // Change into new directory:
				 browseTo(file);
			 } else {
				 Toast.makeText(this, R.string.error_creating_new_folder, Toast.LENGTH_SHORT).show();
			 }
		 }
	 }

	 /*! Recursively delete a directory and all of its children.
	  *  @params toastOnError If set to true, this function will toast if an error occurs.
	  *  @returns true if successful, false otherwise.
	  */
	 private boolean recursiveDelete(GenericFileInterface file, boolean toastOnError) {
		 // Recursively delete all contents.
		 GenericFileInterface[] files = file.listFiles();

		 if (files == null) {
			 Toast.makeText(this, getString(R.string.error_deleting_folder, file.getAbsolutePath()), Toast.LENGTH_LONG);
			 return false;
		 }

		 for (int x=0; x<files.length; x++) {
			 GenericFileInterface childFile = files[x];
			 if (childFile.isDirectory()) {
				 if (!recursiveDelete(childFile, toastOnError)) {
					 return false;
				 }
			 } else {
				 if (!childFile.delete()) {
					 Toast.makeText(this, getString(R.string.error_deleting_child_file, childFile.getAbsolutePath()), Toast.LENGTH_LONG);
					 return false;
				 }
			 }
		 }

		 if (!file.delete()) {
			 Toast.makeText(this, getString(R.string.error_deleting_folder, file.getAbsolutePath()), Toast.LENGTH_LONG);
			 return false;
		 }

		 return true;
	 }

	 private class RecursiveDeleteTask extends GenericAsyncTask<Object, Void, Integer> {

		 private FileManagerActivity activity = FileManagerActivity.this;
		 private static final int success = 0;
		 private static final int err_deleting_folder = 1;
		 private static final int err_deleting_child_file = 2;
		 private static final int err_deleting_file = 3;

		 private GenericFileInterface errorFile;

		 /**
		  * Recursively delete a file or directory and all of its children.
		  * 
		  * @returns 0 if successful, error value otherwise.
		  */
		 private int recursiveDelete(GenericFileInterface file) {
			 if (file.isDirectory() && file.listFiles() != null)
				 for (GenericFileInterface childFile : file.listFiles()) {
					 if (childFile.isDirectory()) {
						 int result = recursiveDelete(childFile);
						 if (result > 0) {
							 return result;
						 }
					 } else {
						 if (!childFile.delete()) {
							 errorFile = childFile;
							 return err_deleting_child_file;
						 }
					 }
				 }

			 if (!file.delete()) {
				 errorFile = file;
				 return file.isFile() ? err_deleting_file : err_deleting_folder;
			 }

			 return success;
		 }

		 @Override
		 protected void onPreExecute() {
			 Toast.makeText(activity, R.string.deleting_files, Toast.LENGTH_SHORT).show();
		 }

		 @SuppressWarnings("unchecked")
		 @Override
		 protected Integer doInBackground(Object... params) {
			 Object files = params[0];

			 if (files instanceof List<?>) {
				 for (GenericFileInterface file: (List<GenericFileInterface>)files) {
					 int result = recursiveDelete(file);
					 if (result != success) return result;
				 }
				 return success;
			 } else
				 return recursiveDelete((GenericFileInterface)files);

		 }

		 @Override
		 protected void onPostExecute(Integer result) {
			 switch (result) {
			 case success:
				 activity.refreshList();
				 Toast.makeText(activity, R.string.folder_deleted,Toast.LENGTH_SHORT).show();
				 break;
			 case err_deleting_folder:
				 Toast.makeText(activity,getString(R.string.error_deleting_folder,
						 errorFile.getAbsolutePath()), Toast.LENGTH_LONG).show();
				 break;
			 case err_deleting_child_file:
				 Toast.makeText(activity,getString(R.string.error_deleting_child_file,
						 errorFile.getAbsolutePath()),Toast.LENGTH_SHORT).show();
				 break;
			 case err_deleting_file:
				 Toast.makeText(activity,getString(R.string.error_deleting_file,
						 errorFile.getAbsolutePath()), Toast.LENGTH_LONG).show();
				 break;
			 }
		 }

	 }

	 private void deleteFileOrFolder(GenericFileInterface file) {

		 new RecursiveDeleteTask().executeOnExecutor(GenericAsyncTask.THREAD_POOL_EXECUTOR, file);
		 //		if (file.isDirectory()) {
		 //			if (recursiveDelete(file, true)) {
		 //				refreshList();
		 //				Toast.makeText(this, R.string.folder_deleted, Toast.LENGTH_SHORT).show();
		 //			}
		 //		} else {
		 //			if (file.delete()) {
		 //				// Delete was successful.
		 //				refreshList();
		 //				Toast.makeText(this, R.string.file_deleted, Toast.LENGTH_SHORT).show();
		 //			} else {
		 //				Toast.makeText(this, R.string.error_deleting_file, Toast.LENGTH_SHORT).show();
		 //			}
		 //		}
	 }

	 private void deleteMultiFile() {
		 //        int toast = 0;
		 LinkedList<GenericFileInterface> files = new LinkedList<GenericFileInterface>();
		 for (IconifiedText it : mDirectoryEntries) {
			 if (!it.isSelected()) {
				 continue;
			 }

			 GenericFileInterface file = FileUtils.getFile(mFileSystem, currentDirectory, it.getText());
			 files.add(file);
			 //            if (file.isDirectory()) {
			 //                if (!recursiveDelete(file, true)) {
			 //                    break;
			 //                }
			 //            } else {
			 //                if (!file.delete()) {
			 //                    toast = R.string.error_deleting_file;
			 //                    break;
			 //                }
			 //            }
		 }

		 new RecursiveDeleteTask().executeOnExecutor(GenericAsyncTask.THREAD_POOL_EXECUTOR, files);

		 //        if (toast == 0) {
		 //            // Delete was successful.
		 //            refreshList();
		 //            toast = R.string.file_deleted;
		 //        }
		 //
		 //        Toast.makeText(FileManagerActivity.this, toast, Toast.LENGTH_SHORT).show();
	 }

	 private void renameFileOrFolder(GenericFileInterface file, String newFileName) {

		 if (newFileName != null && newFileName.length() > 0){
			 if (newFileName.lastIndexOf('.') < 0){				
				 newFileName += FileUtils.getExtension(file.getName()); 
			 }
		 }
		 GenericFileInterface newFile = FileUtils.getFile(mFileSystem, currentDirectory, newFileName);

		 rename(file, newFile);
	 }

	 /**
	  * @param oldFile
	  * @param newFile
	  */
	 private void rename(GenericFileInterface oldFile, GenericFileInterface newFile) {
		 int toast = 0;
		 if (oldFile.renameTo(newFile)) {
			 // Rename was successful.
			 refreshList();
			 if (newFile.isDirectory()) {
				 toast = R.string.folder_renamed;
			 } else {
				 toast = R.string.file_renamed;
			 }
		 } else {
			 if (newFile.isDirectory()) {
				 toast = R.string.error_renaming_folder;
			 } else {
				 toast = R.string.error_renaming_file;
			 }
		 }
		 Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
	 }

	 /*@ RETURNS: A file name that is guaranteed to not exist yet.
	  * 
	  * PARAMS:
	  *   context - Application context.
	  *   path - The path that the file is supposed to be in.
	  *   fileName - Desired file name. This name will be modified to
	  *     create a unique file if necessary.
	  * 
	  */
	 private GenericFileInterface createUniqueCopyName(Context context, GenericFileInterface path, String fileName) {
		 // Does that file exist?
		 GenericFileInterface file = FileUtils.getFile(mFileSystem, path, fileName);

		 if (!file.exists()) {
			 // Nope - we can take that.
			 return file;
		 }

		 // Split file's name and extension to fix internationalization issue #307
		 int fromIndex = fileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
		 String extension = "";
		 if (fromIndex > 0) {
			 extension = fileName.substring(fromIndex);
			 fileName = fileName.substring(0, fromIndex);
		 }

		 // Try a simple "copy of".
		 file = FileUtils.getFile(mFileSystem, path, context.getString(R.string.copied_file_name, fileName).concat(extension));

		 if (!file.exists()) {
			 // Nope - we can take that.
			 return file;
		 }

		 int copyIndex = 2;

		 // Well, we gotta find a unique name at some point.
		 while (copyIndex < 500) {
			 file = FileUtils.getFile(mFileSystem, path, context.getString(R.string.copied_file_name_2, copyIndex, fileName).concat(extension));

			 if (!file.exists()) {
				 // Nope - we can take that.
				 return file;
			 }

			 copyIndex++;
		 }

		 // I GIVE UP.
		 return null;
	 }

	 private boolean copy(GenericFileInterface oldFile, GenericFileInterface newFile) {
		 try {
			 FileInputStream input = new FileInputStream(oldFile.getNativeFile());
			 FileOutputStream output = new FileOutputStream(newFile.getNativeFile());

			 byte[] buffer = new byte[COPY_BUFFER_SIZE];

			 while (true) {
				 int bytes = input.read(buffer);

				 if (bytes <= 0) {
					 break;
				 }

				 output.write(buffer, 0, bytes);
			 }

			 output.close();
			 input.close();

		 } catch (Exception e) {
			 return false;
		 }
		 return true;
	 }

	 private void sendFile(GenericFileInterface file) {

		 String filename = file.getName();
		 String content = "hh";

		 Log.i(TAG, "Title to send: " + filename);
		 Log.i(TAG, "Content to send: " + content);

		 Intent i = new Intent();
		 i.setAction(Intent.ACTION_SEND);
		 i.setType(mMimeTypes.getMimeType(file.getName()));
		 i.putExtra(Intent.EXTRA_SUBJECT, filename);
		 //i.putExtra(Intent.EXTRA_STREAM, FileUtils.getUri(file));
		 i.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://" + FileManagerProvider.AUTHORITY + file.getAbsolutePath()));

		 i = Intent.createChooser(i, getString(R.string.menu_send));

		 try {
			 startActivity(i);
		 } catch (ActivityNotFoundException e) {
			 Toast.makeText(this, R.string.send_not_available,
					 Toast.LENGTH_SHORT).show();
			 Log.e(TAG, "Email client not installed");
		 }
	 }

	 // This code seems to work for SDK 2.3 (target="9")
	 @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {

		 if (keyCode == KeyEvent.KEYCODE_BACK) {
			 if (mStepsBack > 0) {
				 upOneLevel();
				 return true;
			 }
		 }

		 return super.onKeyDown(keyCode, event);
	 }

	 // For targetSdkVersion="5" or higher, one needs to use the following code instead of the one above:
	 // (See http://android-developers.blogspot.com/2009/12/back-and-other-hard-keys-three-stories.html )

	 /*
	//@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
	            && keyCode == KeyEvent.KEYCODE_BACK
	            && event.getRepeatCount() == 0) {
	        // Take care of calling this method on earlier versions of
	        // the platform where it doesn't exist.
	        onBackPressed();
	    }

	    return super.onKeyDown(keyCode, event);
	}

	//@Override
	public void onBackPressed() {
	    // This will be called either automatically for you on 2.0
	    // or later, or by the code above on earlier versions of the
	    // platform.
		if (mStepsBack > 0) {
			upOneLevel();
		} else {
			finish();
		}
	}
	  */

	 /**
	  * This is called after the file manager finished.
	  */
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 super.onActivityResult(requestCode, resultCode, data);

		 switch (requestCode) {
		 case REQUEST_CODE_MOVE:
			 if (resultCode == RESULT_OK && data != null) {
				 // obtain the filename
				 GenericFileInterface movefrom = mContextFile;
				 GenericFileInterface moveto = FileUtils.getFile(mFileSystem, data.getData());
				 if (moveto != null) {
					 if (mState != STATE_MULTI_SELECT) {
						 // Move single file.
						 moveto = FileUtils.getFile(mFileSystem, moveto, movefrom.getName());
						 int toast = 0;
						 if (movefrom.renameTo(moveto)) {
							 // Move was successful.
							 refreshList();
							 if (moveto.isDirectory()) {
								 toast = R.string.folder_moved;
							 } else {
								 toast = R.string.file_moved;
							 }
						 } else {
							 if (moveto.isDirectory()) {
								 toast = R.string.error_moving_folder;
							 } else {
								 toast = R.string.error_moving_file;
							 }
						 }
						 Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
					 } else {
						 // Move multi file.
						 int toast = 0;
						 for (IconifiedText it : mDirectoryEntries) {
							 if (!it.isSelected()) {
								 continue;
							 }

							 movefrom = FileUtils.getFile(mFileSystem, currentDirectory, it.getText());
							 GenericFileInterface newPath = FileUtils.getFile(mFileSystem, moveto, movefrom.getName());
							 if (!movefrom.renameTo(newPath)) {
								 refreshList();
								 if (moveto.isDirectory()) {
									 toast = R.string.error_moving_folder;
								 } else {
									 toast = R.string.error_moving_file;
								 }
								 break;
							 }
						 }

						 if (toast == 0) {
							 // Move was successful.
							 refreshList();
							 toast = R.string.file_moved;
						 }

						 Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();

						 Intent intent = getIntent();
						 setResult(RESULT_OK, intent);
						 finish();
					 }

				 }				

			 }
			 break;

		 case REQUEST_CODE_COPY:
			 if (resultCode == RESULT_OK && data != null) {
				 // obtain the filename
				 GenericFileInterface copyfrom = mContextFile;
				 GenericFileInterface copyto = FileUtils.getFile(mFileSystem, data.getData());
				 if (copyto != null) {
					 if (mState != STATE_MULTI_SELECT) {
						 // Copy single file.
						 copyto = createUniqueCopyName(this, copyto, copyfrom.getName());

						 if (copyto != null) {
							 int toast = 0;
							 if (copy(copyfrom, copyto)) {
								 toast = R.string.file_copied;
								 refreshList();
							 } else {
								 toast = R.string.error_copying_file;
							 }
							 Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
						 }
					 } else {
						 // Copy multi file.
						 int toast = 0;
						 for (IconifiedText it : mDirectoryEntries) {
							 if (!it.isSelected()) {
								 continue;
							 }

							 copyfrom = FileUtils.getFile(mFileSystem, currentDirectory, it.getText());
							 GenericFileInterface newPath = createUniqueCopyName(this, copyto, copyfrom.getName());
							 if (copyto != null) {
								 if (!copy(copyfrom, newPath)) {
									 toast = R.string.error_copying_file;
									 break;
								 }
							 }
						 }

						 if (toast == 0) {
							 // Copy was successful.
							 toast = R.string.file_copied;
							 refreshList();
						 }

						 Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();

						 Intent intent = getIntent();
						 setResult(RESULT_OK, intent);
						 finish();
					 }
				 }				
			 }
			 break;

		 case REQUEST_CODE_MULTI_SELECT:
			 if (resultCode == RESULT_OK && data != null) {
				 refreshList();
			 }
			 break;
		 }
	 }
	 
	@Override
    public void onBackPressed() {
	    super.onBackPressed();

	    AppState.logX(TAG, "onBackPressed");

	    Intent intent = getIntent();
	    intent.putExtra(FileManagerIntents.ACTION_PICK_FILE, Utils.EMPTY_STRING);
	    setResult(RESULT_CANCELED, intent);
	    finish();
	 }
}