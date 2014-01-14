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

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.app.nfsclient.App;
import com.app.nfsclient.AppState;
import com.app.nfsclient.Background;
import com.app.nfsclient.R;
import com.app.nfsclient.Utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.View;

public abstract class GenericFragmentActivity extends SherlockFragmentActivity {
    private static final String TAG = "GenericFragmentActivity";

    protected static final String SAVED_INSTANCE_STATE_KEY = AppState.APP_PACKAGE_NAME + "." + TAG +
    	".savedInstanceStateKey";
    protected boolean savedInstanceStateKey = false;

    protected static final String SAVED_INSTANCE_STATE_FRAGMENT_BUNDLE_KEY = AppState.APP_PACKAGE_NAME + "." +
        TAG + ".savedInstanceStateFragmentBundleKey";
    protected GenericListItemsListFragment savedInstanceStateFragment = null;
    
    public static int DEFAULT_FRAGMENT_CONTAINER_ID = android.R.id.content;

    protected FragmentManager fragmentMgr;
    protected GenericListItemsListFragment fragment = null;
    protected Bundle fragmentArguments;
    protected String fragmentTag;
    
    protected String imageGroupName = Utils.EMPTY_STRING;
    
    protected ProgressDialog progressDialog;
    protected static final int DIALOG_LOADING = 0;
    
    public GenericListItemsListFragment fragmentGet() {
		return fragment;
	}
	public void fragmentSet(GenericListItemsListFragment fragment) {
		this.fragment = fragment;
	}

	public Bundle fragmentArgumentsGet() {
		return fragmentArguments;
	}
	public void fragmentArgumentsSet(Bundle fragmentArguments) {
		this.fragmentArguments = fragmentArguments;
	}

	public String fragmentTagGet() {
		return fragmentTag;
	}
	public void fragmentTagSet(String fragmentTag) {
		this.fragmentTag = fragmentTag;
	}

    protected void fragmentSpawn() {
    	AppState.logX(TAG, String.format("fragmentSpawn 1: fragmentTag = %s", fragmentTag));
    	
    	fragmentSpawn(getSupportFragmentManager(), fragment, DEFAULT_FRAGMENT_CONTAINER_ID, fragmentTag,
    		fragmentArguments);
    }
    public static void fragmentSpawn(FragmentManager fragmentMgr, Fragment fragment, int fragmentId,
    	String fragmentTag, Bundle fragmentArguments) {
    	AppState.logX(TAG, String.format("fragmentSpawn 2: fragmentTag = %s", fragmentTag));
    	
    	if (fragment != null) {
    	    fragment.setArguments(fragmentArguments);
    	    fragmentMgr.beginTransaction().add(fragmentId, fragment, fragmentTag).commitAllowingStateLoss();
    	    fragmentMgr.executePendingTransactions();
    	}
    }
    
    protected void fragmentRemove(Fragment fragment) {
    	AppState.logX(TAG, String.format("fragmentRemove 1: fragmentTag = %s", fragmentTag));

    	if (fragment != null)
    		fragmentRemove(getSupportFragmentManager(), fragment);
    }
    public static void fragmentRemove(FragmentManager fragmentMgr, Fragment fragment) {
    	AppState.logX(TAG, String.format("fragmentRemove 2: fragment = %s", fragment));

        fragmentMgr.beginTransaction().remove(fragment).commitAllowingStateLoss();
    }

    @Override
    protected void onCreate(Bundle savedInstance) {
    	StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
 	        .detectLeakedSqlLiteObjects()
            .penaltyLog()
            .penaltyDeath()
            .build());
    	super.onCreate(savedInstance);
    	AppState.logX(TAG, String.format("onCreate: savedInstance = %s, state key = %s", savedInstance,
    		savedInstance != null ? savedInstance.getBoolean(SAVED_INSTANCE_STATE_KEY) : "n/a"));
    	
