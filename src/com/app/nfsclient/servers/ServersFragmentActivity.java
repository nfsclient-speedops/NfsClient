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

package com.app.nfsclient.servers;

import com.app.nfsclient.AppState;
import com.app.nfsclient.generic.GenericFragmentActivity;
import com.app.nfsclient.generic.GenericListItem;
import com.app.nfsclient.generic.GenericListItemsListFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ServersFragmentActivity extends GenericFragmentActivity {
    private static final String TAG = "GroupsFragmentActivity";
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
    	AppState.log(TAG, "onCreate");

    	fragment = new ServersListFragment();
        fragmentTag = ServersListFragment.FRAGMENT_TAG;
        if (savedInstanceState == null)
            fragmentArguments = getIntent().getExtras();
        else
        	fragmentArguments = savedInstanceState.getBundle(fragmentTag);
        
    	super.onCreate(savedInstanceState);
    }
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		AppState.logX(TAG, String.format("onActivityResult: requestCode = 0x%x, resultCode = %d", requestCode,
			resultCode));
		
        switch(requestCode) {
        case GenericListItem.ACTIVITY_RESULT_LIST_REFRESH:
			AppState.logX(TAG, "onActivityResult: ACTIVITY_RESULT_LIST_REFRESH"); 

        case GenericListItem.ACTIVITY_RESULT_ACCOUNT_EDIT:
			AppState.logX(TAG, "onActivityResult: ACTIVITY_RESULT_ACCOUNT_EDIT"); 

        case GenericListItem.ACTIVITY_RESULT_ACCOUNT_CONNECT:
			AppState.logX(TAG, "onActivityResult: ACTIVITY_RESULT_ACCOUNT_CONNECT"); 

			if (resultCode == Activity.RESULT_OK) {
				String accountName = data.getStringExtra(ServersListFragment.SERVER_INTERNET_ADDRESS_KEY);
				
				AppState.logX(TAG, String.format("onActivityResult: connect returned = %s",
					accountName));
				
				// retrieve the fragment sent to the activity
				GenericListItemsListFragment fragment =
					(GenericListItemsListFragment)getSupportFragmentManager().getFragment(
						data.getBundleExtra(fragmentTag), fragmentTag);
				listFragmentRefresh(fragment, new ServersListFragment());
			}
			
    		break;
    		
		default:
			break;
		}
	}
}