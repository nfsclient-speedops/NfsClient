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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public interface GenericStorageInterface {
	public static final String STORAGE_NOTIFICATION_MESSAGE_FORMAT = "%s\n%s\n";

	public static final String STORAGE_BOX = "Box";
	public static final String STORAGE_DROPBOX = "Dropbox";
	public static final String STORAGE_GOOGLE_DRIVE = "Google drive";
	public static final String STORAGE_LOCAL = "Local storage";

	public static final String PUBLIC_GROUPS_FOLDER_NAME = "Groups";
	public static final String PUBLIC_SUBSCRIPTIONS_FOLDER_NAME = "Subscriptions";
	public static final String PUBLIC_DATABASES_FOLDER_NAME = "Databases";
	public static final String COMM_LINK_FORMAT = "/%s/%s_%s";

	public Object fileInfoGet(String name);
	
	public Object folderCreate(String name);
	public boolean folderDelete(String name);
	
    public Object fileExport(Object directory, String remoteFilename, File localFile);
    public Object fileExport(String remoteFilename, File localFile);
    public Object fileExport(String remoteFilename, InputStream localStream, int fileLength);
    public GenericStorageFile fileExport(String remoteFilename, String localFilename);
    public String fileExportAndShare(String remoteFilename, File localFile);
    public String fileExportAndShare(String remoteFilename, InputStream localStream, int fileLength);
    
    public Object fileImport(String remoteFilename, File localFile);
	public Object fileImport(String remoteFilename, OutputStream outputStream);
    public GenericStorageFile fileImport(String remoteFilename, String localFilename);
    	
	public String publicGroupsFolderGet();
	public String publicSubscriptionsFolderGet();
	public String commLinkFormat(String folderName, String primaryName, String secondaryName);
}