/*
 * Copyright (c) 2013 SpeedOps
 * All rights reserved.
 *
 * SpeedOps is not responsible for any use or misuse of this product.
 * In using this software you agree to hold harmless mux.sms@gmail.com and any other
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

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.app.nfsclient.AppState;
import com.app.nfsclient.R;

public class GenericOnOffSwitchPreference extends Preference {
    private static final String TAG = "GenericOnOffSwitchPreference";
    
    private GenericOnOffSwitch onOffSwitch = null;
    private OnCheckedChangeListener checkedChangeListener = null;
    private boolean state = false;
    private String onText = null;
    private String offText = null;
    
    private LinearLayout valueView = null;
    private String value = null;
    private int valueInputType = InputType.TYPE_CLASS_TEXT;
    
    private OnPreferenceClickListener preferenceClickListener = null;
    
    private OnCheckedChangeListener defaultCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            AppState.logX(TAG, String.format("defaultCheckedChangeListener: isChecked = %s", isChecked));
            
            state = isChecked;
		}
    };

    private OnPreferenceClickListener defaultPreferenceClickListener = new OnPreferenceClickListener() {
		@Override
		public boolean onPreferenceClick(Preference preference) {
            AppState.logX(TAG, "defaultPreferenceClickListener");
			   
            stateSet(!state);
            return false;
		}
    };

    /**
     * {@inheritDoc}
     * @see Preference#IconPreference(Context,AttributeSet,int)
     */
    public GenericOnOffSwitchPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        AppState.logZ(TAG, "constructor1");

        setLayoutResource(R.layout.generic_on_off_switch_preference);
        
        this.checkedChangeListener = defaultCheckedChangeListener;
        this.preferenceClickListener = defaultPreferenceClickListener;
    }

    /**
     * {@inheritDoc}
     * @see Preference#IconPreference(Context,AttributeSet)
     */
    public GenericOnOffSwitchPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        AppState.logZ(TAG, "constructor2");
    }

    /**
     * ImageView
     *
     * {@inheritDoc}
     * @see Preference#onBindView(View)
     */
    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        onOffSwitch = (GenericOnOffSwitch)view.findViewById(R.id.genericOnOffSwitch);
        AppState.logZ(TAG, String.format("onBindView: view = %s, onOffSwitch = %s, state = %s", view,
        	onOffSwitch, state));
        if (onOffSwitch != null) {
           	onOffSwitch.setChecked(state);
           	onOffSwitch.setClickable(true);
           	onOffSwitch.setVisibility(View.VISIBLE);
           
           	if (!TextUtils.isEmpty(onText))
           		onOffSwitch.setTextOn(onText);
           	if (!TextUtils.isEmpty(offText))
           		onOffSwitch.setTextOff(offText);
           	
           	if (checkedChangeListener != null)
           		onOffSwitch.setOnCheckedChangeListener(checkedChangeListener);
        }

        if ((valueView = (LinearLayout)view.findViewById(R.id.genericOnOffSwitchValue)) != null) {
        	if (!TextUtils.isEmpty(value)) {
        		LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
        		LinearLayout valueLayout = (LinearLayout)inflater.inflate(R.layout.generic_preference_value,
        			null);
        		TextView nameView = (TextView)valueLayout.findViewById(R.id.genericPreferenceValueName);
        		nameView.setText(value);
        		nameView.setInputType(valueInputType);
        		nameView.setTextColor(getContext().getResources().getColor(R.color.red));
        		valueView.addView(valueLayout);
        	} else {
        		valueView.setVisibility(View.GONE);
        	}
        }
        
        this.setOnPreferenceClickListener(preferenceClickListener);
    }

    /**
     * @param item
     */
    public void valueSet(String value) {
        this.value = value;
        notifyChanged();
    }
    public void valueInputTypeSet(int valieInputType) {
    	this.valueInputType = valieInputType;
    	notifyChanged();
    }
    
    public GenericOnOffSwitch onOffSwitchGet() {
    	return onOffSwitch;
    }

    public void switchThumbTrackBackgroundSet(int colorId) {
        AppState.logY(TAG, String.format("switchThumbTrackBackgroundSet: state = %s, onOffSwitch = %s", state,
        	onOffSwitch));

        if (onOffSwitch != null)
    	    onOffSwitch.setThumbTrackBackgroundColor(colorId);
    }

    public boolean stateGet() {
    	return state;
    }
    public void stateSet(boolean state) {
        AppState.logZ(TAG, String.format("stateSet: state = %s, onOffSwitch = %s", state, onOffSwitch));

        this.state = state;
        if (onOffSwitch != null) {
        	onOffSwitch.setChecked(state);
            notifyChanged();
        }
    }
    public void setOnOffText(String onText, String offText) {
    	this.onText = onText;
    	this.offText = offText;
    	notifyChanged();
    }
    
    public void onCheckedChangeListenerSet(OnCheckedChangeListener checkedChangeListener) {
    	this.checkedChangeListener = checkedChangeListener;
    }
    public void onPreferenceClickListenerSet(OnPreferenceClickListener preferenceClickListener) {
    	this.preferenceClickListener = preferenceClickListener;
    }
}