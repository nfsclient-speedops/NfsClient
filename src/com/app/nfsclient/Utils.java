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

package com.app.nfsclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Random;

import org.xmlpull.v1.XmlPullParserException;

import com.app.nfsclient.filemanager.util.MimeTypeParser;
import com.app.nfsclient.filemanager.util.MimeTypes;
import com.app.nfsclient.generic.GenericAlertDialog;
import com.app.nfsclient.generic.GenericAsyncTask;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil2;
import eu.medsea.mimeutil.detector.MagicMimeMimeDetector;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;

public abstract class Utils {
	private static final String TAG = "Utils";
	     
    public static final int BUTTONS_DIVIDER_HEIGHT = 1;

    public static final String EMPTY_STRING = "";
	public static final String CR_LF = "\n";
    public static final char DOT_CHAR = '.';
    public static final char SPACE_CHAR = ' ';
    public static final String SPACE_STRING = " ";
    public static final String JAVASCRIPT_MAGIC_PREFIX = "MAGIC";
    public static final String NON_ALPHANUMBERIC_REGEX = "[\\p{Punct}\\s]+";
	public static final String WORD_SPLITTER_REGEX = "\\W+";
	public static final String ALPHANUMERIC_FILTER = "[a-zA-z0-9]*";
	public static final String ALPHA_FILTER = "[a-zA-z]*";
	public static final String NUMERIC_FILTER = "[0-9]*";

    public static final int BUFFERED_READER_SIZE = 1024;
    
    public static float emptyListTextSize;
    public static float populatedListTextSize;
    public static float optionsMenuListTextSize;
    public static void dimensionsInit(Context context) {
    	emptyListTextSize = context.getResources().getDimension(R.dimen.list_title_text_size_empty);
    	populatedListTextSize = context.getResources().getDimension(R.dimen.list_title_text_size);
    	optionsMenuListTextSize = context.getResources().getDimension(R.dimen.list_title_text_size);
    	
    	AppState.logZ(TAG, String.format("viewsInit: titleViewEmptyListTextSize = %f," +
        	"titleViewPluralsTextSize = %f", emptyListTextSize, populatedListTextSize));
    }
    public static float emptyListTextSizeGet() {
    	return emptyListTextSize;
    }
    public static float populatedListTextSizeGet() {
    	return populatedListTextSize;
    }
    public static float optionsMenuListTextSizeGet() {
    	return optionsMenuListTextSize;
    }
    
    private static final Random randomGenerator = new Random();
    public static int randomIntGet() {
		int randomInt = randomIntGet(Integer.MAX_VALUE - 1);
		
		return randomInt == 0 ? 1 : randomInt;	
	}
    public static int randomIntGet(int maxInt) {
    	randomGenerator.setSeed(System.currentTimeMillis());
		
    	return randomGenerator.nextInt(maxInt);
    }
    public static long randomLongGet() {
    	randomGenerator.setSeed(System.currentTimeMillis());
    	long randomLong = randomGenerator.nextLong();
    	
    	return randomLong == 0 ? 1 : randomLong;
    }
	public static String randomBytesStringGet(int length) {		
		byte[] hexBuffer = new byte[length];
		byte[] base64Buffer;
		
		randomGenerator.nextBytes(hexBuffer);
		base64Buffer = Utils.toBase64(hexBuffer, 0, hexBuffer.length);
		
		return new String(base64Buffer);
	}

	public static void defaultSqlCacheSizeSet(final int n) {
        try {
            Field field = SQLiteDatabase.class.getDeclaredField("DEFAULT_SQL_CACHE_SIZE");
            field.setAccessible(true);
            field.set(SQLiteDatabase.class, n);
        } catch (Exception e) {
            AppState.log(TAG, "defaultSqlCacheSizeSet Exception: " + e);
        }        
    }
	
