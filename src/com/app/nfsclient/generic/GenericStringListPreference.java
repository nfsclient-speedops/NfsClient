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

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.nfsclient.AppState;
import com.app.nfsclient.R;

public class GenericStringListPreference extends Preference {
    private static final String TAG = "GenericStringListPreference";
    
    private LinearLayout containerView = null;
    private List<String> list = null;
    
    /**
     * {@inheritDoc}
     * @see Preference#IconPreference(Context,AttributeSet,int)
     */
    public GenericStringListPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutResource(R.layout.generic_preference);
    }

    /**
     * {@inheritDoc}
     * @see Preference#IconPreference(Context,AttributeSet)
     */
    public GenericStringListPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * TextView
     *
     * {@inheritDoc}
     * @see Preference#onBindView(View)
     */
    protected void onBindView(View view) {
        super.onBindView(view);
        AppState.logX(TAG, "onBindView");

        if ((containerView = (LinearLayout)view.findViewById(R.id.genericPreferenceValue)) != null) {
            if (list != null && list.size() > 0) {
                LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
                for (String directory : list) {
                	LinearLayout exportLayout = (LinearLayout)inflater.inflate(R.layout.generic_preference_value,
                		null);
                	TextView nameView = (TextView)exportLayout.findViewById(R.id.genericPreferenceValueName);
                	nameView.setText(directory);
                	nameView.setTextColor(getContext().getResources().getColor(R.color.red));
                	containerView.addView(exportLayout);
                }
            } else {
            	containerView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * @param list
     */
    public void listSet(List<String> list) {
    	if (list != null) {
            this.list = list;
            notifyChanged();
    	}
    }
    
    /**
     * @param list
     */
    public void listSet(String[] list) {
    	if (list != null) {
    		this.list = new ArrayList<String>();
    		for (String element : list)
    			this.list.add(element);
    		notifyChanged();
    	}
    }
}