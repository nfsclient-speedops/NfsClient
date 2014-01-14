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

import android.database.sqlite.SQLiteDatabase;

public interface GenericDatabaseAdapterInterface {
    public static final String ROW_DELETION_WHERE_CLAUSE = "_id=";

    public static final String TO_TABLE = "toTable";
    public static final String FROM_TABLE = "fromTable";
    public static final String TABLE_COPY = "CREATE TABLE " + TO_TABLE + " AS SELECT * FROM " + FROM_TABLE;
    
    public static final String ATTACHED_DATABASE_FILE_PATH = "attachedDbFilePath";
    public static final String ATTACHED_DATABASE_REFERENCE = "attachedDbReference";
    public static final String DATABASE_ATTACH = "ATTACH DATABASE \"" + ATTACHED_DATABASE_FILE_PATH +
    	"\" AS " + ATTACHED_DATABASE_REFERENCE;
    public static final String DATABASE_DETACH = "DETACH DATABASE " + ATTACHED_DATABASE_REFERENCE;
    
    // database operations
    public SQLiteDatabase writeableDatabaseGet();
    public void databaseClose();
    public void databaseDelete();
    public boolean databaseExists();
}