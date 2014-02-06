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

import com.app.nfsclient.Background;
import com.app.nfsclient.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GenericListItemsAdapter extends GenericListAdapter {    
    private Context context;
    private LayoutInflater inflater;
    private int layoutResId;

	private int textColor = 0;
	
    private List<GenericListItem> itemsList;
	
    public static final int NOTIFY_DATA_SET_CHANGED = 1;
    
    public GenericListItemsAdapter(Context context, int layoutResId, List<GenericListItem> itemsList) {
		super(context, layoutResId, itemsList);
		
		this.context = context;
		this.layoutResId = layoutResId;
		this.itemsList = itemsList;
		
		textColor = Background.textColorGet(context);
		
    	inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	}
    
    public GenericListItemsAdapter(Context context) {
		super(context, 0, null);
    }
    
    @Override
    public int getCount() {
    	return itemsList.size();
    }
    
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
    	GenericListItem item = (GenericListItem)itemsList.get(position);
    	
    	if (view == null)
    		view = inflater.inflate(layoutResId, null);
    	
    	if (textColor == 0)
    		textColor = Background.textColorGet(context);
    	
		TextView primaryName = null;
    	if ((primaryName = (TextView)view.findViewById(R.id.genericListItemPrimaryName)) != null) {
    	    primaryName.setText(item.firstGet());
    	    primaryName.setTextColor(textColor);
    	}
    	
    	TextView secondaryName = null;
    	if ((secondaryName = (TextView)view.findViewById(R.id.genericListItemSecondaryName)) != null) {
		    secondaryName.setText(item.secondGet());
		    secondaryName.setTextColor(textColor);
    	}
    	
    	return view;
    }
    
    public List<GenericListItem> listGet() {
    	return itemsList;
    }
    public void listSet(List<GenericListItem> itemsList) {
    	this.itemsList = itemsList;
    }
}