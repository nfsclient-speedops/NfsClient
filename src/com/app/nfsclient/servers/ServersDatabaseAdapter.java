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
import java.util.Collections;
import java.util.List;

import com.app.nfsclient.AppState;
import com.app.nfsclient.generic.GenericDatabaseAdapterInterface;
import com.app.nfsclient.generic.GenericListItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class ServersDatabaseAdapter implements GenericDatabaseAdapterInterface {
    private static final String TAG = "ServersDatabaseAdapter";
    
    protected Context context;
    protected String databaseName;
    protected DatabaseHelper databaseHelper;
    
    protected static final String DATABASE_NAME = "serversDb";
    protected static final int DATABASE_VERSION = AppState.DATABASE_VERSION;
        
    public static final String KEY_SERVER_HOST_NAME = "serverHostName";
    public static final int KEY_SERVER_HOST_NAME_INDEX = 1;
    public static final String KEY_SERVER_INTERNET_ADDRESS = "serverInternetAddress";
    public static final int KEY_SERVER_INTERNET_ADDRESS_INDEX = 2;
    public static final String KEY_SERVER_USER_NAME = "serverUserName";
    public static final int KEY_SERVER_USER_NAME_INDEX = 3;
    public static final String KEY_SERVER_USER_PASSWORD = "serverUserPassword";
    public static final int KEY_SERVER_USER_PASSWORD_INDEX = 4;
    public static final String KEY_LOCAL_MOUNT_DIRECTORY = "localMountDirectory";
    public static final int KEY_LOCAL_MOUNT_DIRECTORY_INDEX = 5;
    public static final String KEY_SERVER_MOUNT_OPTIONS = "serverMountOptions";
    public static final int KEY_SERVER_MOUNT_OPTIONS_INDEX = 6;
    public static final String KEY_SERVER_EXPORT_DIRECTORIES = "serverExportDirectories";
    public static final int KEY_SERVER_EXPORT_DIRECTORIES_INDEX = 7;
    public static final String KEY_SERVER_ROW_ID = "_id";
    
    public static final String SERVERS_TABLE = "serversTable";
    public SQLiteStatement serversTableInsertStmt = null;
    public static final String[] serversTableColumns = {
    	KEY_SERVER_HOST_NAME,
    	KEY_SERVER_INTERNET_ADDRESS,
    	KEY_SERVER_USER_NAME,
    	KEY_SERVER_USER_PASSWORD,
    	KEY_LOCAL_MOUNT_DIRECTORY,
    	KEY_SERVER_MOUNT_OPTIONS,
    	KEY_SERVER_EXPORT_DIRECTORIES,
    	KEY_SERVER_ROW_ID
    };
    public static final String SERVERS_TABLE_CREATE =
    	"create table if not exists " +
    	SERVERS_TABLE +
    	"(" +
        KEY_SERVER_HOST_NAME + " COLLATE NOCASE," +
        KEY_SERVER_INTERNET_ADDRESS + " VARCHAR," +
        KEY_SERVER_USER_NAME + " VARCHAR," +
        KEY_SERVER_USER_PASSWORD + " VARCHAR," +
        KEY_LOCAL_MOUNT_DIRECTORY + " VARCHAR," +
        KEY_SERVER_MOUNT_OPTIONS + " VARCHAR," +
        KEY_SERVER_EXPORT_DIRECTORIES + " VARCHAR," +
        KEY_SERVER_ROW_ID + " integer primary key" +
        ");"; 
    public static final String SERVERS_TABLE_INSERT =
    	"insert into " +
        SERVERS_TABLE +
        "(" +
        KEY_SERVER_HOST_NAME + "," +
        KEY_SERVER_INTERNET_ADDRESS + "," +
        KEY_SERVER_USER_NAME + "," +
        KEY_SERVER_USER_PASSWORD + "," +
        KEY_LOCAL_MOUNT_DIRECTORY + "," +
        KEY_SERVER_MOUNT_OPTIONS + "," +
        KEY_SERVER_EXPORT_DIRECTORIES + "," +
        KEY_SERVER_ROW_ID +
        ") values (?, ?, ?, ?, ?, ?, ?, ?);";
    protected static final String SERVERS_TABLE_DROP = "drop table if exists " + SERVERS_TABLE;
    
    public ServersDatabaseAdapter(Context context) {
    	this(context, DATABASE_NAME, true);
    }
    public ServersDatabaseAdapter(Context context, String databaseName, boolean initialize) {
        this.context = context;
        this.databaseName = databaseName;
        
        if (initialize) {
            databaseHelper = new DatabaseHelper(context, databaseName);
            serversListGet();
        }
    }

    // insert a server
    public void insert(GenericListItem item) {
    	AppState.logX(TAG, String.format("insert 1: server = %s", item.firstGet()));
        
    	Server server = (Server)item;
    	SQLiteDatabase database = databaseHelper.getWritableDatabase();
    	if (serversTableInsertStmt == null)
            serversTableInsertStmt = database.compileStatement(SERVERS_TABLE_INSERT);
        
    	insert(database, server);
    }
    
    // insert a server
    public static void insert(SQLiteDatabase database, GenericListItem item) {
    	Server server = (Server)item;
        SQLiteStatement serversTableInsertStmt = database.compileStatement(SERVERS_TABLE_INSERT);
        serversTableInsertStmt.bindString(KEY_SERVER_HOST_NAME_INDEX, server.serverHostNameGet());
        serversTableInsertStmt.bindString(KEY_SERVER_INTERNET_ADDRESS_INDEX, server.serverInternetAddressGet());
        serversTableInsertStmt.bindString(KEY_SERVER_USER_NAME_INDEX, server.serverUserNameGet());
        serversTableInsertStmt.bindString(KEY_SERVER_USER_PASSWORD_INDEX, server.serverUserPasswordGet());
        serversTableInsertStmt.bindString(KEY_SERVER_EXPORT_DIRECTORIES_INDEX, Server
        	.serverExportDirectoriesStringGet(server.serverExportDirectoriesGet()));
        serversTableInsertStmt.executeInsert();
        serversTableInsertStmt.close();
        
    	AppState.logX(TAG, String.format("insert 3: server = %s, inet addr = %s, exports = %s",
    		server.serverHostNameGet(), server.serverInternetAddressGet(), Server
    	    .serverExportDirectoriesStringGet(server.serverExportDirectoriesGet())));
    }
    
    // update a server
    public void update(GenericListItem item) {
        // update the database
        update(writeableDatabaseGet(), SERVERS_TABLE, item);
    }
    
    // update a server
    public static void update(SQLiteDatabase database, String table, GenericListItem item) {
        AppState.log(TAG, String.format("update 3: database = %s, table = %s, name = %s", database, table,
        	item.firstGet()));

        Server server = (Server)item;
        ContentValues values = new ContentValues();
    	values.put(KEY_SERVER_HOST_NAME, server.serverHostNameGet());
        values.put(KEY_SERVER_INTERNET_ADDRESS, server.serverInternetAddressGet());
        values.put(KEY_SERVER_USER_NAME, server.serverUserNameGet());
        values.put(KEY_SERVER_USER_PASSWORD, server.serverUserPasswordGet());
        values.put(KEY_SERVER_EXPORT_DIRECTORIES, Server.serverExportDirectoriesStringGet(server
        	.serverExportDirectoriesGet()));
        database.update(table, values, KEY_SERVER_HOST_NAME + "=" + server.serverHostNameGet(), null);
    }
    
    public int serversSize() {
        AppState.log(TAG, "serversSize");

    	SQLiteDatabase database = databaseHelper.getWritableDatabase();
    	Cursor cursor = database.query(SERVERS_TABLE, serversTableColumns, null, null, null, null, null);
        int size = 0;
        
    	if (cursor != null) {
    	    AppState.log(TAG, String.format("size: cursor count = %d", cursor.getCount()));

    	    size = cursor.getCount();
    	    cursor.close();
    	}

    	return size;
    }
    
    private Cursor serversSortedRawCursorGet() {
    	AppState.logX(TAG, String.format("serversSortedRawCursorGet: databaseHelper = %s", databaseHelper));
    		
    	return databaseHelper.getWritableDatabase().rawQuery("SELECT * FROM " + SERVERS_TABLE + " ORDER BY " +
    	    KEY_SERVER_HOST_NAME + " COLLATE NOCASE", null);
    }
    
    public Server serversGet(int index) {
        AppState.logX(TAG, "serversGet");

    	SQLiteDatabase database = databaseHelper.getWritableDatabase();
    	Cursor cursor = serversSortedRawCursorGet();
    	Server server = null;
    	if (cursor != null) {
        	AppState.logX(TAG, String.format("serversGet: cursor count = %d", cursor.getCount()));

        	if (cursor.moveToPosition(index))
        		server = serversGenerateFromCursor(cursor, database);

    	    cursor.close();
    	}
    	
    	return server;
    }
    public Server serversGetByInternetAddress(String internetAddress) {
        AppState.log(TAG, "serversGetByInternetAddress");

    	SQLiteDatabase database = databaseHelper.getWritableDatabase();
    	Cursor cursor = database.rawQuery("SELECT * FROM " + SERVERS_TABLE + " WHERE " +
            KEY_SERVER_INTERNET_ADDRESS + "='" + internetAddress + "' COLLATE NOCASE", null);

    	Server server = null;
    	if (cursor != null) {
    		AppState.log(TAG, String.format("serversGetByInternetAddress: cursor count = %d",
    			cursor.getCount()));

    		if (cursor.moveToFirst())
    			server = serversGenerateFromCursor(cursor, database);
    		
    	    cursor.close();
    	}
    	
    	return server;
    }
    public Server serversGetByHostName(String hostName) {
        AppState.log(TAG, "serversGetByHostName");

    	SQLiteDatabase database = databaseHelper.getWritableDatabase();
    	Cursor cursor = database.rawQuery("SELECT * FROM " + SERVERS_TABLE + " WHERE " + KEY_SERVER_HOST_NAME +
    		"='" + hostName + "' COLLATE NOCASE", null);

    	Server server = null;
    	if (cursor != null) {
    		AppState.log(TAG, String.format("serversGetByHostName: cursor count = %d", cursor.getCount()));

    		if (cursor.moveToFirst())
    			server = serversGenerateFromCursor(cursor, database);

    	    cursor.close();
    	}
    	
    	return server;
    }
    public static Server serversGenerateFromCursor(Context context, Cursor cursor, SQLiteDatabase database) {
    	AppState.logX(TAG, "serversGenerateFromCursor 1");
    			
        String hostName = cursor.getString(cursor.getColumnIndex(KEY_SERVER_HOST_NAME));
		String inetAddr = cursor.getString(cursor.getColumnIndex(KEY_SERVER_INTERNET_ADDRESS));
		String userName = cursor.getString(cursor.getColumnIndex(KEY_SERVER_USER_NAME));
		String userPassword = cursor.getString(cursor.getColumnIndex(KEY_SERVER_USER_PASSWORD));
        String exportsStr = cursor.getString(cursor.getColumnIndex(KEY_SERVER_EXPORT_DIRECTORIES));

        List<String> exportsList = new ArrayList<String>();
        for (String export : exportsStr.split(Server.EXPORT_DIRECTORIES_SEPARATOR)) 
        	exportsList.add(export);

		AppState.logX(TAG, String.format("serversGenerateFromCursor 1: host name = %s, inet addr = %s, " +
			"exports = %s", hostName, inetAddr, exportsStr));
	    	
		return new Server(hostName, inetAddr, userName, userPassword, exportsList);
    }
    
    private Server serversGenerateFromCursor(Cursor cursor, SQLiteDatabase database) {
    	AppState.logX(TAG, "serversGenerateFromCursor 2");

        Server server = serversGenerateFromCursor(context, cursor, database);
    	
		return server;
    }
   
    // delete a server from the servers table
    public static String deletionWhereClause(GenericListItem item) {
    	return KEY_SERVER_INTERNET_ADDRESS + "=" + item.firstGet();
    }
    public void serversTableDelete(Server server) {
        AppState.log(TAG, "serversTableDelete");
        
    	SQLiteDatabase database = databaseHelper.getWritableDatabase();
        database.beginTransaction();
        try {
            database.delete(SERVERS_TABLE, deletionWhereClause(server), null);
            database.setTransactionSuccessful();
        } catch (SQLException exception) {
        	AppState.log(TAG, "serversTableDelete: SQLException for the servers table");
        } finally {
        	database.endTransaction();
        }
    }
    
    public List<GenericListItem> serversListGet() {
    	AppState.logX(TAG, String.format("serversListGet: databaseHelper = %s", databaseHelper));
    	
    	SQLiteDatabase database = databaseHelper.getWritableDatabase();
    	Cursor serversCursor = serversSortedRawCursorGet();
    	List<GenericListItem> list = Collections.synchronizedList(new ArrayList<GenericListItem>());
    	if (serversCursor != null) {
    		AppState.logX(TAG, String.format("serversListGet: database = %s, serversCursor count = %d",
    			database, serversCursor.getCount()));

    		if (serversCursor.moveToFirst()) {
    			do {
        			list.add(serversGenerateFromCursor(serversCursor, database));
    			} while (serversCursor.moveToNext());
    		}
    		serversCursor.close();
    	}

    	return list;
    }
    
    //XXX start database
    public SQLiteDatabase writeableDatabaseGet() {
    	return databaseHelper.getWritableDatabase();
    }
    
    public boolean tableExists(String tableName) {
    	SQLiteDatabase database = databaseHelper.getReadableDatabase();
        boolean exists = false;
        
        Cursor cursor = database.rawQuery("SELECT DISTINCT TBL_NAME FROM SQLITE_MASTER WHERE TBL_NAME = '" +
            tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0)
                exists = true;
            cursor.close();
        }
        
        return exists;
    }
    
    public void close() {
        AppState.log(TAG, "close");
        databaseHelper.getWritableDatabase().close();
        databaseHelper.close();
    }
    
    public void databaseClose() {
    	AppState.log(TAG, "close");
    }

    public void databaseDelete() {
    	AppState.log(TAG, "delete");

    	context.deleteDatabase(DATABASE_NAME);
    }
    
    // check if the database already exists
    public boolean databaseExists() {
     	SQLiteDatabase tmp = null;
     	boolean status = false;

     	try {
    	    if ((tmp = SQLiteDatabase.openDatabase(DATABASE_NAME, null, SQLiteDatabase.OPEN_READONLY))
    	    	!= null) {
     		    status = true;
     		    tmp.close();
     		    AppState.log(TAG, "databaseExists: true");
    	    }
     	} catch (SQLiteException e) {
     		AppState.log(TAG, "databaseExists: false");
     	}

    	return status;
    }
    
    protected static class DatabaseHelper extends SQLiteOpenHelper {
    	public static final String TAG = ServersDatabaseAdapter.TAG + ":DatabaseHelper";

    	public DatabaseHelper(Context context) {
    		this(context, DATABASE_NAME);
    	}

    	public DatabaseHelper(Context context, String filename) {
    	    super(context, filename, null, DATABASE_VERSION);	
    	}
    	
		@Override
		public void onCreate(SQLiteDatabase database) {
	        AppState.log(TAG, "onCreate");
	        
	        database.execSQL(SERVERS_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			AppState.log(TAG, "onUpgrade");
		
			if (newVersion > oldVersion) {
				db.beginTransaction();

				boolean success = true;
				for (int i = oldVersion; i < newVersion; i++) {					
					switch (i + 1) {
					case 2: // upgrade to version 2, set success
						break;
					}

					if (!success)
						break;
				}

				if (success)
					db.setTransactionSuccessful();
			
				db.endTransaction();
			}
			else {
				//clearDatabase(db);
				onCreate(db);
			}
		}
    }
}