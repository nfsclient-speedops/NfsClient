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

import com.app.nfsclient.R;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GenericTextPreference extends Preference {
    @SuppressWarnings("unused")
	private static final String TAG = "GenericTextPreference";
    
    private LinearLayout valueView = null;
    private String value = null;
    
    /**
     * {@inheritDoc}
     * @see Preference#IconPreference(Context,AttributeSet,int)
     */
    public GenericTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutResource(R.layout.generic_preference);
    }

    /**
     * {@inheritDoc}
     * @see Preference#IconPreference(Context,AttributeSet)
     */
    public GenericTextPreference(Context context, AttributeSet attrs) {
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
        
        if ((valueView = (LinearLayout)view.findViewById(R.id.genericPreferenceValue)) != null) {
        	if (!TextUtils.isEmpty(value)) {
        		LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
        		LinearLayout valueLayout = (LinearLayout)inflater.inflate(R.layout.generic_preference_value,
        			null);
        		TextView nameView = (TextView)valueLayout.findViewById(R.id.genericPreferenceValueName);
        		nameView.setText(value);
        		nameView.setTextColor(getContext().getResources().getColor(R.color.red));
        		valueView.addView(valueLayout);
        	} else {
        		valueView.setVisibility(View.GONE);
        	}
        }
    }

    /**
     * @param item
     */
    public void valueSet(String value) {
        this.value = value;
        notifyChanged();
    }
}