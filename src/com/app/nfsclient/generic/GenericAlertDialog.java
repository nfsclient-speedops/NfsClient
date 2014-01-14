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

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class GenericAlertDialog extends AlertDialog {
	private static final String TAG = "GenericAlertDialog";

	private Context context;

	private LinearLayout dialogLayout;
	private ImageView iconView;
	private int iconResId;
	private TextView titleView;
	private String title;
	private View titleDivider;
	private TextView messageView;
	private String message;
	private View buttonDivider;

	private AlertDialog dialog;
	
	private View.OnClickListener positiveButtonListener;
	private int positiveButtonResid = R.id.genericAlertDialogRightButton;
	private int positiveButtonLabel = R.string.genericApplyButtonLabel;
	private Button positiveButton;
	private String positiveButtonText;
	
	private View.OnClickListener negativeButtonListener;
	private int negativeButtonResid = R.id.genericAlertDialogLeftButton;
	private int negativeButtonLabel = R.string.genericCancelButtonLabel;
	private Button negativeButton;
	private String negativeButtonText;
    
	private View.OnClickListener neutralButtonListener;
	private int neutralButtonResid = R.id.genericAlertDialogNeutralButton;
	private int neutralButtonLabel = R.string.genericCloseButtonLabel;
	private Button neutralButton;
	private String neutralButtonText;
	
	public GenericAlertDialog(Context context, String title, String message,
		View.OnClickListener positiveButtonListener, String positiveButtonText,
		View.OnClickListener negativeButtonListener, String negativeButtonText,
		View.OnClickListener neutralButtonListener, String neutralButtonText) {
		super(context, R.style.notificationDialogTheme);

		this.context = context;
		this.title = title;
		this.message = message;
		
		this.positiveButtonListener = positiveButtonListener;
		this.positiveButtonText = positiveButtonText;
		
		this.negativeButtonListener = negativeButtonListener;
		this.negativeButtonText = negativeButtonText;
		
		this.neutralButtonListener = neutralButtonListener;
		this.neutralButtonText = positiveButtonText;
	}
	public GenericAlertDialog(Context context, String title, String message) {
		this(context, title, message, null, null, null, null, null, null);
	}
	public GenericAlertDialog(Context context) {
		this(context, null, null, null, null, null, null, null, null);
	}
	public GenericAlertDialog(Context context, AlertDialog dialog) {
		this(context);
		
		this.dialog = dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppState.logX(TAG, String.format("onCreate: neutralButtonListener = %s, neutralButtonText = %s",
			neutralButtonListener, neutralButtonText));

        if (neutralButtonListener != null || !TextUtils.isEmpty(neutralButtonText))
			setContentView(R.layout.generic_alert_dialog_one_button);
		else
			setContentView(R.layout.generic_alert_dialog_two_buttons);

		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		if ((dialogLayout = (LinearLayout)findViewById(R.id.genericAlertDialogLayout)) != null)
            dialogLayout.setBackgroundColor(Background.windowBackgroundGet(context));
        
		if ((titleView = (TextView)findViewById(R.id.genericAlertDialogTitle)) != null) {
		    titleView.setText(title);
		    titleView.setTextColor(Background.textColorGet(context));
		}

		if ((titleDivider = (View)findViewById(R.id.genericAlertTitleDivider)) != null) {
		    titleDivider.setBackgroundDrawable(Background.solidListDividerDrawableGet());
		    titleDivider.setMinimumHeight((int)context.getResources().getDimension(
		    	R.dimen.default_line_spacer_height));			
		}

		if ((messageView = (TextView)findViewById(R.id.genericAlertDialogMessage)) != null) {
			if (!TextUtils.isEmpty(message)) {
				messageView.setText(message);
				messageView.setTextColor(Background.textColorGet(context));
			} else {
				messageView.setMaxHeight(0);
			}
		}
		
		if ((buttonDivider = (View)findViewById(R.id.genericAlertButtonDivider)) != null) {
		    titleDivider.setBackgroundDrawable(Background.solidListDividerDrawableGet());
			buttonDivider.setMinimumHeight((int)context.getResources().getDimension(
		    	R.dimen.default_line_spacer_height));			
		}
		
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        	positiveButtonResid = R.id.genericAlertDialogLeftButton;
        	negativeButtonResid = R.id.genericAlertDialogRightButton;
        }
        
		if ((positiveButton = (Button)findViewById(positiveButtonResid)) != null) {
			if (!TextUtils.isEmpty(positiveButtonText))
				positiveButton.setText(positiveButtonText);
			else
				positiveButton.setText(positiveButtonLabel);
			
			if (positiveButtonListener != null)
				positiveButton.setOnClickListener(positiveButtonListener);
			else
				positiveButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {	            
						AppState.log(TAG, "positiveButtonListener");

						dismiss();
					}
				});
			
        	positiveButton.setTextColor(Background.textColorGet(context));
            positiveButton.setBackgroundDrawable(Background.positiveButtonSelectorGet(context));
		}
		
		if ((negativeButton = (Button)findViewById(negativeButtonResid)) != null) {
			if (!TextUtils.isEmpty(negativeButtonText))
				negativeButton.setText(negativeButtonText);
			else
				negativeButton.setText(negativeButtonLabel);
			
			if (negativeButtonListener != null)
				negativeButton.setOnClickListener(negativeButtonListener);
			else
				negativeButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {	            
						AppState.log(TAG, "negativeButtonListener");

						dismiss();
					}
				});

        	negativeButton.setTextColor(Background.textColorGet(context));
            negativeButton.setBackgroundDrawable(Background.negativeButtonSelectorGet(context));
		}
		
		if ((neutralButton = (Button)findViewById(neutralButtonResid)) != null) {
			if (!TextUtils.isEmpty(neutralButtonText))
				neutralButton.setText(neutralButtonText);
			else
				neutralButton.setText(neutralButtonLabel);
			
			if (neutralButtonListener != null)
				neutralButton.setOnClickListener(neutralButtonListener);
			else
				neutralButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {	            
						AppState.log(TAG, "neutralButtonListener");

						dismiss();
					}
				});
			neutralButton.setTextColor(Background.textColorGet(context));
            neutralButton.setBackgroundDrawable(Background.neutralButtonSelectorGet(context));
		}
	}
