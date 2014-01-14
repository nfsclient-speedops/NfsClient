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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

import com.app.nfsclient.generic.GenericAppStateInitAsyncTask;
import com.app.nfsclient.generic.GenericListItem;
import com.app.nfsclient.generic.GenericListItemList;
import com.app.nfsclient.generic.GenericStorage;
import com.app.nfsclient.generic.GenericStorageInterface;
import com.app.nfsclient.images.ImagesUtils;
import com.app.nfsclient.servers.Server;
import com.app.nfsclient.servers.ServersDatabaseAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;

public class AppState {
    private static final String TAG = "AppState";
        
    public static final String VERSION = "1.0";
    public static final String APP_PACKAGE_NAME = "com.app.nfsclient";
		    
    public static Activity globalActivity = null;
    
    public static final int DATABASE_VERSION = 1;

    private static boolean developerMode = true;
    
    private static final String SHARED_PREFERENCES_NAME = "appPreferences";
	    	        
    public static final String appRootDirectoryName = "NfsClient";
    public static final String appDataDirectoryName = "data";
    private static final String appLogFilename = "log";
    public static final String appDropboxDirectoryName = "dropbox";

    private static AppLog appLog;
    private static boolean appDebugEnabled = true;
    private static Context initialContext = null;

    public static int BACK_BUTTON_VISIBILITY = View.INVISIBLE;
    
    public static boolean developerModeGet() {
    	return developerMode;
    }
    
    public static void stateInit(Activity activity) {
    	androidLogX(TAG, "stateInit");

    	fileSystemInit(activity);
    	Utils.dimensionsInit(activity);
    	
    	// initialize the default images
    	ImagesUtils.transparentDefaultImagesInit(activity);
    	ImagesUtils.itemDefaultImagesInit(activity);
    	ImagesUtils.dropboxDefaultImagesInit(activity);
  
    	// create the folder and file image files
    	
    	// initialize the cloud accounts list
    	cloudStorageProvidersInit(activity);

    	initialContext = activity;
    }
    
    public static synchronized Context initialContextGet() {
    	return initialContext;
    }
    
    //XXX file system
	private static void fileSystemInit(Context context) {
		androidLogX(TAG, "fileSystemInit");

		// initialize the external storage (sdcard) or internal storage
		File appRootDirectoryFile;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			androidLogX(TAG, "fileSystemInit: using the external storage");

			File externalStorageDirectoryFile = Environment.getExternalStorageDirectory();
			androidLogX(TAG, String.format("fileSystemInit: ext storage dir %s",
				externalStorageDirectoryFile.getAbsolutePath()));

			appRootDirectoryFile = new File(externalStorageDirectoryFile, appRootDirectoryName);
			if (appRootDirectoryFile.exists() == false) {
				if (!appRootDirectoryFile.mkdir()) {
					androidLogX(TAG, String.format("fileSystemInit: unable to create app directory = %s",
						appRootDirectoryName));
				}
			} else {
				androidLogX(TAG, String.format("fileSystemInit: app directory %s exists",
				    appRootDirectoryName));
			}
		} else if ((appRootDirectoryFile = context.getFilesDir()) != null) {
			androidLogX(TAG, "fileSystemInit: using the internal storage");

		} else {
			androidLogX(TAG, "fileSystemInit: internal/external storage does not exist");
		}

		// initialize the subdirectories
		File appDataDirectoryFile;
		File appDropboxDirectoryFile;
		if (appRootDirectoryFile != null) {
			appDataDirectoryFile = new File(appRootDirectoryFile, appDataDirectoryName);
			if (appDataDirectoryFile.exists() == false) {
				if (!appDataDirectoryFile.mkdir())
					androidLogX(TAG, String.format("fileSystemInit: unable to create the app directory = %s",
						appDataDirectoryName));
			} else {
				androidLogX(TAG, String.format("fileSystemInit: the app directory %s exists",
					appDataDirectoryName));
			}
			
			// initialize the Dropbox mirror directory
			appDropboxDirectoryFile = new File(appRootDirectoryFile, appDropboxDirectoryName);
			if (appDropboxDirectoryFile.exists() == false) {
				if (!appDropboxDirectoryFile.mkdir())
					androidLogX(TAG, String.format("fileSystemInit: unable to create the app directory = %s",
						appDropboxDirectoryName));
			} else {
				androidLogX(TAG, String.format("fileSystemInit: the app directory %s exists",
					appDataDirectoryName));
			}
		}
		
