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

import java.util.ArrayList;
import java.util.List;

import org.jsoup.helper.StringUtil;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.nfsclient.AppState;
import com.app.nfsclient.generic.GenericFileInterface;
import com.app.nfsclient.generic.GenericFileSystem;
import com.app.nfsclient.generic.GenericListItem;

public class Server extends GenericFileSystem {
	private static final String TAG = "Server";
			             
	public static final String EXPORT_DIRECTORIES_SEPARATOR = ":::::";
	
    private List<String> serverExportDirectories;
    
    public Server(String serverHostName, String serverInternetAddress, String userName, String userPassword,
    	List<String> serverExportDirectories) {
    	super(GenericListItem.ITEM_TYPE_NFS_SERVER, serverHostName, serverInternetAddress);
    	AppState.log(TAG, "constructor 1");
    	
    	serverUserNameSet(userName);
    	serverUserPasswordSet(userPassword);
    	this.serverExportDirectories = serverExportDirectories;
    }
    public Server(String serverHostName, String serverInternetAddress, String userName, String userPassword,
    	String serverExportDirectoriesStr) {
    	super(GenericListItem.ITEM_TYPE_NFS_SERVER, serverHostName, serverInternetAddress);
    	AppState.log(TAG, "constructor 2");

    	serverUserNameSet(userName);
    	serverUserPasswordSet(userPassword);
    	serverExportDirectories = serverExportDirectoriesSet(serverExportDirectoriesStr);
    }
    public Server(String serverHostName, String serverInternetAddress, String userName, String userPassword,
       	String[] serverExportDirectoriesArray) {
       	super(GenericListItem.ITEM_TYPE_NFS_SERVER, serverHostName, serverInternetAddress);
       	AppState.log(TAG, "constructor 3");

       	serverUserNameSet(userName);
       	serverUserPasswordSet(userPassword);
       	serverExportDirectories = serverExportDirectoriesSet(serverExportDirectoriesArray);
    }
    
    public String serverHostNameGet() {
    	return first;
    }
    public void serverHostNameSet(String hostName) {
    	first = hostName;
    }
    
    public String serverInternetAddressGet() {
    	return second;
    }
    public void serverInternetAddressSet(String address) {
    	second = address;
    }
    
    public String serverUserNameGet() {
    	return third;
    }
    public void serverUserNameSet(String name) {
    	third = name;
    }
    
    public String serverUserPasswordGet() {
    	return fourth;
    }
    public void serverUserPasswordSet(String password) {
    	fourth = password;
    }
    
    public String localMountDirectoryGet() {
    	return fifth;
    }
    public void localMountDirectorySet(String mount) {
    	fifth = mount;
    }
    
    public String serverMountOptionGet() {
    	return sixth;
    }
    public void serverMountOptionsSet(String options) {
    	sixth = options;
    }
    
    public List<String> serverExportDirectoriesGet() {
    	return serverExportDirectories;
    }
    public static String serverExportDirectoriesStringGet(List<String> list) {
    	return StringUtil.join(list, EXPORT_DIRECTORIES_SEPARATOR);
    }
    public static String[] serverExportDirectoriesArrayGet(List<String> list) {
    	String[] array = null;
    	
    	if (list != null && list.size() > 0) {
    		array = new String[list.size()];
    		for (int i = 0; i < list.size(); i++)
    			array[i] = list.get(i);
    	}
    	
    	return array;
    }
    public void serverExportDirectoriesSet(List<String> exports) {
    	this.serverExportDirectories = exports;
    }
    public static List<String> serverExportDirectoriesSet(String exports) {
       	String[] array = serverExportsDirectoriesStringParse(exports);
       	List<String> list = new ArrayList<String>();
       	for (String export : array)
       		list.add(export);
       	
       	return list;
    }
    public static List<String> serverExportDirectoriesSet(String[] exports) {
       	List<String> list = new ArrayList<String>();
       	for (String export : exports)
       		list.add(export);
       	
       	return list;
    }
    
    public static String[] serverExportsDirectoriesStringParse(String exports) {
    	return exports.split(EXPORT_DIRECTORIES_SEPARATOR);
    }
    
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel out, int arg1) {
		out.writeString(serverHostNameGet());
		out.writeString(serverInternetAddressGet());
		out.writeString(serverUserNameGet());
		out.writeString(serverUserPasswordGet());
		out.writeStringList(serverExportDirectoriesGet());
	}
	
    public static final Parcelable.Creator<Parcelable> CREATOR = new Parcelable.Creator<Parcelable>() {
        public Server createFromParcel(Parcel in) {
            return new Server(in);
        }

        public Parcelable[] newArray(int size) {
            return new Server[size];
        }
    };

    public Server(Parcel in) {
    	this(in.readString(), in.readString(), in.readString(), in.readString(), new ArrayList<String>());
        in.readStringList(serverExportDirectories);
    }
    
    @Override
	public String fileSystemTypeGet() {
		return itemTypeGet();
	}
	
	public GenericFileInterface fileInstanceGet(String path) {
		return new NfsFile(path);
	}
}