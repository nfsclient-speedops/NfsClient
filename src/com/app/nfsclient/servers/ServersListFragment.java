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

package com.app.nfsclient.servers;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.app.nfsclient.AppState;
import com.app.nfsclient.R;
import com.app.nfsclient.Utils;
import com.app.nfsclient.filemanager.FileManagerActivity;
import com.app.nfsclient.filemanager.intents.FileManagerIntents;
import com.app.nfsclient.generic.GenericAlertDialog;
import com.app.nfsclient.generic.GenericDialogContextMenu;
import com.app.nfsclient.generic.GenericListItem;
import com.app.nfsclient.generic.GenericListItemsListFragment;
import com.app.nfsclient.protocol.xfile.XFile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ServersListFragment extends GenericListItemsListFragment {
    private static final String TAG = "ServersListFragment";
    
    public static final String FRAGMENT_TAG = AppState.APP_PACKAGE_NAME + "." + TAG + ".fragmentTag";
    public static final String SERVER_INTERNET_ADDRESS_KEY = AppState.APP_PACKAGE_NAME + "." + TAG +
    	".serverInternetAddressKey";
    public static final String SERVER_HOST_NAME_KEY = AppState.APP_PACKAGE_NAME + "." + TAG +
    	".serverHostNameKey";
    public static final String SERVER_USER_NAME_KEY = AppState.APP_PACKAGE_NAME + "." + TAG +
    	".serverUserNameKey";
    public static final String SERVER_USER_PASSWORD_KEY = AppState.APP_PACKAGE_NAME + "." + TAG +
        ".serverUserPasswordKey";
    public static final String SERVER_EXPORT_DIRECTORIES_KEY = AppState.APP_PACKAGE_NAME + "." + TAG +
       	".serverExportDirectoriesNameKey";
    
    public ServersListFragment() {
    	
    }
    
    @Override
    protected void dataInit() {
    	super.dataInit();
    	AppState.logX(TAG, "dataInit");
    	
    	fragmentTag = FRAGMENT_TAG;
    	listType = GenericListType.List;
    	list = AppState.serversListGet(getActivity());
    	titleViewEmptyListResid = R.string.noServers;
    	titleViewPluralsResid = R.plurals.listAllServers;
    	titleViewPluralsParams = new Object[] {
    		list.size()
    	};
    	itemLayoutId = R.layout.generic_list_item_row;
    }
	
    @Override
	public void viewsInit() {
		super.viewsInit();
    	AppState.logX(TAG, "viewsInit");

    }
	
    private void editActivityStart(Context context, Server server, int requestCode) {
        AppState.logX(TAG, String.format("editActivityStart: requestCode = %d", requestCode));

    	Bundle bundle = new Bundle();
    	getFragmentManager().putFragment(bundle, FRAGMENT_TAG, ServersListFragment.this);
	
        Intent intent = new Intent(context, ServerPreferencesActivity.class);
    	intent.putExtra(FRAGMENT_TAG, bundle);
    	String hostName = Utils.EMPTY_STRING;
    	String inetAddr = Utils.EMPTY_STRING;
    	String userName = Utils.EMPTY_STRING;
    	String userPass = Utils.EMPTY_STRING;
    	String[] exports = null;
    	
    	if (server != null) {
            hostName = server.serverHostNameGet();
            inetAddr = server.serverInternetAddressGet();
            userName = server.serverUserNameGet();
            userPass = server.serverInternetAddressGet();
            exports = Server.serverExportDirectoriesArrayGet(server.serverExportDirectoriesGet());
            
    		AppState.logX(TAG, String.format("activityStart: host name = %s, inet addr = %s, exports = %s",
    			server.serverHostNameGet(), server.serverInternetAddressGet(), Server
    			.serverExportDirectoriesStringGet(server.serverExportDirectoriesGet())));
    	}
    	
    	intent.putExtra(SERVER_HOST_NAME_KEY, hostName);
        intent.putExtra(SERVER_INTERNET_ADDRESS_KEY, inetAddr);
        intent.putExtra(SERVER_USER_NAME_KEY, userName);
        intent.putExtra(SERVER_USER_PASSWORD_KEY, userPass);
        intent.putExtra(SERVER_EXPORT_DIRECTORIES_KEY, exports);
        startActivityForResult(intent, requestCode);
    }
	
    private void serverFilesShow(Context context, Server server) {
    	Intent intent = new Intent(context, FileManagerActivity.class);
		Uri uriFile = Uri.parse(AppState.appDataDirectoryFileGet(context).getAbsolutePath());
        intent.setData(uriFile);
		
		// Set the file manager parameters
        intent.putExtra(FileManagerIntents.EXTRA_FILE_SYSTEM_TYPE, server.itemTypeGet());
        intent.putExtra(FileManagerIntents.EXTRA_FILE_SYSTEM_ID, server.serverHostNameGet());
        intent.putExtra(FileManagerIntents.EXTRA_TITLE, "Files");
		intent.putExtra(FileManagerIntents.EXTRA_BUTTON_TEXT, "Files");
		
		// startActivity(intent);
    }
    private class ContextMenuItemClickListener implements AdapterView.OnItemClickListener {
        private static final String TAG = ServersListFragment.TAG + ":ContextMenuItemClickListener";
        
        private Context context;
        private Server server;
        private GenericDialogContextMenu dialog;
        
        public ContextMenuItemClickListener(Context context, Server server, GenericDialogContextMenu dialog) {
        	this.context = context;
        	this.server = server;
        	this.dialog = dialog;
        }
        
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
			AppState.logX(TAG, String.format("onItemClick: arg2 = %d, arg3 = %d", arg2, arg3));

	    	final FragmentActivity activity = getActivity();
	        switch(arg2) {
	        case 0: // edit the server configuration
	        	editActivityStart(activity, server, GenericListItem.ACTIVITY_RESULT_ACCOUNT_EDIT);
	            break;
	            
	        case 1: // show the server's nfs files
	        	serverFilesShow(activity, server);
	        	XFile xf;
	            break;
	            
	        case 2: // delete the server
	        	final GenericAlertDialog dialog = new GenericAlertDialog(context);
	    	    dialog
	    	        .cancelableSet(true)
	                .iconSet(R.drawable.ic_trash)
	                .titleSet(R.string.genericConfirm)
	                .messageSet(String.format("Delete the server \"%s\"?", server.firstGet()))
	                .positiveButtonSet(R.string.genericDeleteButtonLabel, new View.OnClickListener() {
	    		        public void onClick(View v) {
	    				    AppState.serversDelete(context, server, true);
	    				    list = AppState.serversListGet(context);
	    				    
	    				    // refresh the view
	    				    listFragmentRefresh(new ServersListFragment(), FRAGMENT_TAG);
	    				    dialog.dismiss();
	    			    }
	    		     })
	                .negativeButtonSet(R.string.genericCancelButtonLabel, new View.OnClickListener() {
	            	    @Override
	    	            public void onClick(View v) {
	    		            dialog.dismiss();
	    	            }
	                });
	            dialog.show();
				break;		

			}
			dialog.dismiss();
		}
	}
    
    @Override
	public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        final Activity activity = getActivity();
        final Server server = AppState.serversGet(activity, position);
        List<GenericDialogContextMenu.GenericDialogContextMenuItem> items =
        	new ArrayList<GenericDialogContextMenu.GenericDialogContextMenuItem>();
        items.add(new GenericDialogContextMenu.GenericDialogContextMenuItem(activity.getString(
            R.string.genericEditButtonLabel)));
        items.add(new GenericDialogContextMenu.GenericDialogContextMenuItem(activity.getString(
            R.string.genericFilesButtonLabel)));
        items.add(new GenericDialogContextMenu.GenericDialogContextMenuItem(activity.getString(
        	R.string.genericDeleteButtonLabel)));

        final GenericDialogContextMenu dialog = new GenericDialogContextMenu(activity, server.firstGet(),
        	items, null);
        dialog.itemClickListenerSet(new ContextMenuItemClickListener(activity, server, dialog));
        dialog.show();
	}
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	final FragmentActivity activity = getActivity();
        inflater.inflate(R.menu.servers_list_options_menu, menu);
        menu.findItem(R.id.serversListOptionsMenuNewServerItem)
            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			    public boolean onMenuItemClick(MenuItem item) {
			    	editActivityStart(activity, null, GenericListItem.ACTIVITY_RESULT_ACCOUNT_EDIT);
			    	
				    return false;
			    }
    	    });
        super.onCreateOptionsMenu(menu, inflater);
    }

    protected ProgressDialog onCreateDialog(int id) {
    	ProgressDialog dialog = new ProgressDialog(getActivity());
    	    	
    	dialog.setTitle(Utils.EMPTY_STRING);
    	dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        
        switch(id) {
        case GenericListItem.DIALOG_LOADING:
        	dialog.setMessage(getString(R.string.genericProgressDialogLoading));
        	break;
        case GenericListItem.DIALOG_FILE_SEARCH:
           	dialog.setTitle(getString(R.string.genericProgressDialogSearchingTitle));
           	dialog.setMessage(getString(R.string.genericProgressDialogSearchingMessage));
           	break;
        default:
            dialog = null;
        }
        
        return dialog;
    }
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		AppState.logX(TAG, String.format("onActivityResult: requestCode = 0x%x, resultCode = %d", requestCode,
			resultCode));

		switch(requestCode) {
		case GenericListItem.ACTIVITY_RESULT_ACCOUNT_EDIT:
			AppState.logX(TAG, "onActivityResult: ACTIVITY_RESULT_ACCOUNT_EDIT"); 

    		// refresh the view
			if (resultCode == Activity.RESULT_OK)
				fragmentInit();

    		break;
    		
		default:
			break;
		}
	}
}