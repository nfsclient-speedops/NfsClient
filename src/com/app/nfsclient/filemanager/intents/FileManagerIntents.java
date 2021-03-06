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
/* 
 * Copyright (C) 2008 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.nfsclient.filemanager.intents;

// Version Dec 9, 2008


/**
 * Provides OpenIntents actions, extras, and categories used by providers. 
 * <p>These specifiers extend the standard Android specifiers.</p>
 */
public final class FileManagerIntents {

	/**
	 * Activity Action: Pick a file through the file manager, or let user
	 * specify a custom file name.
	 * Data is the current file name or file name suggestion.
	 * Returns a new file name as file URI in data.
	 * 
	 * <p>Constant Value: "org.openintents.action.PICK_FILE"</p>
	 */
	public static final String ACTION_PICK_FILE = "org.openintents.action.PICK_FILE";
    public static final String ACTION_PICK_FILE_DROPBOX = "org.openintents.action.PICK_FILE_DROPBOX";
    
	/**
	 * Activity Action: Pick a directory through the file manager, or let user
	 * specify a custom file name.
	 * Data is the current directory name or directory name suggestion.
	 * Returns a new directory name as file URI in data.
	 * 
	 * <p>Constant Value: "org.openintents.action.PICK_DIRECTORY"</p>
	 */
	public static final String ACTION_PICK_DIRECTORY = "org.openintents.action.PICK_DIRECTORY";
	
	/**
	 * Activity Action: Move, copy or delete after select entries.
     * Data is the current directory name or directory name suggestion.
     * 
     * <p>Constant Value: "org.openintents.action.MULTI_SELECT"</p>
	 */
	public static final String ACTION_MULTI_SELECT = "org.openintents.action.MULTI_SELECT";

	/**
	 * The title to display.
	 * 
	 * <p>This is shown in the title bar of the file manager.</p>
	 * 
	 * <p>Constant Value: "org.openintents.extra.TITLE"</p>
	 */
	public static final String EXTRA_TITLE = "org.openintents.extra.TITLE";

	/**
	 * The text on the button to display.
	 * 
	 * <p>Depending on the use, it makes sense to set this to "Open" or "Save".</p>
	 * 
	 * <p>Constant Value: "org.openintents.extra.BUTTON_TEXT"</p>
	 */
	public static final String EXTRA_BUTTON_TEXT = "org.openintents.extra.BUTTON_TEXT";

	/**
	 * Flag indicating to show only writeable files and folders.
     *
	 * <p>Constant Value: "org.openintents.extra.WRITEABLE_ONLY"</p>
	 */
	public static final String EXTRA_WRITEABLE_ONLY = "org.openintents.extra.WRITEABLE_ONLY";
	
	/**
	 * Flags indicating to show the file format options.
     *
	 * <p>Constant Value: "org.openintents.extra.EXPORT"</p>
	 */
	public static final String EXTRA_FILE_FORMAT = "org.openintents.extra.FILE_FORMAT";
	
	/**
	 * Flag to pass the file system type.
     *
	 * <p>Constant Value: "org.openintents.extra.EXTRA_FILE_SYSTEM_TYPE"</p>
	 */
	public static final String EXTRA_FILE_SYSTEM_TYPE = "org.openintents.extra.FILE_SYSTEM_TYPE";
	
	/**
	 * Flag to pass the file system id.
     *
	 * <p>Constant Value: "org.openintents.extra.EXTRA_FILE_SYSTEM_ID"</p>
	 */
	public static final String EXTRA_FILE_SYSTEM_ID = "org.openintents.extra.FILE_SYSTEM_ID";
	
}