		// initialize the image files
		ImagesUtils.directoryImageInit(context);
		ImagesUtils.fileImageInit(context);
		
		// initialize the debug log
		if (appDebugEnabled) {
			appLog = new AppLog(appLogFilename);
			if (appLog != null) {
				System.setOut(appLog.logStream);
				System.setErr(appLog.logStream);
			}
		}
	}
	
	public static File appRootDirectoryFileGet(Context context) {
		return new File(Environment.getExternalStorageDirectory(), appRootDirectoryName);
	}
	
	public static File appDataDirectoryFileGet(Context context) {
		return new File(appRootDirectoryFileGet(context), appDataDirectoryName);
	}
	
	public static File appDropboxDirectoryFileGet(Context context) {
		return new File(appRootDirectoryFileGet(context), appDropboxDirectoryName);
	}
	
	public static boolean dataCacheIsEnabled(Context context) {
		return booleanGet(context, App.DATA_CACHE_ENABLE_KEY, App.DEFAULT_CACHE_ENABLE);
	}
	
	//XXX start shared preferences
	public static SharedPreferences sharedPreferencesGet(Context context) {
		return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
	}
	
	public static synchronized boolean contains(Context context, String key) {
		return sharedPreferencesGet(context).contains(key);
	}

	public static synchronized boolean tokenGet(Context context, String key) {
		Editor editor = sharedPreferencesGet(context).edit();
		boolean result = false;
		if (!booleanGet(context, key, false)) {
		    editor.putBoolean(key, true);
		    editor.commit();
		    result = true;
		}
		
		return result;
	}
	
	public static synchronized boolean booleanGet(Context context, String key, boolean defValue) {
		return sharedPreferencesGet(context).getBoolean(key, defValue);
	}
	public static synchronized void booleanSet(Context context, String key, boolean value) {
		Editor editor = sharedPreferencesGet(context).edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public static synchronized float floatGet(Context context, String key, float defValue) {
        return sharedPreferencesGet(context).getFloat(key, defValue);
	}
	public static synchronized void floatSet(Context context, String key, float value) {
		Editor editor = sharedPreferencesGet(context).edit();
		editor.putFloat(key, value);
		editor.commit();
	}
	
	public static synchronized int intGet(Context context, String key, int defValue) {
        return sharedPreferencesGet(context).getInt(key, defValue);
	}
	public static synchronized void intSet(Context context, String key, int value) {
		Editor editor = sharedPreferencesGet(context).edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public static synchronized long longGet(Context context, String key, long defValue) {
		return sharedPreferencesGet(context).getLong(key, defValue);
	}
	public static synchronized void longSet(Context context, String key, long value) {
		Editor editor = sharedPreferencesGet(context).edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	public static synchronized String stringGet(Context context, String key, String defValue) {
		return sharedPreferencesGet(context).getString(key, defValue);
	}
	public static synchronized void stringSet(Context context, String key, String value) {
		Editor editor = sharedPreferencesGet(context).edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	private static String ARRAY_LENGTH_SUFFIX = "_ARRAY_LENGTH";
	public static synchronized String[] stringArrayGet(Context context, String key, String defValue) {
		String[] array = new String[0];
		int length = sharedPreferencesGet(context).getInt(key + ARRAY_LENGTH_SUFFIX, 0);
		if (length > 0) {
			array = new String[length];
			for (int i = 0; i < length; i++)
				array[i] = sharedPreferencesGet(context).getString(key + i, defValue);
		}
		return array;
	}
	public static synchronized List<String> stringListGet(Context context, String key, String defValue) {
		List<String> arrayList = new ArrayList<String>();
		int length = sharedPreferencesGet(context).getInt(key + ARRAY_LENGTH_SUFFIX, 0);
		if (length > 0) {
			for (int i = 0; i < length; i++)
				arrayList.add(sharedPreferencesGet(context).getString(key + i, defValue));
		}
		return arrayList;
	}
	public static synchronized void stringArraySet(Context context, String key, String[] array) {
		Editor editor = sharedPreferencesGet(context).edit();
		if (array != null) {
			editor.putInt(key + ARRAY_LENGTH_SUFFIX, array.length);
			for (int i = 0; i < array.length; i++)
				editor.putString(key + i, array[i]);
		} else {
			editor.putInt(key + ARRAY_LENGTH_SUFFIX, 0);
		}
	    
		editor.commit();
	}
	public static synchronized void stringArraySet(Context context, String key, Object[] array) {
		Editor editor = sharedPreferencesGet(context).edit();
		if (array != null) {
			editor.putInt(key + ARRAY_LENGTH_SUFFIX, array.length);
			for (int i = 0; i < array.length; i++)
				editor.putString(key + i, (String)array[i]);
		} else {
			editor.putInt(key + ARRAY_LENGTH_SUFFIX, 0);
		}
		
	    editor.commit();
	}
	// end shared preferences
	
	public static void registerOnSharedPreferenceChangeListener(Context context,
		OnSharedPreferenceChangeListener listener) {
		sharedPreferencesGet(context).registerOnSharedPreferenceChangeListener(listener);
	}

	public static void unregisterOnSharedPreferenceChangeListener(Context context,
		OnSharedPreferenceChangeListener listener) {
		sharedPreferencesGet(context).unregisterOnSharedPreferenceChangeListener(listener);
	}
   
	//XXX start nfs servers
	public static class ServersInitAsyncTask extends GenericAppStateInitAsyncTask {
		private static final String TAG = AppState.TAG + ":ServersInitAsyncTask";
				
		public ServersInitAsyncTask() {
			super(TAG, null);
		}

		@Override
		protected Void doInBackground(Object... params) {
			androidLogX(TAG, "doInBackground");
			
			context = (Context)params[0];
			serversInit(context);

			return null;
		}
	}
	public static void serversInit(Context context) {
	    serversDatabaseGet(context);
	}
	public static ServersDatabaseAdapter serversDatabaseGet(Context context) {
		return new ServersDatabaseAdapter(context);
	}
	public static List<GenericListItem> serversListGet(Context context) {	
		ServersDatabaseAdapter database = new ServersDatabaseAdapter(context);
	    List<GenericListItem> list = database.serversListGet();
	    database.close();
	    return list;
    }
	public static Server serversGet(Context context, int index) {
	    ServersDatabaseAdapter database = new ServersDatabaseAdapter(context);
        Server server = database.serversGet(index);
        database.close();
        return server;
    }
	public static int serversSize(Context context) {
		ServersDatabaseAdapter database = new ServersDatabaseAdapter(context);
        int size = database.serversSize();
        database.close();
		return size;
	}
    public static void serversAdd(Context context, Server server) {
    	logX(TAG, String.format("serversAdd 1: server = %s", server.primaryNameGet()));

    	ServersDatabaseAdapter database = new ServersDatabaseAdapter(context);
        database.insert(server);
        database.close();
    }
    public static void serversDelete(Context context, Server server, boolean deleteFromDatabase) {
    	ServersDatabaseAdapter database = new ServersDatabaseAdapter(context);
    	if (deleteFromDatabase)
            database.serversTableDelete(server);
        database.close();
    }
    public static void serversUpdate(Context context, Server server) {
    	ServersDatabaseAdapter database = new ServersDatabaseAdapter(context);
        database.update(server);
        database.close();
    }
    public static Server serversGetByInternetAddress(Context context, String internetAddress) {
    	ServersDatabaseAdapter database = new ServersDatabaseAdapter(context);
        Server server = database.serversGetByInternetAddress(internetAddress);
        database.close();
    	return server;
    }
    public static Server serversGetByHostName(Context context, String hostName) {
    	ServersDatabaseAdapter database = new ServersDatabaseAdapter(context);
        Server server = database.serversGetByHostName(hostName);
        database.close();
    	return server;
    }
    public static List<String> serverExportDirectoriesGet(Context context, String serverInternetAddress) {
    	List<String> exports = null;
    	Server server = AppState.serversDatabaseGet(context).serversGetByInternetAddress(serverInternetAddress);
    	if (server != null)
    		exports = server.serverExportDirectoriesGet();
    	else
    		exports = new ArrayList<String>();
    	
		return exports;
    }
    // end nfs servers
    
    //XXX start storage providers
    public static synchronized GenericListItemList storageProvidersGet(Context context) {
    	logX(TAG, "storageProvidersGet");
    	
    	GenericListItemList providers = new GenericListItemList();
    	providers.add(GenericListItem.ITEM_TYPE_STORAGE_PROVIDER_LOCAL,
    		GenericListItem.ITEM_TYPE_STORAGE_PROVIDER_LOCAL);
    	for (GenericListItem cloudProvider : cloudStorageProvidersGet(context))
   			providers.add(cloudProvider);
    	
    	return providers;
    }
    public static synchronized GenericListItemList storageProviderLocationsGetByProvider(Context context,
    	String provider) {
    	logX(TAG, String.format("storageProviderLocationsGetByProvider: provider = %s", provider));

    	GenericListItemList list = new GenericListItemList();
    	if (provider.equalsIgnoreCase(GenericListItem.ITEM_TYPE_STORAGE_PROVIDER_LOCAL)) {
    		list.add(GenericListItem.ITEM_TYPE_STORAGE_LOCATION_LOCAL);
    	} else {
    		for (GenericListItem item : cloudStorageProvidersListGet(context)) {
    			GenericStorage account = (GenericStorage)item;
    			logX(TAG, String.format("storageProviderLocationsGetByProvider: item = %s, type = %s",
    					account.primaryNameGet(), account.storageTypeGet().toString()));

    			if (account.storageTypeGet().toString().equalsIgnoreCase(provider))
    				list.add(item);
    		}
    	}

    	return list;
    }
    // end storage providers
    
    //XXX start cloud storage providers
    private static final String[] cloudStorageProviders = new String[] {
    };
    public static class CloudStorageProvidersInitAsyncTask extends GenericAppStateInitAsyncTask {
    	private static final String TAG = AppState.TAG + ":CloudStorageProvidersInitAsyncTask";
				
		public CloudStorageProvidersInitAsyncTask() {
			super(TAG, null);
		}

		@Override
		protected Void doInBackground(Object... params) {
			androidLogX(TAG, "doInBackground");
			
			context = (Context)params[0];
			cloudStorageProvidersInit(context);
			initTasksProgressUpdate();
			
			return null;
		}
	}
    public static synchronized void cloudStorageProvidersInit(Context context) {
    	
    }
    public static synchronized List<GenericListItem> cloudStorageProvidersListGet(Context context) {
    	// add the supported cloud account types
    	List<GenericListItem> list = null;
    	
    	return list;
    }
    public static synchronized GenericListItemList cloudStorageProvidersGet(Context context) {
    	logX(TAG, "cloudStorageProvidersGet");

    	GenericListItemList providers = new GenericListItemList();
    	for (String cloudProvider : cloudStorageProviders)
    		if (storageProviderLocationsGetByProvider(context, cloudProvider).size() > 0)
    			providers.add(cloudProvider);
    	
    	return providers;
    }
    public static synchronized GenericListItem cloudStorageProvidersGet(Context context, int index) {
    	return cloudStorageProvidersListGet(context).get(index);
    }
    public static synchronized int cloudStorageProvidersIndexOf(Context context, GenericListItem account) {
    	return cloudStorageProvidersListGet(context).indexOf(account);
    }
    public static synchronized int cloudStorageProvidersSize(Context context) {    	
    	return cloudStorageProvidersListGet(context).size();
    }
    public static synchronized GenericListItem cloudStorageProvidersGetByNameAndType(Context context,
    	String name, String type) {
    	logX(TAG, String.format("cloudStorageProvidersGetByNameAndType: name = %s, type = %s", name, type));
    	
    	GenericListItem account = null;
    	for (GenericListItem item : cloudStorageProvidersListGet(context)) {
    		GenericStorage ca = (GenericStorage)item;
    		
    		if (ca.primaryNameGet().equalsIgnoreCase(name) &&
    			ca.storageTypeGet().toString().equalsIgnoreCase(type)) {
    			account = item;
    			break;
    		}
    	}
    	
    	return account;
    }
    public static synchronized int cloudStorageProvidersIndexOfByNameAndType(Context context, String name,
    	String type) {
    	logX(TAG, String.format("cloudStorageProvidersIndexOfByNameAndType: name = %s, type = %s", name, type));

    	int index = -1;
    	for (int i = 0; i < cloudStorageProvidersListGet(context).size(); i++) {
    		GenericStorage account = (GenericStorage)cloudStorageProvidersGet(context, i);

    		if (account.primaryNameGet().equalsIgnoreCase(name) &&
    			account.storageTypeGet().toString().equalsIgnoreCase(type)) {
    			index = i;
    			break;
    		}
    	}

    	return index;
    }
    public static synchronized String[] cloudStorageProvidersEntriesGet(Context context) {
    	List<GenericListItem> list = cloudStorageProvidersListGet(context);
    	String[] entries = null;

    	if (list.size() > 0) {
    	    entries = new String[list.size()];
    	    
    	    for (int i = 0; i < list.size(); i++) {
    	    	GenericStorage account = (GenericStorage)list.get(i);
    	    	
    	    	entries[i] = String.format("%s %s", account.primaryNameGet(),
    	    		account.storageTypeGet().toString());;
    	    }
    	}

    	return (entries);
    }
    public static synchronized GenericListItem[] cloudStorageProvidersEntriesValuesGet(Context context) {
    	return (cloudStorageProvidersListGet(context).toArray(new GenericListItem[1]));
    }
    // end cloud storage providers

    // start storage
    public static synchronized String storageDatabasesFolderGet() {
    	return GenericStorageInterface.PUBLIC_DATABASES_FOLDER_NAME;
    }
    // end storage
    
    // XXX start action bar
    public static synchronized void actionBarSet(Context context, ActionBar bar) {
    	if (bar != null) {
            bar.setDisplayShowHomeEnabled(true);
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(false);
            bar.setLogo(appIconDrawableGet(context));
            bar.setBackgroundDrawable(Background.actionBarBackgroundDrawableGet());
            bar.setDisplayShowTitleEnabled(true);
            bar.setDisplayShowTitleEnabled(false);
    	}
    }
    public static synchronized void actionBarSet(SherlockActivity activity) {
    	actionBarSet(activity, activity.getSupportActionBar());
    }
    public static synchronized void actionBarSet(SherlockListActivity activity) {
    	actionBarSet(activity, activity.getSupportActionBar());
    }
    public static synchronized void actionBarSet(SherlockFragmentActivity activity) {
    	actionBarSet(activity, activity.getSupportActionBar());
    }
    public static synchronized void actionBarSet(SherlockPreferenceActivity activity) {
    	actionBarSet(activity, activity.getSupportActionBar());
    }
    private static final int APP_ICON = R.drawable.ic_launcher;
    public static synchronized Drawable appIconDrawableGet(Context context) {
    	Resources res = context.getResources();    	
    	Bitmap bitmap = BitmapFactory.decodeResource(res, APP_ICON);
    	bitmap = bitmap.copy(bitmap.getConfig(), true);    	
    	Paint paint = new Paint();
    	paint.setColor(res.getColor(R.color.black));
    	Canvas canvas = new Canvas(bitmap);
    	canvas.drawBitmap(bitmap, 0, 0, paint);
    	BitmapDrawable drawable = new BitmapDrawable(res, bitmap);
    	
    	return drawable;
    }
    // end action bar
    
	private static final String X = " XXXXXXXXXXXX ";
	private static final String Y = " YYYYYYYYYYYY ";
	private static final String Z = " ZZZZZZZZZZZZ ";
	public static synchronized void log(String tag, String message) {
    	if (appDebugEnabled && appLog != null && false)
    		appLog.e(tag, message);
    }
	public static synchronized void logX(String tag, String message) {
    	if (appDebugEnabled && appLog != null)
    		appLog.e(tag, X + message + X);
    }
	public static synchronized void logY(String tag, String message) {
    	if (appDebugEnabled && appLog != null)
    		appLog.e(tag, Y + message + Y);
    }
	public static synchronized void logZ(String tag, String message) {
    	if (appDebugEnabled && appLog != null)
    		appLog.e(tag, Z + message + Z);
    }
	public static synchronized void logChar(String tag, char banner, String message) {
    	if (appDebugEnabled && appLog != null)
    		appLog.e(tag, X.replace('X', banner) + message + X.replace('X', banner));
    }
	public static synchronized void androidLog(String tag, String message) {
    	if (appDebugEnabled)
    		Log.e(tag, message);
    }
	public static synchronized void androidLogX(String tag, String message) {
    	if (appDebugEnabled)
    		Log.e(tag, X + message + X);
    }
	public static synchronized void androidLogZ(String tag, String message) {
    	if (appDebugEnabled)
    		Log.e(tag, Z + message + Z);
    }
}