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

import android.content.Context;
import android.util.AttributeSet;

import com.actionbarsherlock.internal.widget.IcsSpinner;
import com.app.nfsclient.AppState;

public class GenericIcsSpinner extends IcsSpinner {
	private static final String TAG = "GenericIcsSpinner";

	public GenericIcsSpinner(Context context, AttributeSet attrs) {
	    super(context, attrs, com.actionbarsherlock.R.attr.actionDropDownStyle);
		
		AppState.logX(TAG, "constructor 1");
	}

	public GenericIcsSpinner(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);

		AppState.logX(TAG, "constructor 2");
	}
}