    	if (savedInstance == null) {
    		fragmentMgr = getSupportFragmentManager();
    		if (!savedInstanceStateKey) {
    			if ((progressDialog = (ProgressDialog)onCreateDialog(DIALOG_LOADING)) != null)
    				progressDialog.show();

    			if (progressDialog != null)
    				progressDialog.dismiss();
    			
    			AppState.logX(TAG, "onCreate: calling fragmentSpawn in savedInstanceState");

    			if (fragment != null) {
    			    fragmentRemove(savedInstanceStateFragment);
    			    fragmentSpawn();
    			}
    		}
    	}
    	
    	// initialize the action bar
    	AppState.actionBarSet(this);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        AppState.logX(TAG, "onSaveInstanceState");
        
        savedInstanceState.putBoolean(SAVED_INSTANCE_STATE_KEY, true);
        
        if (fragment != null && fragmentTag != null) {
            AppState.logX(TAG, String.format("onSaveInstanceState: fragmentTag = %s, fragment = %s",
            	fragmentTag, fragment));
            Bundle bundle = new Bundle();
            savedInstanceState.putBundle(SAVED_INSTANCE_STATE_FRAGMENT_BUNDLE_KEY, bundle);
        }
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
        AppState.logX(TAG, "onRestoreInstanceState");

        savedInstanceStateKey = savedInstanceState.getBoolean(SAVED_INSTANCE_STATE_KEY, false);
        
