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

import java.util.List;

import android.database.Cursor;

public interface GenericListItemsDatabaseAdapterInterface {
    public static final String GENERIC_KEY_ROW_ID = "_id";

    // get the size from the items table
    public int size();
    
    // insert an item
    public void insert(GenericListItem item);
    public void insert(List<GenericListItem> list);
    
    // update an item
    public void update(GenericListItem item);
 
    // get items
    public List<GenericListItem> listGet();
    public GenericListItem get(int index);
    public GenericListItem getByUserId(String userId);
    public GenericListItem getByName(String name);

    // generate item
    public GenericListItem generateItemFromCursor(Cursor cursor);

    // get the index of an item
    public int indexOf(GenericListItem item);
    public int indexOfByUserId(String userId);
    
    // delete an item
    public String deletionWhereClause(GenericListItem item);
    public int delete(GenericListItem item);
    public int deleteAll();
    
    // database operations
    public void close();
    public boolean delete();
}