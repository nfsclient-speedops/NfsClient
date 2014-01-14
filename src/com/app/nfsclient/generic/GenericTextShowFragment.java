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

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.app.nfsclient.AppState;
import com.app.nfsclient.Background;
import com.app.nfsclient.R;
import com.app.nfsclient.Utils;

public class GenericTextShowFragment extends SherlockFragment {
    private static final String TAG = "GenericTextShowFragment";
    
    private TextView windowTitle;
    private String windowTitleText = null;
    private View titleDivider;
    private TextView textBody;
    private String textBodyText = null;
    private int textBodyResid;

    private void viewsInit() {
    	final FragmentActivity activity = getActivity();

        windowTitle = (TextView)activity.findViewById(R.id.genericTextShowTitle);
    	windowTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utils.populatedListTextSizeGet());
    	windowTitle.setTextColor(Background.textColorGet(activity));
        windowTitle.setTypeface(Typeface.DEFAULT_BOLD);
        windowTitle.setBackgroundColor(Background.listHeaderGet(activity));
    	windowTitle.setGravity(Gravity.CENTER_HORIZONTAL);
    	
    	if ((windowTitleText = getArguments().getString(GenericTextShowFragmentActivity.WINDOW_TITLE_KEY))
    		!= null)
    	    windowTitle.setText(windowTitleText);
		
        titleDivider = (View)activity.findViewById(R.id.genericTextShowTitleDivider);
		titleDivider.setBackgroundDrawable(Background.solidListDividerDrawableGet());

    	textBody = (TextView)activity.findViewById(R.id.genericTextShowBody);
        textBody.setTextSize(TypedValue.COMPLEX_UNIT_PX, activity.getResources().getDimension(
        	R.dimen.default_text_size_small));
        textBody.setTextColor(Background.textColorGet(activity));
        textBody.setBackgroundColor(Background.windowBackgroundGet(activity));
    	textBody.setGravity(Gravity.LEFT);
    	
    	if ((textBodyText = getArguments().getString(GenericTextShowFragmentActivity.WINDOW_TEXT_STRING_KEY))
    		!= null)
        	textBody.setText(textBodyText);
    	else if ((textBodyResid = getArguments().getInt(GenericTextShowFragmentActivity.WINDOW_TEXT_RESID_KEY))
        	> 0)
        	textBody.setText(textBodyResid);
    }
/*    
    public GenericFileExportSelectionDialog fileExportSelectionDialogGet() {
    	return fileExportSelectionDialog;
    }
*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	AppState.logX(TAG, "onCreate");
    	
    	// initialize the action bar
    	AppState.actionBarSet(getSherlockActivity());

    	setHasOptionsMenu(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppState.logX(TAG, "onCreateView");

    	return (LinearLayout)inflater.inflate(R.layout.generic_text_show, container, false);
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppState.logX(TAG, "onActivityCreated");
        
		viewsInit();
	}
}