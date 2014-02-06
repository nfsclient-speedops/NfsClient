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

import com.app.nfsclient.AppState;
import com.app.nfsclient.Background;
import com.app.nfsclient.R;
import com.app.nfsclient.Utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class GenericDialogContextMenu extends Dialog {
    private static final String TAG = "GenericDialogContextMenu";
    	
    private Context context;
    private List<GenericDialogContextMenuItem> items;
    private OnItemClickListener clickListener;
    
    private LinearLayout dialogLayout;
    private TextView titleView;
	private String titleString;
	private View titleDivider;
    private ListView listView;
    private GenericDialogContextMenuAdapter listAdapter;

	public GenericDialogContextMenu(Context context, String titleString,
	    List<GenericDialogContextMenuItem> items, OnItemClickListener clickListener) {
		super(context, R.style.notificationDialogTheme);

		this.context = context;
		this.titleString = titleString;
		this.items = items;
		this.clickListener = clickListener;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppState.logX(TAG, "onCreate");
    
		setContentView(R.layout.generic_dialog_context_menu);
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        LayoutParams params = getWindow().getAttributes(); 
        params.width = LayoutParams.MATCH_PARENT; 
        getWindow().setAttributes((android.view.WindowManager.LayoutParams)params); 

        if ((dialogLayout = (LinearLayout)findViewById(R.id.genericDialogContextMenuLayout)) != null)
            dialogLayout.setBackgroundColor(Background.windowBackgroundGet(context));
        
        titleView = (TextView)findViewById(R.id.genericDialogContextMenuTitle);
        titleDivider = (View)findViewById(R.id.genericDialogContextMenuTitleDivider);
		listView = (ListView)findViewById(android.R.id.list);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		AppState.logX(TAG, "onStart");
		
		titleView.setText(titleString);
		titleView.setTextColor(Background.textColorGet(context));
        
        titleDivider.setBackgroundDrawable(Background.solidListDividerDrawableGet());
        titleDivider.setMinimumHeight((int)context.getResources().getDimension(
        	R.dimen.default_line_spacer_height));
        
        listAdapter = new GenericDialogContextMenuAdapter(context, items);
        listView.setAdapter(listAdapter);
        listView.setDivider(Background.solidListDividerDrawableGet());
		listView.setDividerHeight((int)context.getResources().getDimension(R.dimen.default_line_spacer_height));
        if (clickListener != null)
        	listView.setOnItemClickListener(clickListener);
        else
        	listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				    dismiss();
				}
        	});
	}
	
	public static class GenericDialogContextMenuItem extends GenericListItem {
		private static final String TAG = "GenericDialogContextMenuItem";
				
		public GenericDialogContextMenuItem(String itemName) {
			super(GenericListItem.ITEM_TYPE_GENERIC_LIST_ITEM_OBJECT, itemName, Utils.EMPTY_STRING);
		}
	}
	
	private class GenericDialogContextMenuAdapter extends ArrayAdapter<GenericDialogContextMenuItem> {
        private static final String TAG = GenericDialogContextMenu.TAG + ":GenericDialogContextMenuAdapter";
		
        private Context context;
        private List<GenericDialogContextMenuItem> items;

		public GenericDialogContextMenuAdapter(Context context, List<GenericDialogContextMenuItem> items) {
			super(context, R.layout.generic_context_menu_item, items);
			AppState.logX(TAG, "constructor");
			
			this.context = context;
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
		
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.generic_context_menu_item, null);
			}
			
			final GenericDialogContextMenuItem item = items.get(position);
			if (item != null) {
				AppState.logX(TAG, String.format("getView: itemName = %s", item.firstGet()));

				TextView nameView = (TextView)v.findViewById(R.id.genericContextMenuItemName);
				nameView.setText(item.firstGet());
				nameView.setTextColor(Background.textColorGet(context));
			}
			
			return v;
		}
	}
	
	public Context contextGet() {
		return context;
	}
	
	public TextView titleViewGet() {
		return titleView;
	}
	
	public void itemClickListenerSet(OnItemClickListener clickListener) {
		this.clickListener = clickListener;
	}
	
	public void dialogShow() {
        AppState.logX(TAG, "dialogShow");

        show();
	}
}