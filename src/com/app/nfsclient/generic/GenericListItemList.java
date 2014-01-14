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

import com.app.nfsclient.AppState;
import com.app.nfsclient.Utils;

public class GenericListItemList extends ArrayList<GenericListItem> {
	private static final String TAG = "GenericItemList";

	private static final long serialVersionUID = 1L;
    
    public static class GenericListItemString extends GenericListItem {
        private static final String TAG = GenericListItemList.TAG + "GenericListItemString";
        
		public GenericListItemString(String itemType, String primaryName, String secondaryName,
			String imageLocation, String imageType, long localAppId) {
			super(itemType, primaryName, secondaryName);
			AppState.logX(TAG, "constructor");
			
		}
    }
    
    public void add(String item) {
		AppState.logX(TAG, "add 1");
		
		add(new GenericListItemString(GenericListItem.ITEM_TYPE_GENERIC_LIST_ITEM_STRING, item,
			Utils.EMPTY_STRING, null, null, 0));
	}
    public void add(String item, String itemType) {
		AppState.logX(TAG, "add 2");
		
		add(new GenericListItemString(itemType, item, Utils.EMPTY_STRING, null, null, 0));
	}
    
	public void add(String[] array) {
		AppState.logX(TAG, "add 3");
		
		for (String item : array) {
			add(new GenericListItemString(GenericListItem.ITEM_TYPE_GENERIC_LIST_ITEM_STRING, item,
				Utils.EMPTY_STRING, null, null, 0));
		}
	}
	public void add(String[] array, String itemType) {
		AppState.logX(TAG, "add 4");
		
		for (String item : array) {
			add(new GenericListItemString(itemType, item, Utils.EMPTY_STRING, null, null, 0));
		}
	}
}
