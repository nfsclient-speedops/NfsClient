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

import com.app.nfsclient.AppState;
import com.app.nfsclient.Background;
import com.app.nfsclient.R;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class GenericSpinnerAdapter implements SpinnerAdapter {
	private static final String TAG = "GenericSpinnerAdapter";

	private Context context;
	private GenericListItemList items;
    private LayoutInflater inflater;

	public GenericSpinnerAdapter(Context context, GenericListItemList items) {
		AppState.logX(TAG, String.format("constructor 1: items = %s", (Object)items));
		
		this.context = context;
		this.items = items;
		
    	inflater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.generic_spinner_dropdown_item, null);
        TextView textView = (TextView)view.findViewById(android.R.id.text1);
        
        if (items != null && items.size() > 0) {
			int background = Background.windowBackgroundGet(context);
			int textColor = Background.textColorGet(context);
			
            textView.setBackgroundColor(background);
            textView.setText(items.get(position).firstGet()); 
            textView.setTextColor(textColor);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
        }
//        textView.setWidth(200);
        
        return textView;
    }

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.generic_spinner_dropdown_item, null); 
        TextView textView = (TextView)view.findViewById(android.R.id.text1);
        
        if (items != null && items.size() > 0) {
			int background = Background.listHeaderGet(context);
			int textColor = Background.textColorGet(context);
			
            textView.setBackgroundColor(background);
            textView.setText(items.get(position).firstGet()); 
            textView.setTextColor(textColor);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
        }
//        textView.setWidth(250);
        
        return textView; 
	}

	@Override
	public int getCount() {
		return (items != null) ? items.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return (items != null) ? items.get(position) : null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public int getItemViewType(int arg0) {
        return android.R.id.text1;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver arg0) {
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver arg0) {
	}
}