	public static GenericAlertDialog alertDialogCreate(Context context, String title, String message,
		String buttonLabel, View.OnClickListener listener) {
		final GenericAlertDialog dialog = new GenericAlertDialog(context);
		dialog
    	    .cancelableSet(true)
		    .titleSet(title)
		    .messageSet(message)
		    .neutralButtonSet(TextUtils.isEmpty(buttonLabel) ? context.getString(
		    	R.string.genericCloseButtonLabel) : buttonLabel, listener);

		if (listener == null)
			dialog.neutralButtonSet(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
                    dialog.dismiss();
				}
			});
		
		return dialog;
	}
	public static GenericAlertDialog alertDialogShow(Context context, String title, String message,
		String buttonLabel, View.OnClickListener listener) {
		GenericAlertDialog dialog = alertDialogCreate(context, title, message, buttonLabel, listener);
		dialog.show();

		return dialog;
	}
	public static GenericAlertDialog alertDialogShow(Context context, String title, String message,
		String buttonLabel) {
		return alertDialogShow(context, title, message, buttonLabel, null);
	}
	public static GenericAlertDialog alertDialogShow(Context context, String title, String message) {
		return alertDialogShow(context, title, message, context.getString(R.string.genericCloseButtonLabel),
			null);
	}

	public static byte[] stringToBytes(String str, String encoding) {
		byte[] array = null;

		if (str != null) {
			try {
				array = str.getBytes(encoding);
			} catch(java.io.UnsupportedEncodingException e) {
				array = str.getBytes();
			}
		}
		
		return array;
	}
	public static byte[] stringToBytes(String str) {
		return stringToBytes(str, "UTF-8");
	}
	public static String bytesToString(byte[] bytes, String encoding) {
		String str = null;

		if (bytes != null) {
			try {
				str = new String(bytes, encoding);
			} catch(java.io.UnsupportedEncodingException e) {
				str = new String(bytes);
			}
		}
		
		return str;
	}
	public static String bytesToString(byte[] bytes) {
		return bytesToString(bytes, "UTF-8");
	}
	
	private static final byte[] b64 =
		stringToBytes("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=");

	public static byte[] toBase64(byte[] buf, int start, int length) {
		int tmpSize = length * 2;
		byte[] tmp = new byte[tmpSize];
		int i, j, k;
		int foo = (length / 3) * 3 + start;
		
		for (j = start, i = 0; j < foo; j += 3) {
			k = (buf[j] >>> 2) & 0x3f;
			tmp[i++] = b64[k];
			k = (buf[j] & 0x03) << 4 | (buf[j + 1] >>> 4) & 0x0f;
			tmp[i++] = b64[k];
			k = (buf[j + 1] & 0x0f) << 2 | (buf[j + 2] >>> 6) & 0x03;
			tmp[i++] = b64[k];
			k = buf[j + 2] & 0x3f;
			tmp[i++] = b64[k];
		}

		foo = (start + length) - foo;
		if (foo == 1) {
			k = (buf[j] >>> 2) & 0x3f;
			tmp[i++] = b64[k];
			k = ((buf[j] & 0x03) << 4) & 0x3f;
			tmp[i++] = b64[k];
			
			if (i < tmpSize) {
			    tmp[i++] = (byte)'=';
			
			    if (i < tmpSize)
			        tmp[i++] = (byte)'=';
			}
		}
		else if (foo == 2) {
			k = (buf[j] >>> 2) & 0x3f;
			tmp[i++] = b64[k];
			k = (buf[j] & 0x03) << 4 | (buf[j + 1] >>> 4) & 0x0f;
			tmp[i++] = b64[k];
			k = ((buf[j + 1] & 0x0f) << 2) & 0x3f;
			tmp[i++] = b64[k];
			tmp[i++] = (byte)'=';
		}
		
		byte[] bar = new byte[i];
		System.arraycopy(tmp, 0, bar, 0, i);
		
		return bar;
	}
	private static byte val(byte foo) {
		if (foo == '=')
			return 0;

		for (int j = 0; j < b64.length; j++) {
			if (foo == b64[j])
				return (byte)j;
		}

		return 0;
	}

	public static byte[] fromBase64(byte[] buf, int start, int length) {
		byte[] foo = new byte[length];
		int j = 0;
		
		for (int i = start; i < start + length; i += 4) {
			foo[j] = (byte)((val(buf[i]) << 2) | ((val(buf[i + 1]) & 0x30) >>> 4));
			
			if (buf[i + 2] == (byte)'=') {
				j++;
				break;
			}
						
			foo[j + 1] = (byte)(((val(buf[i + 1]) &0x0f) << 4) | ((val(buf[i + 2]) & 0x3c) >>> 2));
			if (buf[i + 3] == (byte)'=') {
				j += 2;
				break;
			}

			foo[j + 2] = (byte)(((val(buf[i + 2]) & 0x03) << 6 ) | (val(buf[i + 3]) & 0x3f));
			j += 3;
		}
		
		byte[] bar = new byte[j];
	    System.arraycopy(foo, 0, bar, 0, j);
	    
	    return bar;
	}
	
	private static char[] lowercases = {
        '\000','\001','\002','\003','\004','\005','\006','\007',
        '\010','\011','\012','\013','\014','\015','\016','\017',
        '\020','\021','\022','\023','\024','\025','\026','\027',
        '\030','\031','\032','\033','\034','\035','\036','\037',
        '\040','\041','\042','\043','\044','\045','\046','\047',
        '\050','\051','\052','\053','\054','\055','\056','\057',
        '\060','\061','\062','\063','\064','\065','\066','\067',
        '\070','\071','\072','\073','\074','\075','\076','\077',
        '\100','\141','\142','\143','\144','\145','\146','\147',
        '\150','\151','\152','\153','\154','\155','\156','\157',
        '\160','\161','\162','\163','\164','\165','\166','\167',
        '\170','\171','\172','\133','\134','\135','\136','\137',
        '\140','\141','\142','\143','\144','\145','\146','\147',
        '\150','\151','\152','\153','\154','\155','\156','\157',
        '\160','\161','\162','\163','\164','\165','\166','\167',
        '\170','\171','\172','\173','\174','\175','\176','\177'
    };
	public static boolean endsWithIgnoreCase(String s,String w) {
		if (w == null)
			return true;

		if (s == null)
			return false;

		int sl = s.length();
		int wl = w.length();

		if (sl < wl)
			return false;

		for (int i = wl; i-- > 0; ) {
			char c1 = s.charAt(--sl);
			char c2 = w.charAt(i);
			if (c1 != c2) {
				if (c1 <= 127)
					c1 = lowercases[c1];
				if (c2 <= 127)
					c2 = lowercases[c2];
				if (c1 != c2)
					return false;
			}
		}
		
		return true;
	}
	
	public static boolean isEmpty(byte[] byteArray) {
		return(byteArray == null || byteArray.length == 0);
	}
	
	public static void stackTracePrint() {
	    StackTraceElement[] stack = Thread.currentThread().getStackTrace();
	    StringBuffer buffer = new StringBuffer("Stack\n");
	    
	    for (StackTraceElement element : stack) {
	        buffer.append(String.format("%s(%s %s)\n", element.getMethodName(),
	        	element.getFileName(), element.getLineNumber()));
	        
	        if (element.getMethodName().equals("stackTracePrint"))
	        	break;
	    }
	    
	    AppState.log(TAG, buffer.toString());
	}
	
	public static int cacheFolderClear(final File dir, final int numDays) {
	    int deletedFiles = 0;
	    
	    if (dir != null && dir.isDirectory()) {
	        try {
	            for (File child : dir.listFiles()) {
	                // first delete subdirectories recursively
	                if (child.isDirectory())
	                    deletedFiles += cacheFolderClear(child, numDays);

	                // then delete the files and subdirectories in this dir
	                // only empty directories can be deleted, so subdirs have been done first
	                if (child.lastModified() < new Date().getTime() - numDays *
	                	DateUtils.DAY_IN_MILLIS) {
	                    if (child.delete())
	                        deletedFiles++;
	                }
	            }
	        } catch(Exception e) {
	            AppState.log(TAG, String.format("cacheFolderClear: Failed to clean the cache, " +
	            	"error %s", e.getMessage()));
	        }
	    }
	    
	    return deletedFiles;
	}
	
	public static int directoryFilesClear(final File dir) {
	    int deletedFiles = 0;

		if (dir != null && dir.isDirectory()) {
	        try {
	            for (File child : dir.listFiles()) {
	                // first delete the contents of the subdirectories recursively
	                if (child.isDirectory())
	                    deletedFiles += directoryFilesClear(child);
	                else if (child.delete()) // then delete the files in this directory
	                    deletedFiles++;
	            }
	        } catch(SecurityException e) {
	            AppState.log(TAG, "directoryFilesClear: SecurityException");
	        }
	    }
		
		return deletedFiles;
	}
	
    public static class FilesDeletionAsyncTask extends GenericAsyncTask<File, Void, Void> {
		private static final String TAG = Utils.TAG + ":FilesDeletionAsyncTask";
		
		@Override
		protected Void doInBackground(File... params) {
			AppState.log(TAG, "doInBackground");
			
			// delete every file in this directory and its subdirectories
			directoryFilesClear(params[0]);
			
			return null;
		}
	}
    
    public static void copyFile(File src, File dst) throws IOException {
    	FileInputStream fis = new FileInputStream(src);
    	FileChannel inChannel = fis.getChannel();
    	FileOutputStream fos = new FileOutputStream(dst);
    	FileChannel outChannel = fos.getChannel();
    	try {
    		inChannel.transferTo(0, inChannel.size(), outChannel);
    	} finally {
    		fis.close();
    		if (inChannel != null)
    			inChannel.close();
    		
    		fos.close();
    		if (outChannel != null)
    			outChannel.close();
    	}
    }
    
    public static MimeTypes mimeTypesGet(Context context) {
        MimeTypeParser mtp = new MimeTypeParser();
		XmlResourceParser in = context.getResources().getXml(R.xml.mimetypes);

		MimeTypes mimeTypes = null;
		try {
		    mimeTypes = mtp.fromXmlResource(in);
		} catch (XmlPullParserException e) {
			AppState.logX(TAG, "mimeTypesGet: XmlPullParserException");
			throw new RuntimeException("mimeTypesGet: XmlPullParserException");
		} catch (IOException e) {
		    AppState.logX(TAG, "mimeTypesGet: IOException");
			throw new RuntimeException("mimeTypesGet: IOException");
	    }
		
		return mimeTypes;
    }
    
    public static String mimeTypeGet(Context context, String filename) {
		return mimeTypesGet(context).getMimeType(filename);
    }
    
	private static final MimeType UNKNOWN_MIME_TYPE = new MimeType("application/octet-stream");
	public static String mimeTypeGet2(Context context, String filename) {
		AppState.logX(TAG, String.format("mimeTypeGet2: filename = %s", filename));

		MimeType mimeType = UNKNOWN_MIME_TYPE;
		AppState.logX(TAG, "mimeTypeGet2: 1");
		File file = new File(filename);
		AppState.logX(TAG, "mimeTypeGet2: 2");
		if (file.isDirectory()) {
			AppState.logX(TAG, "mimeTypeGet2: 3");
			mimeType = MimeUtil2.DIRECTORY_MIME_TYPE;
			AppState.logX(TAG, "mimeTypeGet2: 4");
		} else {
			AppState.logX(TAG, "mimeTypeGet2: 5");
			MagicMimeMimeDetector detector = new MagicMimeMimeDetector(context);
			AppState.logX(TAG, "mimeTypeGet2: 6");
			Collection<MimeType> mimeTypes = new ArrayList<MimeType>();
			AppState.logX(TAG, "mimeTypeGet2: 7");
			mimeTypes.addAll(detector.getMimeTypesFileName(filename));
			AppState.logX(TAG, String.format("mimeTypeGet2: 8: mimeType.size = %d", mimeTypes.size()));
			mimeTypes.remove(UNKNOWN_MIME_TYPE);
			AppState.logX(TAG, "mimeTypeGet2: 9");
			mimeType = MimeUtil2.getMostSpecificMimeType(mimeTypes);
			AppState.logX(TAG, "mimeTypeGet2: 10");
		}

		AppState.logX(TAG, String.format("mimeTypeGet2: mimeType = %s", mimeType != null ? mimeType.toString() :
			"null"));

		return mimeType != null ? mimeType.toString() : context.getString(R.string.mime_file_text);
	}
	
	/**
	 * Ensures that an object reference passed as a parameter to the calling
	 * method is not null.
	 *
	 * @param reference an object reference
	 * @return the non-null reference that was validated
	 * @throws NullPointerException if {@code reference} is null
	 */
	public static <T> T checkNotNull(T reference) {
		if (reference == null) {
			throw new NullPointerException();
		}
		return reference;
	}
	  
	/**
	 * Returns a string, of length at least {@code minLength}, consisting of
	 * {@code string} appended with as many copies of {@code padChar} as are
	 * necessary to reach that length. For example,
	 *
	 * <ul>
	 * <li>{@code padEnd("4.", 5, '0')} returns {@code "4.000"}
	 * <li>{@code padEnd("2010", 3, '!')} returns {@code "2010"}
	 * </ul>
	 *
	 * <p>See {@link Formatter} for a richer set of formatting capabilities.
	 *
	 * @param string the string which should appear at the beginning of the result
	 * @param minLength the minimum length the resulting string must have. Can be
	 *     zero or negative, in which case the input string is always returned.
	 * @param padChar the character to append to the end of the result until the
	 *     minimum length is reached
	 * @return the padded string
	 */
	public static String padEnd(String string, int minLength, char padChar) {
		checkNotNull(string);  // eager for GWT.
		if (string.length() >= minLength) {
			return string;
		}
		StringBuilder sb = new StringBuilder(minLength);
		sb.append(string);
		for (int i = string.length(); i < minLength; i++) {
			sb.append(padChar);
		}
		
		return sb.toString();
	}
}