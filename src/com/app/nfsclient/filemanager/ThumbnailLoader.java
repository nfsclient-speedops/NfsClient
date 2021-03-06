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
 * Copyright (C) 2007-2011 OpenIntents.org
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

package com.app.nfsclient.filemanager;

import java.util.List;

import com.app.nfsclient.filemanager.compatibility.BitmapDrawable_Compatible;
import com.app.nfsclient.filemanager.util.FileUtils;
import com.app.nfsclient.filemanager.util.MimeTypes;
import com.app.nfsclient.generic.GenericFileInterface;
import com.app.nfsclient.generic.GenericFileSystem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;

public class ThumbnailLoader extends Thread {

	private static final String TAG = "OIFM_ThumbnailLoader";
	
	GenericFileSystem fileSystem;
    List<IconifiedText> listFile;
    public boolean cancel;
    GenericFileInterface file;
    Handler handler;
    Context context;

	private MimeTypes mMimeTypes;
	
    private static int thumbnailWidth = 32;
    private static int thumbnailHeight = 32;
    
	
	public ThumbnailLoader(GenericFileSystem fileSystem, GenericFileInterface file, List<IconifiedText> list, Handler handler, Context context, MimeTypes mimetypes) {
		super("Thumbnail Loader");
		
		this.fileSystem = fileSystem;
		listFile = list;
		this.file = file;
		this.handler = handler;
		this.context = context;
		this.mMimeTypes = mimetypes;
	}
	
	public static void setThumbnailHeight(int height) {
		thumbnailHeight = height;
		thumbnailWidth = height * 4 / 3;
	}
	
	public void run() {
		int count = listFile.size();
		
		Log.v(TAG, "Scanning for thumbnails (files=" + count + ")");
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		
		options.inSampleSize = 16;
		
		for (int x=0; x<count; x++) {
			if (cancel) {
				Log.v(TAG, "Thumbnail loader canceled");
				listFile = null;
				return;
			}
			IconifiedText text = listFile.get(x);
			String fileName = text.getText();
			try {
				options.inJustDecodeBounds = true;
				options.outWidth = 0;
				options.outHeight = 0;
				options.inSampleSize = 1;
				
				if ("video/mpeg".equals(mMimeTypes.getMimeType(fileName ))){
					// don't try to decode mpegs.
					continue;
				}
				
				BitmapFactory.decodeFile(FileUtils.getFile(fileSystem, file, fileName).getPath(), options);
				
				if (options.outWidth > 0 && options.outHeight > 0) {
					// Now see how much we need to scale it down.
					int widthFactor = (options.outWidth + thumbnailWidth - 1) / thumbnailWidth;
					int heightFactor = (options.outHeight + thumbnailHeight - 1) / thumbnailHeight;
					
					widthFactor = Math.max(widthFactor, heightFactor);
					widthFactor = Math.max(widthFactor, 1);
					
					// Now turn it into a power of two.
					if (widthFactor > 1) {
						if ((widthFactor & (widthFactor-1)) != 0) {
							while ((widthFactor & (widthFactor-1)) != 0) {
								widthFactor &= widthFactor-1;
							}
							
							widthFactor <<= 1;
						}
					}
					options.inSampleSize = widthFactor;
					options.inJustDecodeBounds = false;
					
					Bitmap bitmap = BitmapFactory.decodeFile(FileUtils.getFile(fileSystem, file, fileName).getPath(), options);
				
					if (bitmap != null) {
//						Log.v(TAG, "Got thumbnail for " + text.getText());
						
						// SDK 1.6 and higher only:
						// BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
						
						BitmapDrawable drawable = BitmapDrawable_Compatible.getNewBitmapDrawable(context.getResources(), bitmap);
						
						drawable.setGravity(Gravity.CENTER);
						drawable.setBounds(0, 0, thumbnailWidth, thumbnailHeight);
						
						text.setIcon(drawable);
						
						Message msg = handler.obtainMessage(FileManagerActivity.MESSAGE_ICON_CHANGED);
						msg.obj = text;
						msg.sendToTarget();
					}
				}
			} catch (Exception e) {
				// That's okay, guess it just wasn't a bitmap.
			}
		}
		
		Log.v(TAG, "Done scanning for thumbnails");
		listFile = null;
	}
}