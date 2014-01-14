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

package com.app.nfsclient.filemanager.distribution;

import com.app.nfsclient.filemanager.util.IntentUtils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * @author Peli
 * @author Karl Ostmo
 */
public class MarketUtils {
    
	/**
	 * URI prefix to a package name to bring up the download page on the Android Market
	 */
    public static final String MARKET_PACKAGE_DETAILS_PREFIX = "market://details?id=";
    

	public static boolean isMarketAvailable(Context context, String packageName) {
		return IntentUtils.isIntentAvailable(context, getMarketDownloadIntent(packageName));
	}
	

    public static Intent getMarketDownloadIntent(String packageName) {
        Uri marketUri = Uri.parse(MARKET_PACKAGE_DETAILS_PREFIX + packageName);
        return new Intent(Intent.ACTION_VIEW, marketUri);
    }
}