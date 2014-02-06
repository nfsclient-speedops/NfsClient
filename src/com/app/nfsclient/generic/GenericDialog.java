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
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GenericDialog extends Dialog {
    protected static final String TAG = "GenericDialog";
    	
    protected Context context;
    protected int layoutResid = R.layout.generic_dialog;

    protected LinearLayout dialogLayout;
    protected TextView titleView;
    protected View titleDivider;
    protected LinearLayout messageLayout;
	protected TextView messageView;
	protected LinearLayout bodyLayout;
	protected View view;
	
	protected Button positiveButton;
	protected int positiveButtonResid = R.id.genericDialogRightButton;
	protected int positiveButtonLabel = R.string.genericApplyButtonLabel;
	protected View.OnClickListener positiveButtonListener;
	
	protected Button negativeButton;
	protected int negativeButtonResid = R.id.genericDialogLeftButton;
	protected int negativeButtonLabel = R.string.genericCancelButtonLabel;
	protected View.OnClickListener negativeButtonListener;
	
	protected String titleString;
	protected String messageString;

	public GenericDialog(Context context, String titleString, String messageString, View view) {
		super(context, R.style.notificationDialogTheme);
		
		this.context = context;
		this.titleString = titleString;
		this.messageString = messageString;
		this.view = view;
	}
	public GenericDialog(Context context, int titleId, int messageId, View view) {
		this(context, context.getString(titleId), context.getString(messageId), view);
	}
	public GenericDialog(Context context, int titleId, String messageString, View view) {
		this(context, context.getString(titleId), messageString, view);
	}
	public GenericDialog(Context context, String titleString, int messageId, View view) {
		this(context, titleString, context.getString(messageId), view);
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppState.logX(TAG, "onCreate");
            
        setContentView(layoutResid);
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        LayoutParams params = getWindow().getAttributes(); 
        params.width = LayoutParams.MATCH_PARENT; 
        getWindow().setAttributes((android.view.WindowManager.LayoutParams)params); 

        if ((dialogLayout = (LinearLayout)findViewById(R.id.genericDialogLayout)) != null)
            dialogLayout.setBackgroundColor(Background.windowBackgroundGet(context));
        
        titleView = (TextView)findViewById(R.id.genericDialogTitle);
        titleDivider = (View)findViewById(R.id.genericDialogTitleDivider);
        
        if ((messageLayout = (LinearLayout)findViewById(R.id.genericDialogMessageLayout)) != null)
            messageLayout.setBackgroundColor(Background.windowBackgroundGet(context));
        
        messageView = (TextView)findViewById(R.id.genericDialogMessage);
        bodyLayout = (LinearLayout)findViewById(R.id.genericDialogBody);
		
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        	positiveButtonResid = R.id.genericDialogLeftButton;
        	negativeButtonResid = R.id.genericDialogRightButton;
        }
        
        if ((positiveButton = (Button)findViewById(positiveButtonResid)) != null) {
        	positiveButton.setText(positiveButtonLabel);        	
        	if (positiveButtonListener != null)
        		positiveButton.setOnClickListener(positiveButtonListener);
        	else
        		positiveButton.setOnClickListener(new View.OnClickListener() {
        			public void onClick(View v) {
        				dismiss();
        			}
        		});
		
        	positiveButton.setTextColor(Background.textColorGet(context));
        	positiveButton.setBackgroundDrawable(Background.positiveButtonSelectorGet(context));
        }
		
        if ((negativeButton = (Button)findViewById(negativeButtonResid)) != null) {
        	negativeButton.setText(negativeButtonLabel);
        	if (negativeButtonListener != null)
        		negativeButton.setOnClickListener(negativeButtonListener);
        	else
        		negativeButton.setOnClickListener(new View.OnClickListener() {
        			public void onClick(View v) {
        				dismiss();
        			}
        		});

			negativeButton.setTextColor(Background.textColorGet(context));
            negativeButton.setBackgroundDrawable(Background.negativeButtonSelectorGet(context));
        }
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		AppState.logX(TAG, "onStart");

		titleView.setText(titleString);
		titleView.setTextColor(Background.textColorGet(context));

//      titleDivider.setBackgroundDrawable(AppState.colorThemeSolidListDividerGet(context));
        titleDivider.setBackgroundDrawable(Background.solidListDividerDrawableGet());
        titleDivider.setMinimumHeight((int)context.getResources().getDimension(
        	R.dimen.default_line_spacer_height));
        
		if (!TextUtils.isEmpty(messageString)) {
		    messageView.setText(messageString);
		    messageView.setTextColor(Background.textColorGet(context));
		} else {
			messageView.setMaxHeight(0);
		}
		
		if (view != null) {
			view.setBackgroundColor(Background.windowBackgroundGet(context));
		    bodyLayout.addView(view);
		}
	}

	public TextView titleViewGet() {
		return titleView;
	}
	public void titleViewSet(TextView titleView) {
		this.titleView = titleView;
	}
	
	public View titleDividerGet() {
		return titleDivider;
	}
	
	public TextView messageViewGet() {
		return messageView;
	}
	public void messageViewSet(TextView messageView) {
		this.messageView = messageView;
	}
	
	public View viewGet() {
		return view;
	}
	public void viewSet(View view) {
		this.view = view;
	}
	
	public void positiveButtonListenerSet(int label, View.OnClickListener listener) {
		positiveButtonLabel = label;
		positiveButtonListener = listener;
	}
	public void positiveButtonListenerSet(View.OnClickListener positiveButtonListener) {
		this.positiveButtonListener = positiveButtonListener;
	}
	
	public void negativeButtonListenerSet(int label, View.OnClickListener listener) {
	    negativeButtonLabel = label;
	    negativeButtonListener = listener;
	}
	public void negativeButtonListenerSet(View.OnClickListener negativeButtonListener) {
		this.negativeButtonListener = negativeButtonListener;
	}
	
	public GenericDialog dialogShow() {
        AppState.logX(TAG, "dialogShow");

        show();
        return this;
	}
}