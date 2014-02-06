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
import java.util.List;

import com.app.nfsclient.AppState;
import com.app.nfsclient.Utils;
import com.app.nfsclient.images.ImagesUtils;

import android.content.Context;
import android.graphics.Bitmap;

public abstract class GenericListItem implements Comparable<GenericListItem> {
    private static final String TAG = "GenericListItem";
	
    public static final String ACTIVITY_RESULT_FILE_SELECTION_LOGIN_NAME_KEY = AppState.APP_PACKAGE_NAME + "." +
        TAG + ".activityResultFileSelectionLoginNameKey";
    public static final String ACTIVITY_RESULT_FILE_SELECTION_FILE_NAME_KEY = AppState.APP_PACKAGE_NAME + "." +
        TAG + ".activityResultFileSelectionFileNameKey";
    
    public static final int ACTIVITY_RESULT_FILE_SELECTION_PROVIDER_MASK = 0xff00;    
	public static final int ACTIVITY_RESULT_FILE_SELECTION_PROVIDER_LOCAL_STORAGE = 0x0300;
	
	public static final int ACTIVITY_RESULT_FILE_SELECTION_TYPE_ALL_MASK = 0xff;
	public static final int ACTIVITY_RESULT_FILE_SELECTION_TYPE_SUPER_MASK = 0xc0;
    public static final int ACTIVITY_RESULT_FILE_SELECTION_TYPE_EXPORT = 0x80;
	public static final int ACTIVITY_RESULT_FILE_SELECTION_TYPE_IMPORT = 0x40;
	
	public static final int ACTIVITY_RESULT_FILE_SELECTION_TYPE_SUB_MASK = 0x3f;
	
    public static final int ACTIVITY_RESULT_LIST_REFRESH = 1;
	public static final int ACTIVITY_RESULT_ACCOUNT_CONNECT = 2;
	public static final int ACTIVITY_RESULT_ACCOUNT_EDIT = 3;
	
    public static final int DIALOG_LOADING = 0;
    public static final int DIALOG_DELETING = 1;
	public static final int DIALOG_REFRESHING = 2;
    public static final int DIALOG_FILE_SEARCH = 3;
    public static final int DIALOG_FILE_IMPORT = 4;
    public static final int DIALOG_FILE_EXPORT = 5;
    
    public static final String ITEM_TYPE_GENERIC_LIST_ITEM_OBJECT = "GenericListItemObject";
    public static final long ITEM_TYPE_GENERIC_LIST_ITEM_OBJECT_ID = 0;
    public static final String ITEM_TYPE_GENERIC_LIST_ITEM_STRING = "GenericListItemString";
    public static final long ITEM_TYPE_GENERIC_LIST_ITEM_STRING_ID = 1;
    public static final String ITEM_TYPE_NFS_SERVER = "NFS server";
    public static final long ITEM_TYPE_NFS_SERVER_ID = 2;
    public static final String ITEM_TYPE_LOCAL_STORAGE = "Local storage";
    public static final long ITEM_TYPE_LOCAL_STORAGE_ID = 3;
     
    public static final int GENERIC_LIST_ITEM_BUTTON_LEFT = 0;
    public static final int GENERIC_LIST_ITEM_BUTTON_RIGHT = 1;
    
    private static final List<ItemType> itemTypes = new ArrayList<ItemType>();
	public static enum ItemType {	    
        GenericListItemObject(ITEM_TYPE_GENERIC_LIST_ITEM_OBJECT, ITEM_TYPE_GENERIC_LIST_ITEM_OBJECT_ID),
        GenericListItemString(ITEM_TYPE_GENERIC_LIST_ITEM_STRING, ITEM_TYPE_GENERIC_LIST_ITEM_STRING_ID),
		Server(ITEM_TYPE_NFS_SERVER, ITEM_TYPE_NFS_SERVER_ID),
		StorageLocal(ITEM_TYPE_LOCAL_STORAGE, ITEM_TYPE_LOCAL_STORAGE_ID)
		;
        
        private final String name;
        private final long id;
        
        private ItemType(String name, long id) {
            this.name = name;
            this.id = id;
            
            itemTypes.add(this);
        }

        public long idGet() {
        	return id;
        }
        
