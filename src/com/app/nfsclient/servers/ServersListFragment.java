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
import com.app.nfsclient.generic.GenericAlertDialog;
import com.app.nfsclient.generic.GenericDialogContextMenu;
import com.app.nfsclient.generic.GenericListItem;
import com.app.nfsclient.generic.GenericListItemsListFragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;

public class ServersListFragment extends GenericListItemsListFragment {
    private static final String TAG = "ServersListFragment";
    
    public static final String FRAGMENT_TAG = AppState.APP_PACKAGE_NAME + "." + TAG + ".fragmentTag";
    public static final String SERVER_INTERNET_ADDRESS_KEY = AppState.APP_PACKAGE_NAME + "." + TAG +
    	".serverInternetAddressKey";
    public static final String SERVER_HOST_NAME_KEY = AppState.APP_PACKAGE_NAME + "." + TAG +
    	".serverHostNameKey";
    public static final String SERVER_EXPORT_DIRECTORIES_KEY = AppState.APP_PACKAGE_NAME + "." + TAG +
       	".serverExportDirectoriesNameKey";
    
    public ServersListFragment() {
    	
    }
    
    public final static Handler buttonHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	AppState.androidLogX(TAG, "startupHandler");
        	
        	final ServersListFragment fragment = (ServersListFragment)msg.obj;
        	final int position = msg.arg1;
            switch(msg.what) {
            case GenericListItem.GENERIC_LIST_ITEM_BUTTON_LEFT: // files
            	break;
            case GenericListItem.GENERIC_LIST_ITEM_BUTTON_RIGHT: // edit
            	fragment.editActivityStart(AppState.serversGet(fragment.getActivity(), position),
                   	GenericListItem.ACTIVITY_RESULT_LIST_REFRESH);
                break;
            }
        }
    };
    
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
    	itemLayoutId = R.layout.generic_list_item_row_with_buttons;
    	listItemButtonHandler = buttonHandler;
    }
	
    @Override
	public void viewsInit() {
		super.viewsInit();
    	AppState.logX(TAG, "viewsInit");

        getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
	        @Override
	        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
	        	
	        	final Activity activity = getActivity();
	        	final Server server = AppState.serversGet(getActivity(), position);
	        	List<GenericDialogContextMenu.GenericDialogContextMenuItem> items =
					new ArrayList<GenericDialogContextMenu.GenericDialogContextMenuItem>();
				items.add(new GenericDialogContextMenu.GenericDialogContextMenuItem(activity.getString(
					R.string.genericDeleteButtonLabel), R.drawable.ic_trash));
				
				final GenericDialogContextMenu dialog = new GenericDialogContextMenu(activity,
					server.primaryNameGet(), items, null);
				dialog.itemClickListenerSet(new ContextMenuItemClickListener(activity, server, dialog));
				dialog.show();
				
	            return true;
	        }	
	    });
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

	        switch(arg2) {
	        case 0:
	            break;
	          
	        case 1: // delete the server
	        	final GenericAlertDialog dialog = new GenericAlertDialog(context);
	    	    dialog
	    	        .cancelableSet(true)
	                .iconSet(R.drawable.ic_trash)
	                .titleSet(R.string.genericConfirm)
	                .messageSet(String.format("Delete the server \"%s\"?", server.primaryNameGet()))
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
	
    private void editActivityStart(Server server, int requestCode) {
        AppState.logX(TAG, String.format("editActivityStart: requestCode = %d", requestCode));

    	final FragmentActivity activity = getActivity();
    	
    	Bundle bundle = new Bundle();
    	getFragmentManager().putFragment(bundle, FRAGMENT_TAG, ServersListFragment.this);
	
        Intent intent = new Intent(activity, ServerPreferencesActivity.class);
    	intent.putExtra(FRAGMENT_TAG, bundle);
    	
    	if (server != null) {
            intent.putExtra(SERVER_HOST_NAME_KEY, server.serverHostNameGet());
            intent.putExtra(SERVER_INTERNET_ADDRESS_KEY, server.serverInternetAddressGet());
            intent.putExtra(SERVER_EXPORT_DIRECTORIES_KEY, server.serverExportDirectoriesStringGet());
            
    		AppState.logX(TAG, String.format("activityStart: host name = %s, inet addr = %s, exports = %s",
    			server.serverHostNameGet(), server.serverInternetAddressGet(), server
    			.serverExportDirectoriesStringGet()));
    	}
    	
        activity.startActivityForResult(intent, requestCode);
    }
	
    @Override
	public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        AppState.logX(TAG, "onListItemClick");

	}
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	AppState.logX(TAG, "onCreateOptionsMenu");
    	
        inflater.inflate(R.menu.servers_list_options_menu, menu);
        menu.findItem(R.id.serversListOptionsMenuNewServerItem)
            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			    public boolean onMenuItemClick(MenuItem item) {
			    	editActivityStart(null, GenericListItem.ACTIVITY_RESULT_ACCOUNT_CONNECT);
			    	
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
		AppState.logX(TAG, String.format("onActivityResult: requestCode = 0x%x, resultCode = %d",
			requestCode, resultCode));

		switch(requestCode) {
		case GenericListItem.ACTIVITY_RESULT_LIST_REFRESH:
			AppState.logX(TAG, "onActivityResult: ACTIVITY_RESULT_LIST_REFRESH"); 

			if (resultCode == Activity.RESULT_OK)
        		// refresh the view
				fragmentInit();

    		break;
    		
		default:
			break;
		}
	}
}