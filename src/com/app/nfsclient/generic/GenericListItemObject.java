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

import com.app.nfsclient.Utils;

public class GenericListItemObject extends GenericListItem {
    @SuppressWarnings("unused")
	private static final String TAG = "GenericListItemObject";
    
    private Object extra;
    
	public GenericListItemObject(String itemType, String primaryName, String secondaryName, Object extra) {
		super(itemType, primaryName, secondaryName);
		
		this.extra = extra;
	}
	public GenericListItemObject() {
		this(ITEM_TYPE_GENERIC_LIST_ITEM_OBJECT, Utils.EMPTY_STRING, Utils.EMPTY_STRING, null);
	}
	
	public Object extraGet() {
		return extra;
	}
}