        public static long getIdByName(String name) {
        	long id = -1;
        	for (ItemType itemType : itemTypes) {
        		if (itemType.name.equalsIgnoreCase(name)) {
        			id = itemType.id;
        			break;
        		}
        	}
        	
        	return id;
        }
        
        public static String getNameById(long id) {
        	String name = null;
        	for (ItemType itemType : itemTypes) {
        		if (itemType.id == id) {
        			name = itemType.name;
        			break;
        		}
        	}
        	
        	return name;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
	
	// callback handler whats
	protected Context context = null;
	
	protected String itemType = Utils.EMPTY_STRING;
	protected String userId = Utils.EMPTY_STRING;
    protected String first = Utils.EMPTY_STRING;
    protected String second = Utils.EMPTY_STRING;
    protected String third = Utils.EMPTY_STRING;
    protected String fourth = Utils.EMPTY_STRING;
    protected String fifth = Utils.EMPTY_STRING;
    protected String sixth = Utils.EMPTY_STRING;
    protected String seventh = Utils.EMPTY_STRING;
    protected String eigth = Utils.EMPTY_STRING;
    protected String ninth = Utils.EMPTY_STRING;
    protected String tenth = Utils.EMPTY_STRING;
    
    public static final long RESERVED_CONTAINER_ID = Integer.MAX_VALUE;
            
    protected Boolean storageEnable = false;
    protected String storageName = Utils.EMPTY_STRING;
    protected String storageType = Utils.EMPTY_STRING;
    
    public GenericListItem(String itemType, String first, String second) {
    	this.itemType = itemType;
    	this.first = first;
    	this.second = second;
    }
	
    @Override
    public String toString() {
    	return first;
    }
    
	public String userIdGet() {
		return userId;
	}
	public void userIdSet(String userId) {
		this.userId = userId;
	}
	
	public String firstGet() {
		return first;
	}
	public void firstSet(String first) {		
		this.first = first;
	}
	
	public String secondGet() {
		return second;
	}
	public void secondSet(String second) {		
		this.second = second;
	}
	
	public String thirdGet() {
		return third;
	}
	public void third(String third) {		
		this.third = third;
	}
	
	public String fourthGet() {
		return fourth;
	}
	public void fourthSet(String fourth) {		
		this.fourth = fourth;
	}
	
	public String fifthGet() {
		return fifth;
	}
	public void fifthSet(String fifth) {		
		this.fifth = fifth;
	}
	
	public String sixthGet() {
		return sixth;
	}
	public void sixthSet(String sixth) {		
		this.sixth = sixth;
	}
	
	public String seventhGet() {
		return seventh;
	}
	public void seventhSet(String seventh) {		
		this.seventh = seventh;
	}
	
	public String eigthGet() {
		return eigth;
	}
	public void eigthSet(String eigth) {		
		this.eigth = eigth;
	}
	
	public String ninthGet() {
		return ninth;
	}
	public void ninthSet(String ninth) {		
		this.ninth = ninth;
	}
	
	public String tenthGet() {
		return tenth;
	}
	public void tenthSet(String tenth) {		
		this.tenth = tenth;
	}
	
	public String itemTypeGet() {
		return itemType;
	}
	public void itemTypeSet(String itemType) {
		this.itemType = itemType;
	}
	public long itemTypeIdGet() {
		return ItemType.getIdByName(itemType);
	}
	
	public Bitmap imageDefaultBitmapGet() {
		Bitmap bitmap = null;
		bitmap = ImagesUtils.itemDefaultBitmapImageGet(context);
		
		return bitmap;
	}
	public static Bitmap defaultBitmapGet(Context context, GenericListItem item) {
		Bitmap bitmap = null;
		bitmap = ImagesUtils.itemDefaultBitmapImageGet(context);
		
		return bitmap;
	}
	
	public Boolean storageEnableGet() {
		return storageEnable;
	}
	public void storageEnableSet(Boolean storageEnable) {
		this.storageEnable = storageEnable;
	}
	
	public String storageNameGet() {
		return storageName;
	}
	public void storageNameSet(String storageName) {
		this.storageName = storageName;
	}
	
	public String storageTypeGet() {
		return storageType;
	}
	public void storageTypeSet(String storageType) {
		this.storageType = storageType;
	}
	
	public int compareTo(GenericListItem item) {
		return first.toLowerCase().compareTo(item.first.toLowerCase());
	}
}