/*
	@Override
	protected void onStart() {
		super.onStart();
		AppState.log(TAG, "onStart");

		ColorTheme theme = AppState.colorThemeGet(context);
		
		titleView.setText(title);
		titleView.setTextColor(theme.textColorGet(context));
		
		titleDivider.setBackgroundDrawable(theme.solidListDividerGet());
		titleDivider.setMinimumHeight((int)context.getResources().getDimension(
			R.dimen.default_line_spacer_height));
		
		if (!TextUtils.isEmpty(message)) {
		    messageView.setText(message);
		    messageView.setTextColor(theme.textColorGet(context));
		}
		else
			messageView.setMaxHeight(0);
	}
*/	
	public AlertDialog dialogGet() {
		return dialog;
	}
	public void dialogSet(AlertDialog dialog) {
		this.dialog = dialog;
	}
	
	public GenericAlertDialog cancelableSet(boolean flag) {
		this.setCancelable(flag);
		return this;
	}
	
	public ImageView iconViewGet() {
		return iconView;
	}
	public GenericAlertDialog iconViewSet(ImageView iconView) {
		this.iconView = iconView;
		return this;
	}
	
	public int iconResIdGet() {
		return iconResId;
	}
	public GenericAlertDialog iconSet(int iconResId) {
		this.iconResId = iconResId;
		return this;
	}
	
	public GenericAlertDialog titleSet(String title) {
		this.title = title;
		return this;
	}
	public GenericAlertDialog titleSet(int resId) {
		return titleSet(context.getString(resId));
	}
	
	public GenericAlertDialog messageSet(String message) {
		this.message = message;
		return this;
	}
	public GenericAlertDialog messageSet(int resId) {
		return messageSet(context.getString(resId));
	}
	
	public GenericAlertDialog positiveButtonSet(String positiveButtonText,
		View.OnClickListener positiveButtonListener) {
		this.positiveButtonText = positiveButtonText;
		this.positiveButtonListener = positiveButtonListener;
		return this;
	}
	public GenericAlertDialog positiveButtonSet(int resId, View.OnClickListener positiveButtonListener) {
		return positiveButtonSet(context.getString(resId), positiveButtonListener);
	}
	public GenericAlertDialog positiveButtonSet(View.OnClickListener positiveButtonListener) {
		return positiveButtonSet(R.string.genericApplyButtonLabel, positiveButtonListener);
	}
	
	public GenericAlertDialog negativeButtonSet(String negativeButtonText,
		View.OnClickListener negativeButtonListener) {
		this.negativeButtonText = negativeButtonText;
		this.negativeButtonListener = negativeButtonListener;
		return this;
	}
	public GenericAlertDialog negativeButtonSet(int resId, View.OnClickListener negativeButtonListener) {
		return negativeButtonSet(context.getString(resId), negativeButtonListener);
	}
	public GenericAlertDialog negativeButtonSet(View.OnClickListener negativeButtonListener) {
		return negativeButtonSet(R.string.genericCancelButtonLabel, negativeButtonListener);
	}

	public GenericAlertDialog neutralButtonSet(String neutralButtonText,
		View.OnClickListener neutralButtonListener) {
		this.neutralButtonText = neutralButtonText;
		this.neutralButtonListener = neutralButtonListener;
		return this;
	}
	public GenericAlertDialog neutralButtonSet(int resId, View.OnClickListener neutralButtonListener) {
		return neutralButtonSet(context.getString(resId), neutralButtonListener);
	}
	public GenericAlertDialog neutralButtonSet(View.OnClickListener neutralButtonListener) {
		return neutralButtonSet(R.string.genericCloseButtonLabel, neutralButtonListener);
	}
		
	public Context contextGet() {
		return context;
	}
	
	public void dialogShow() {
		AppState.log(TAG, "dialogShow");

		if (dialog != null)
			dialog.show();
	}
}