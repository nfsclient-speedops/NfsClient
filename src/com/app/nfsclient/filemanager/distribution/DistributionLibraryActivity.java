/* 
 * Copyright (C) 2007-2011 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.nfsclient.filemanager.distribution;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;

public class DistributionLibraryActivity extends Activity {

	static final int MENU_DISTRIBUTION_START = Menu.FIRST;
	
	static final int DIALOG_DISTRIBUTION_START = 1;

	protected DistributionLibrary mDistribution;
	
    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mDistribution = new DistributionLibrary(this, MENU_DISTRIBUTION_START, DIALOG_DISTRIBUTION_START);
    }
/*
 	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
 		super.onCreateOptionsMenu(menu);
 		mDistribution.onCreateOptionsMenu(menu);
 		return true;
 	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDistribution.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
*/
	@Override
	protected Dialog onCreateDialog(int id) {
		return mDistribution.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		mDistribution.onPrepareDialog(id, dialog);
	}
}