        Bundle fragmentBundle = savedInstanceState.getBundle(SAVED_INSTANCE_STATE_FRAGMENT_BUNDLE_KEY);
        if (fragmentBundle != null)
            savedInstanceStateFragment = (GenericListItemsListFragment)getSupportFragmentManager().getFragment(
            	fragmentBundle, fragmentTag);
    }
    
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
    	View view = null;
    	if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")) {
            AppState.logX(TAG, String.format("onCreateView: name = %s", name));

    		try {
    			final View optionsMenuView = getLayoutInflater().createView(name, null, attrs);

    			/*  
    			 * The background gets refreshed each time a new item is added the options
    			 * menu.  So each time Android applies the default background we need to
    			 * set our own background. This is done using a thread giving the
    			 * background change as runnable object.
    			 */
    			new Handler().post(new Runnable() {
    				public void run() {
    					AppState.logX(TAG, "onCreateView: handler run");

    					optionsMenuView.setBackgroundColor(Background.listHeaderGet(
    						GenericFragmentActivity.this));
    				}
    			});

    			view = optionsMenuView;
    		} catch (InflateException e) {
    			AppState.logX(TAG, "onCreateView: InflateException");
    		} catch (ClassNotFoundException e) {
    			AppState.logX(TAG, "onCreateView: ClassNotFoundException");
    		}
    	} else {
    		view = super.onCreateView(name, context, attrs);
    	}

    	return view;
    }
   
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		AppState.logX(TAG, "onOptionsItemSelected");
		
		boolean value = false;
		switch (item.getItemId()) {
		case android.R.id.home:
			AppState.logX(TAG, "onOptionsItemSelected: home selected");

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
	
	public void databaseOpen() {
        AppState.logX(TAG, "databaseOpen");
    }
	public void databaseClose() {
        AppState.logX(TAG, "databaseClose");
	}
	
	protected Dialog onCreateDialog(int id) {
    	ProgressDialog progressDialog = new ProgressDialog(this);
    	    	
    	AppState.logX(TAG, String.format("onCreateDialog: id = %d", id));
    	
    	progressDialog.setTitle(Utils.EMPTY_STRING);
    	progressDialog.setIndeterminate(true);
    	progressDialog.setCancelable(true);
        
        switch(id) {
        case DIALOG_LOADING:
        default:
        	progressDialog.setMessage(getString(R.string.genericProgressDialogLoading));
            break;
        }
        
        return progressDialog;
    }
    
    public void listFragmentRefresh(GenericListItemsListFragment newFragment) {
    	AppState.logX(TAG, String.format("listFragmentRefresh 0: newFragment = %s", newFragment));

    	listFragmentRefresh(fragment, newFragment);
    }

    public void listFragmentRefresh(GenericListItemsListFragment oldFragment,
    	GenericListItemsListFragment newFragment) {
    	AppState.logX(TAG, String.format("listFragmentRefresh 1: oldFragment = %s, newFragment = %s",
    		oldFragment, newFragment));

    	fragment = newFragment;
    	getSupportFragmentManager()
    	    .beginTransaction()
    	    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    	    .remove(oldFragment)
    	    .add(GenericFragmentActivity.DEFAULT_FRAGMENT_CONTAINER_ID, newFragment, fragmentTag)
    	    .commitAllowingStateLoss();
    }

    public void listFragmentRefresh(GenericListItemsListFragment newFragment, String fragmentTag) {
    	AppState.logX(TAG, String.format("listFragmentRefresh 2: fragmentTag = %s, fragmentMgr = %s",
    		fragmentTag, fragmentMgr));

    	if (fragmentMgr == null)
    		fragmentMgr = getSupportFragmentManager();

    	fragment = newFragment;
    	listFragmentRefreshStatic(fragmentMgr, newFragment, fragmentTag);
    }

    public static void listFragmentRefreshStatic(FragmentManager fragmentMgr,
    	GenericListItemsListFragment fragment, String fragmentTag) {
    	AppState.logX(TAG, String.format("listFragmentRefreshStatic 1: fragmentTag = %s, " +
    		"fragmentMgr = %s", fragmentTag, fragmentMgr));

    	if (fragmentMgr != null) {
    		//XXX
    		AppState.logX(TAG, String.format("XXX listFragmentRefreshStatic 1: backStackEntryCount = %d, " +
    			"fragmentByTag = %s, fragmentById = %s", fragmentMgr.getBackStackEntryCount(),
    			fragmentMgr.findFragmentByTag(fragmentTag), fragmentMgr.findFragmentById(
    			GenericFragmentActivity.DEFAULT_FRAGMENT_CONTAINER_ID)));

    		Fragment oldFragment;
    		if ((oldFragment = fragmentMgr.findFragmentById(
    			GenericFragmentActivity.DEFAULT_FRAGMENT_CONTAINER_ID)) != null &&
    			oldFragment.getTag().equals(fragmentTag)) {
    			AppState.logX(TAG, "listFragmentRefreshStatic 1: the fragment was found");

    			fragment.setArguments(oldFragment.getArguments());
    			fragmentMgr
    			    .beginTransaction()
    			    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    			    .replace(GenericFragmentActivity.DEFAULT_FRAGMENT_CONTAINER_ID, fragment, fragmentTag)
    				.commitAllowingStateLoss();
    		} else {
    			AppState.logX(TAG, "listFragmentRefreshStatic 1: the fragment was not found");

    			fragmentMgr.beginTransaction().remove(null);
    		}
    	}
    }
    public static void listFragmentRefreshStatic(FragmentManager fragmentMgr,
    	GenericListItemsListFragment oldFragment, GenericListItemsListFragment newFragment) {
    	AppState.logX(TAG, String.format("listFragmentRefresh 2: oldFragment = %s, newFragment = %s",
    		oldFragment, newFragment));

    	if (fragmentMgr != null) {
    		fragmentMgr
    		    .beginTransaction()
    		    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    		    .remove(oldFragment)
    		    .add(oldFragment.getId(), newFragment, oldFragment.getTag())
    		    .commitAllowingStateLoss();
    	}
    }

    public static boolean listFragmentValidityCheck(FragmentManager fragmentMgr,
    	GenericListItemsListFragment fragment, String fragmentTag, Bundle fragmentArguments) {
    	return fragmentMgr != null && fragment != null && fragmentArguments != null &&
    		!TextUtils.isEmpty(fragmentTag);
    }
}