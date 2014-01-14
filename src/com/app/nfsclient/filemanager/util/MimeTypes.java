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

package com.app.nfsclient.filemanager.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.app.nfsclient.AppState;
import com.app.nfsclient.R;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.webkit.MimeTypeMap;

public class MimeTypes {
    private static final String TAG = "MimeTypes";

	private Map<String, String> mMimeTypes;

	public MimeTypes() {
		mMimeTypes = new HashMap<String,String>();
	}
	
	public void put(String type, String extension) {
		// Convert extensions to lower case letters for easier comparison
		extension = extension.toLowerCase();
		
		mMimeTypes.put(type, extension);
	}
	
	public String getMimeType(String filename) {
		
		String extension = FileUtils.getExtension(filename);
		
		// Let's check the official map first. Webkit has a nice extension-to-MIME map.
		// Be sure to remove the first character from the extension, which is the "." character.
		if (extension.length() > 0) {
			String webkitMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));
		
			if (webkitMimeType != null) {
				// Found one. Let's take it!
				return webkitMimeType;
			}
		}
		
		// Convert extensions to lower case letters for easier comparison
		extension = extension.toLowerCase();
		
		String mimetype = mMimeTypes.get(extension);
		
		if(mimetype==null) mimetype = "*/*";
		
		return mimetype;
	}
	
	public static MimeTypes getMimeTypes(Context context) {
        MimeTypeParser mtp = new MimeTypeParser();
		XmlResourceParser in = context.getResources().getXml(R.xml.mimetypes);
        MimeTypes mimeTypes = null;
        
		try {
		    mimeTypes = mtp.fromXmlResource(in);
		} catch (XmlPullParserException e) {
			AppState.log(TAG, "PreselectedChannelsActivity: XmlPullParserException");
			throw new RuntimeException("PreselectedChannelsActivity: XmlPullParserException");
		} catch (IOException e) {
			AppState.log(TAG, "PreselectedChannelsActivity: IOException");
			throw new RuntimeException("PreselectedChannelsActivity: IOException");
	    }
		
		return mimeTypes;
	}
}