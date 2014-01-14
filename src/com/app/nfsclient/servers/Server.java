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

import com.app.nfsclient.generic.GenericListItem;

public class Server extends GenericListItem {
	@SuppressWarnings("unused")
	private static final String TAG = "Server";
			             
	public static final String EXPORT_DIRECTORIES_SEPARATOR = ":::::";
	
    private List<String> serverExportDirectories;
    
    public Server(String serverHostName, String serverInternetAddress, List<String> serverExportDirectories) {
    	super(GenericListItem.ITEM_TYPE_SERVER, serverHostName, serverInternetAddress);
    	
    	this.serverExportDirectories = serverExportDirectories;
    }
    public Server(String serverHostName, String serverInternetAddress, String serverExportDirectoriesStr) {
    	super(GenericListItem.ITEM_TYPE_SERVER, serverHostName, serverInternetAddress);
    	
    	String[] serverExportsArray = this.serverExportsDirectoriesStringParse(serverExportDirectoriesStr);
    	serverExportDirectories = new ArrayList<String>();
    	for (String export : serverExportsArray)
    		serverExportDirectories.add(export);
    }
  
    public String serverHostNameGet() {
    	return primaryName;
    }
    public void serverHostNameSet(String hostName) {
    	primaryName = hostName;
    }
    
    public String serverInternetAddressGet() {
    	return secondaryName;
    }
    public void serverInternetAddressSet(String address) {
    	secondaryName = address;
    }
    
    public List<String> serverExportDirectoriesGet() {
    	return serverExportDirectories;
    }
    public String serverExportDirectoriesStringGet() {
    	return StringUtil.join(serverExportDirectories, EXPORT_DIRECTORIES_SEPARATOR);
    }
    public void serverExportDirectorySet(List<String> serverExportDirectories) {
    	this.serverExportDirectories = serverExportDirectories;
    }
    
    public String[] serverExportsDirectoriesStringParse(String exports) {
    	return exports.split(EXPORT_DIRECTORIES_SEPARATOR);
    }
}