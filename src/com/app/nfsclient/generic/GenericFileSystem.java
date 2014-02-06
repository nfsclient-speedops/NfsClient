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

import android.os.Parcel;
import android.os.Parcelable;

import com.app.nfsclient.AppState;
import com.app.nfsclient.Utils;

public class GenericFileSystem extends GenericListItem implements Parcelable {
    private static final String TAG = "GenericFileSystem";
    
	public GenericFileSystem(String itemType, String primaryName, String secondaryName) {
		super(itemType, primaryName, secondaryName);
		AppState.logX(TAG, "constructor");
	}

	public GenericFileSystem(String itemType) {
		super(itemType, Utils.EMPTY_STRING, Utils.EMPTY_STRING);
	}
	
    @Override
	public void writeToParcel(Parcel out, int arg1) {
		out.writeString(firstGet());
		out.writeString(secondGet());
		out.writeString(thirdGet());
		out.writeString(fourthGet());
		out.writeString(fifthGet());
	}
	
    public final Parcelable.Creator<Parcelable> CREATOR = new Parcelable.Creator<Parcelable>() {
        public GenericFileSystem createFromParcel(Parcel in) {
            return new GenericFileSystem(in);
        }

        public Parcelable[] newArray(int size) {
            return new GenericFileSystem[size];
        }
    };

    public GenericFileSystem(Parcel in) {
    	this(in.readString(), in.readString(), in.readString());
    }
    
	public String fileSystemTypeGet() {
		return Utils.EMPTY_STRING;
	}
	
	public GenericFileInterface fileInstanceGet(String path) {
		return null;
	}

	@Override
	public int describeContents() {
		return 0;
	}
}