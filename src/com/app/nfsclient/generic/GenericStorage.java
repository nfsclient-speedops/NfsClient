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

public abstract class GenericStorage extends GenericListItem implements GenericStorageInterface {
    private static final String TAG = "GenericStorage";
    
    protected Object rootDirectory = null;
    
	public GenericStorage(String itemType, String primaryName, String secondaryName, String storagType) {
		super(itemType, primaryName, secondaryName);
		
		AppState.log(TAG, "constructor");
		
		this.storageType = storagType;
	}
	
	public Object cloudAccountRootDirectoryGet() {
		return rootDirectory;
	}
	public void cloudAccountRootDirectory(Object cloudAccountRootDirectory) {
		this.rootDirectory = cloudAccountRootDirectory;
	}
}