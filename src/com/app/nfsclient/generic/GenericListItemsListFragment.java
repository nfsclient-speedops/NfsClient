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

import java.util.List;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.MenuItem;
import com.app.nfsclient.App;
import com.app.nfsclient.AppState;
import com.app.nfsclient.Background;
import com.app.nfsclient.R;
import com.app.nfsclient.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public abstract class GenericListItemsListFragment extends SherlockListFragment implements
    ListView.OnScrollListener {
    private static final String TAG = "GenericListItemsListFragment";
    
    public static final String FRAGMENT_TAG = AppState.APP_PACKAGE_NAME + "." + TAG + ".fragmentTagKey";

    protected static FragmentManager fragmentMgr = null;
    
    protected GenericListItemsListFragment fragment;
    protected Bundle fragmentArguments;
    protected String fragmentTag;
    
    protected static final String LIST_TYPE_LIST = "listTypeList";
    protected static final String LIST_TYPE_SELECTION = "listTypeSelection";
    protected enum GenericListType {
		List(LIST_TYPE_LIST),
        Selection(LIST_TYPE_SELECTION);
        
        private final String name;

        private GenericListType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    protected GenericListType listType = GenericListType.List;
    
    protected LinearLayout fragmentLayout;
    
    protected int windowLayoutId = R.layout.generic_list_items_selection;
    protected int listItemLayoutId = R.layout.generic_list_items;
    
    protected int itemLayoutId = R.layout.generic_list_item_row;
    
    protected int titleLayoutViewId = R.id.genericListItemsTitleLayout;
	protected int titleViewId = R.id.genericListItemsTitle;
    protected int titleDividerId = R.id.genericListItemsTitleDivider;
	protected int listViewId = android.R.id.list;
    
    protected LinearLayout titleViewLayout;
	protected TextView titleView;
	protected int titleViewEmptyListResid;
	protected CharSequence titleViewEmptyListString = null;
	protected int titleViewEmptyListGravity = Gravity.LEFT;
	protected float titleViewEmptyListTextSize;
	
	protected int titleViewPluralsResid;
	protected int titleViewPluralsGravity = Gravity.CENTER_HORIZONTAL;
	protected float titleViewPluralsTextSize;
	protected Object[] titleViewPluralsParams;
	
    protected View titleDivider;
    
    protected ListView listView = null;
    protected List<GenericListItem> list = null;
    protected GenericListAdapter listAdapter = null;
    protected Handler listItemButtonHandler = null;
    protected boolean initialized = false;
        
    protected Button applyButton;
    protected int applyButtonResid = R.id.genericListItemsRightButton;
    protected int applyButtonLabel = R.string.genericApplyButtonLabel;
    protected View.OnClickListener applyButtonListener = new ApplyButtonListener();

    protected Button cancelButton;
    protected int cancelButtonResid = R.id.genericListItemsLeftButton;
    protected int cancelButtonLabel = R.string.genericCancelButtonLabel;
    protected View.OnClickListener cancelButtonListener = new CancelButtonListener();
        
    protected void fragmentInit() {
    	AppState.logX(TAG, "fragmentInit");

    	dataInit();
    	viewsInit();
    }
    
    protected void dataInit() {
    	AppState.logX(TAG, "dataInit");
    	
    	fragmentMgr = getFragmentManager();
    	fragment = this;
        fragmentArguments = getArguments();
        
        titleViewEmptyListTextSize = Utils.emptyListTextSizeGet();
		titleViewPluralsTextSize = Utils.populatedListTextSizeGet();
    }
    
    protected void viewsInit() {
    	AppState.logX(TAG, String.format("viewsInit: titleViewEmptyListTextSize = %f, " +
    		"titleViewPluralsTextSize = %f", titleViewEmptyListTextSize, titleViewPluralsTextSize));
    	
        final FragmentActivity activity = getActivity();
        
        getListView().setBackgroundColor(Background.windowBackgroundGet(activity));

    	titleViewLayout = (LinearLayout)activity.findViewById(titleLayoutViewId);
		titleView = (TextView)activity.findViewById(titleViewId);
        titleDivider = (View)activity.findViewById(titleDividerId);
		//titleDivider.setBackgroundDrawable(Background.solidListDividerDrawableGet());
		titleDivider.setBackgroundColor(getResources().getColor(R.color.white));
		
		// check if the list is populated
		int count = list.size();
		if (count == 0) {
			titleViewLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
			titleView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

			titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleViewEmptyListTextSize);
			titleView.setTextColor(Background.textColorGet(activity));
			titleView.setBackgroundColor(Background.windowBackgroundGet(activity));
			titleView.setGravity(titleViewEmptyListGravity);
			if (titleViewEmptyListString != null)
				titleView.setText(titleViewEmptyListString);
			else
			    titleView.setText(titleViewEmptyListResid);
		} else {
			titleViewLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			titleView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

			// display the list title
			titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleViewPluralsTextSize);
			titleView.setTextColor(Background.textColorGet(activity));
            titleView.setTypeface(Typeface.DEFAULT_BOLD);
			titleView.setBackgroundColor(Background.listHeaderGet(activity));
			titleView.setGravity(titleViewPluralsGravity);
			String format = getResources().getQuantityText(titleViewPluralsResid, list.size()).toString();
			String title = String.format(format, titleViewPluralsParams);
			titleView.setText(title);

		    listAdapter = new GenericListItemsAdapter(getActivity(), this, itemLayoutId, list,
		    	listItemButtonHandler);

			if ((listView = getListView()) == null)		
				listView = (ListView)activity.findViewById(listViewId);
			
            listView.setDivider(Background.solidListDividerDrawableGet());
			listView.setDividerHeight((int)activity.getResources().getDimension(
				R.dimen.default_line_spacer_height));
			listView.setLongClickable(true);
            setListAdapter(listAdapter);
			registerForContextMenu(listView);

			getListView().setOnScrollListener(this);
		}
		
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        	applyButtonResid = R.id.genericListItemsLeftButton;
        	cancelButtonResid = R.id.genericListItemsRightButton;
        }
        
		if ((applyButton = (Button)activity.findViewById(applyButtonResid)) != null) {
			applyButton.setText(applyButtonLabel);
			applyButton.setOnClickListener(applyButtonListener);
			applyButton.setVisibility(count == 0 || listType == GenericListType.List ?
				Button.INVISIBLE : Button.VISIBLE);
        	applyButton.setTextColor(Background.textColorGet(activity));
			applyButton.setBackgroundDrawable(Background.positiveButtonSelectorGet(activity));
		}

		if ((cancelButton = (Button)activity.findViewById(cancelButtonResid)) != null) {
			cancelButton.setText(cancelButtonLabel);
			cancelButton.setOnClickListener(cancelButtonListener);
			cancelButton.setVisibility(count == 0 || listType == GenericListType.List ?
				Button.INVISIBLE : Button.VISIBLE);
        	cancelButton.setTextColor(Background.textColorGet(activity));
			cancelButton.setBackgroundDrawable(Background.negativeButtonSelectorGet(activity));
		}
    }
    protected class ApplyButtonListener implements View.OnClickListener {
    	private final static String TAG = GenericListItemsListFragment.TAG + ":ApplyButtonListener";

		public void onClick(View v) {
			AppState.logX(TAG, "onClick");

		}
    }
    protected class CancelButtonListener implements View.OnClickListener {
    	private final static String TAG = GenericListItemsListFragment.TAG + ":CancelButtonListener";

		public void onClick(View v) {
			AppState.logX(TAG, "onClick");

			getActivity().finish();			
		}
    }
    protected class BackButtonListener implements View.OnClickListener {
    	private final static String TAG = GenericListItemsListFragment.TAG + ":BackButtonListener";
    	
		public void onClick(View v) {
			AppState.logX(TAG, "onClick");

			getActivity().finish();			
		}
    }

    @Override
    public ListAdapter getListAdapter() {
    	return listAdapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppState.logX(TAG, String.format("onCreate: savedInstanceState = %s", savedInstanceState));
/*
        if (savedInstanceState != null)
    		setArguments(savedInstanceState);
*/
        // initialize the action bar
//    	AppState.fragmentActivityActionBarSet((SherlockFragmentActivity)getActivity());

        initialized = false;
    	dataInit();
		setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppState.logX(TAG, String.format("onCreateView: list size = %d, listType = %s, container = %s",
        	list.size(), listType, container));
        
        int layoutId = (listType == GenericListType.Selection) ? windowLayoutId : listItemLayoutId;
        fragmentLayout = (LinearLayout)inflater.inflate(layoutId, container, false);
        
        AppState.logX(TAG, String.format("onCreateView: inflated view = %s", fragmentLayout));
        
        return fragmentLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppState.logX(TAG, "onActivityCreated");

//      viewsInit();
    }
    
    @Override
	public void onResume() {
        super.onResume();
        AppState.logX(TAG, String.format("onResume: initialized = %s", initialized));
                  	        
        viewsInit();
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
        AppState.logX(TAG, "onSaveInstanceState");

        if (getArguments() != null)
		    outState.putAll(getArguments());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		AppState.logX(TAG, "onOptionsItemSelected");
		
		final FragmentActivity activity = getActivity();
		boolean value = false;
		switch (item.getItemId()) {
		case android.R.id.home:
			AppState.logX(TAG, "onOptionsItemSelected: home selected");

			Intent intent = new Intent(activity, App.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

			value = true;
			break;
			
		default:
			value = super.onOptionsItemSelected(item);
		}
		
		return value;
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
/*
		if (ready && list.size() > 0) {
			GenericListItem listItem = (GenericListItem)list.get(firstVisibleItem);
			
			if (!TextUtils.isEmpty(listItem.primaryNameGet())) {
				char firstLetter = listItem.primaryNameGet().charAt(0);

				if (isTab && dialogTextEnable)
					dialogTextEnable = ((GenericActionBarTabsInterface)getActivity()).currentTabTextGet()
					    .equalsIgnoreCase(tabText);

				if (!showing && firstLetter != prevLetter)
					showing = true;

				dialogText.setVisibility(dialogTextEnable ? View.VISIBLE : View.INVISIBLE);
				dialogText.setText(((Character)firstLetter).toString());
				handler.removeCallbacks(removeWindowThread);
				handler.postDelayed(removeWindowThread, 3000);
				prevLetter = firstLetter;
			}
		}
*/
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
	
    public static ProgressDialog onCreateDialog(Context context, int id) {
		ProgressDialog dialog = new ProgressDialog(context);

    	dialog.setTitle(Utils.EMPTY_STRING);
	    dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        
        switch(id) {
        case GenericListItem.DIALOG_FILE_IMPORT:
        	dialog.setMessage(context.getString(R.string.genericProgressDialogImporting));
        	break;
        case GenericListItem.DIALOG_FILE_EXPORT:
        	dialog.setMessage(context.getString(R.string.genericProgressDialogExporting));
        	break;
        default:
            dialog = null;
        }
        
        return dialog;    	
    }
    
	public void listFragmentRefresh(GenericListItemsListFragment oldFragment,
		GenericListItemsListFragment newFragment) {
        AppState.logX(TAG, String.format("listFragmentRefresh 1: oldFragment = %s, newFragment = %s",
        	oldFragment, newFragment));

	    getFragmentManager()
	        .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .remove(oldFragment)
            .add(GenericFragmentActivity.DEFAULT_FRAGMENT_CONTAINER_ID, newFragment, fragmentTag)
            .commitAllowingStateLoss();
	}
	
	public void listFragmentRefresh(GenericListItemsListFragment fragment, String fragmentTag) {
        AppState.logX(TAG, String.format("listFragmentRefresh 2: fragmentTag = %s, fragmentMgr = %s",
        	fragmentTag, fragmentMgr));

        if (fragmentMgr == null)
        	fragmentMgr = getFragmentManager();
        
        listFragmentRefreshStatic(fragment, fragmentTag);
	}
	
    public static void listFragmentRefreshStatic(GenericListItemsListFragment fragment,
    	String fragmentTag) {
        AppState.logX(TAG, String.format("listFragmentRefreshStatic 1: fragmentTag = %s, " +
        	"fragmentMgr = %s", fragmentTag, fragmentMgr));
        
        if (fragmentMgr != null) {
        	//XXX
        	AppState.logX(TAG, String.format("XXX listFragmentRefreshStatic 1: backStackEntryCount " +
        		"= %d, fragmentByTag = %s, fragmentById = %s", fragmentMgr.getBackStackEntryCount(),
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
        	        .replace(GenericFragmentActivity.DEFAULT_FRAGMENT_CONTAINER_ID, fragment,
        	    	    fragmentTag)
        	    	.commitAllowingStateLoss();
            } else {
                AppState.logX(TAG, "listFragmentRefreshStatic 1: the fragment was not found");
                
                fragmentMgr.beginTransaction().remove(null);
            }
        }
    }
    public static void listFragmentRefreshStatic(GenericListItemsListFragment oldFragment,
    	GenericListItemsListFragment newFragment) {
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
    
    public static boolean listFragmentValidityCheck(GenericListItemsListFragment fragment,
    	String fragmentTag, Bundle fragmentArguments) {
    	return fragmentMgr != null && fragment != null && fragmentArguments != null &&
    		!TextUtils.isEmpty(fragmentTag);
    }
    
    public void listViewRefresh() {
        AppState.logX(TAG, "listViewRefresh 1");

        listViewRefresh(listView, listAdapter);
    }
    public static void listViewRefresh(ListView listView, ArrayAdapter<GenericListItem> listAdapter) {
        AppState.logX(TAG, String.format("listViewRefresh 2: listAdapter = %s", listAdapter));
        
		listView.invalidateViews();
		listView.invalidate();
        
		if (listAdapter != null)
		    listAdapter.notifyDataSetChanged();
	}
}