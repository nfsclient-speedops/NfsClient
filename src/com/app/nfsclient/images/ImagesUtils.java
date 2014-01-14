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

package com.app.nfsclient.images;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.app.nfsclient.AppState;
import com.app.nfsclient.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;

public class ImagesUtils {
    private static final String TAG = "ImagesUtils";
    
	private static Matrix defaultImageMatrix = null;

	private static Bitmap transparentDefaultBitmapImage = null;
	private static Drawable transparentDefaultDrawableImage = null;
    
	private static Bitmap itemDefaultBitmapImage = null;
	private static Drawable itemDefaultDrawableImage = null;
	private static String itemDefaultImageUriString = null;
	private static Uri itemDefaultImageUri = null;

	private static Bitmap dropboxDefaultBitmapImage = null;
	private static Drawable dropboxDefaultDrawableImage = null;
	private static String dropboxDefaultImageUriString = null;
	private static Uri dropboxDefaultImageUri = null;
	
	private static final float IMAGE_SCALE_X = (float)1.0;
	private static final float IMAGE_SCALE_Y = (float)1.0;
	
    public static final String EXT_JPEG = "jpeg";
    public static final String EXT_JPG = "jpg";
    public static final String EXT_PNG = "png";
    public static final String EXT_TEXT = "txt";

    public static final String REGEX_EXT_JPEG = "(?i).*" + EXT_JPEG;
    public static final String REGEX_EXT_JPG = "(?i).*" + EXT_JPG;
    public static final String REGEX_EXT_TEXT = "(?i).*" + EXT_TEXT;
    public static final String REGEX_EXT_PNG = "(?i).*" + EXT_PNG;
    
    public static final int JPG_COMPRESSION_QUALITY = 100;
    public static final int PNG_COMPRESSION_QUALITY = 100;

    public static final int DEFAULT_BITMAP_SAMPLE_SIZE = 2;
    
    public static final String CAMERA_IMAGE_BUCKET_NAME =
        Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera";
    public static final String CAMERA_IMAGE_BUCKET_ID = bucketIdGet(CAMERA_IMAGE_BUCKET_NAME);

    public static final String IMAGES_GROUP_NAME_KEY = AppState.APP_PACKAGE_NAME + "." + TAG +
        ".imagesGroupNameKey";
    public static final String IMAGE_LOCATION_KEY = AppState.APP_PACKAGE_NAME + "." + TAG +
     	".imageUriLocationKey";
    public static final String IMAGE_TYPE_KEY = AppState.APP_PACKAGE_NAME + "." + TAG + ".imageTypeKey";
    public static final String IMAGE_SELECTION_POSITION_KEY = AppState.APP_PACKAGE_NAME + "." + TAG +
        ".imageSelectionPositionKey";
    public static final String IMAGES_PAGE_NUMBER_KEY = AppState.APP_PACKAGE_NAME + "." + TAG +
      	".imagesPageNumberKey";
   	public static final int IMAGES_PER_PAGE = 10;
    	
    public static String bucketIdGet(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }

	public static void dropboxDefaultImagesInit(Context context) {
		if (dropboxDefaultBitmapImage == null) {
			// create the dropbox default bitmap image
			Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.dropbox_default_image);

  			dropboxDefaultBitmapImage = bitmapScale(imageBitmap, itemDefaultBitmapImageGet(context), 1f);

			dropboxDefaultImageUriString = context.getString(R.drawable.dropbox_default_image);
			dropboxDefaultImageUri = Uri.parse(dropboxDefaultImageUriString);
		}
		
		// create the dropbox default drawable image
		if (dropboxDefaultDrawableImage == null)
			dropboxDefaultDrawableImage = new BitmapDrawable(context.getResources(), dropboxDefaultBitmapImage);
	}
	public static Bitmap dropboxDefaultBitmapImageGet(Context context) {
		dropboxDefaultImagesInit(context);
		
		return dropboxDefaultBitmapImage;
	}
	public static Drawable dropboxDefaultDrawableImageGet(Context context) {
		dropboxDefaultImagesInit(context);
		
		return dropboxDefaultDrawableImage;
	}
    public static Uri dropboxDefaultImageUriGet() {
		return dropboxDefaultImageUri;
	}
    public static String dropboxDefaultImageUriStringGet() {
    	return dropboxDefaultImageUriString;
    }

    public static Matrix imageMatrixDefaultGet(float scale) {
        if (defaultImageMatrix == null)
        	defaultImageMatrix = new Matrix();

        defaultImageMatrix.reset();
		defaultImageMatrix.postScale(IMAGE_SCALE_X * scale, IMAGE_SCALE_Y * scale);

        return defaultImageMatrix;
	}
    
    public static void transparentDefaultImagesInit(Context context) {
		// create the transparent default bitmap image
		if (transparentDefaultBitmapImage == null) {
			Bitmap referenceBitmap = BitmapFactory.decodeResource(context.getResources(),
				ITEM_DEFAULT_IMAGE);
			Bitmap.Config conf = Bitmap.Config.ARGB_8888;
			
			transparentDefaultBitmapImage = Bitmap.createBitmap(referenceBitmap.getWidth(),
				referenceBitmap.getHeight(), conf);
			transparentDefaultBitmapImage = Bitmap.createBitmap(transparentDefaultBitmapImage, 0, 0,
				transparentDefaultBitmapImage.getWidth(), transparentDefaultBitmapImage.getHeight(),
				imageMatrixDefaultGet(1f), false);
//XXX		imageBitmap.recycle();
		}
		
		// create the transparent default drawable image
		if (transparentDefaultDrawableImage == null)
			transparentDefaultDrawableImage = new BitmapDrawable(context.getResources(),
				transparentDefaultBitmapImage);
/*
		// cache the bitmap
		if (!AppState.imageCacheContainsKey(context, GenericListItem.IMAGE_TYPE_TRANSPARENT_DEFAULT)) {
			byte[] bytes = bitmapToByteArrayConvert(transparentDefaultBitmapImage);
			AppState.imageCacheImageAdd(context, GenericListItem.IMAGE_TYPE_TRANSPARENT_DEFAULT, bytes);
		}
*/
	}
	public static Bitmap transparentDefaultBitmapImageGet(Context context) {
		transparentDefaultImagesInit(context);
		
		return transparentDefaultBitmapImage;
	}
	public static Drawable transparentDefaultDrawableImageGet(Context context) {
		transparentDefaultImagesInit(context);
		
		return transparentDefaultDrawableImage;
	}
	public static byte[] transparentDefaultBitmapBytesGet(Context context) {
		transparentDefaultImagesInit(context);

		// get the bitmap bytes from the cache if it's there
		byte[] bytes = bitmapToByteArrayConvert(transparentDefaultBitmapImage);
		
		return bytes;
	}
	
	public static int ITEM_DEFAULT_IMAGE = R.drawable.ic_contact;
	public static void itemDefaultImagesInit(Context context) {
		// create the item default bitmap image
		if (itemDefaultBitmapImage == null) {
			Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(), ITEM_DEFAULT_IMAGE);

			itemDefaultBitmapImage = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(),
				imageBitmap.getHeight(), imageMatrixDefaultGet(1f), false);
			
			itemDefaultImageUriString = context.getString(ITEM_DEFAULT_IMAGE);
			itemDefaultImageUri = Uri.parse(itemDefaultImageUriString);
		}
		
		// create the item default drawable image
		if (itemDefaultDrawableImage == null)
			itemDefaultDrawableImage = new BitmapDrawable(context.getResources(), itemDefaultBitmapImage);
	}
	private static final int DEFAULT_BITMAP_PAINT_FLAGS = Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG |
		Paint.DITHER_FLAG;
	public static Bitmap itemDefaultBitmapImageGet(Context context) {
		itemDefaultImagesInit(context);
		
		Bitmap bitmap = Bitmap.createBitmap(itemDefaultBitmapImage.getWidth(),
			itemDefaultBitmapImage.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawBitmap(itemDefaultBitmapImage, defaultImageMatrix, new Paint(DEFAULT_BITMAP_PAINT_FLAGS));
		
		return bitmap;
	}
	public static Drawable itemDefaultDrawableImageGet(Context context) {
		itemDefaultImagesInit(context);
		
		return itemDefaultDrawableImage;
	}
    public static Uri itemDefaultImageUriGet() {
		return itemDefaultImageUri;
	}
    public static String itemDefaultImageUriStringGet() {
    	return itemDefaultImageUriString;
    }
    
    public static int DEFAULT_DIRECTORY_IMAGE = R.drawable.folder;
    public static final String DIRECTORY_IMAGE_FILENAME = "directory.png";
	public static void directoryImageInit(Context context) {
		// create a directory image file
		File dataDirectory = AppState.appDataDirectoryFileGet(context);
		File imageFile = new File(dataDirectory, DIRECTORY_IMAGE_FILENAME);
		if (!imageFile.exists()) {
			Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), DEFAULT_DIRECTORY_IMAGE);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
				imageMatrixDefaultGet(1f), false);
			genericImageSave(dataDirectory, DIRECTORY_IMAGE_FILENAME, bitmap);
		}
	}
	public static String directoryImageFilenameGet(Context context) {
		return String.format("%s/%s", AppState.appDataDirectoryFileGet(context), DIRECTORY_IMAGE_FILENAME);
	}
	
	public static int DEFAULT_FILE_IMAGE = R.drawable.folder;
    public static final String FILE_IMAGE_FILENAME = "file.png";
	public static void fileImageInit(Context context) {
		// create a file image file
		File dataDirectory = AppState.appDataDirectoryFileGet(context);
		File imageFile = new File(dataDirectory, FILE_IMAGE_FILENAME);
		if (!imageFile.exists()) {
			Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), DEFAULT_FILE_IMAGE);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
				imageMatrixDefaultGet(1f), false);
			genericImageSave(dataDirectory, FILE_IMAGE_FILENAME, bitmap);
		}
	}
	public static String fileImageFilenameGet(Context context) {
		return String.format("%s/%s", AppState.appDataDirectoryFileGet(context), FILE_IMAGE_FILENAME);
	}
	
	public static Bitmap bitmapScale(Bitmap sourceBitmap, Bitmap referenceBitmap, float scale) {
		AppState.logX(TAG, String.format("reference.width = %d, reference.height = %d", referenceBitmap
			.getWidth(), referenceBitmap.getHeight()));
		
	    Bitmap bitmap = null;
		try {
		    if (referenceBitmap != null && referenceBitmap.getWidth() > 0 && referenceBitmap.getHeight() > 0) {
			    Bitmap scaledRef = Bitmap.createBitmap(referenceBitmap, 0, 0, referenceBitmap.getWidth(),
			        referenceBitmap.getHeight(), imageMatrixDefaultGet(scale), false);
			
		        bitmap = Bitmap.createScaledBitmap(sourceBitmap, scaledRef.getWidth(), scaledRef.getHeight(),
		        	false);
		    }
		} catch (IllegalArgumentException exception) {
			AppState.logX(TAG, "bitmapScale: IllegalArgumentException");
		}
		
		return bitmap;
	}
	
	public static Drawable drawableScale(Context context, Drawable sourceDrawable, Bitmap referenceBitmap,
		float scale) {
		Bitmap scaledRef = Bitmap.createBitmap(referenceBitmap, 0, 0, referenceBitmap.getWidth(),
			referenceBitmap.getHeight(), imageMatrixDefaultGet(scale), false);
		
		return new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(
			drawableToBitmap(sourceDrawable), scaledRef.getWidth(), scaledRef.getHeight(), false));
	}
	public static Bitmap drawableToBitmap(Drawable drawable) {
	    Bitmap bitmap = null;

	    if (drawable instanceof BitmapDrawable) {
	        bitmap = ((BitmapDrawable)drawable).getBitmap();
	    } else {
	    	int width = drawable.getIntrinsicWidth();
	    	width = width > 0 ? width : 1;
	    	int height = drawable.getIntrinsicHeight();
	    	height = height > 0 ? height : 1;

	    	bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
	    	Canvas canvas = new Canvas(bitmap); 
	    	drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    	drawable.draw(canvas);
	    }
	    
	    return bitmap;
	}
	
	public static Bitmap imageUriToBitmapConvert(Context context, Uri imageUri, Options options,
		float scale) throws IOException {
		Bitmap imageBitmap = null;
		String imageCacheKey = imageUri.toString();
		
	    AppState.logX(TAG, String.format("imageUriToBitmapConvert: imageUri = %s", imageUri.toString()));
	 	
	 	if (imageBitmap == null) {
	 		InputStream is = context.getContentResolver().openInputStream(imageUri);
	 		imageBitmap = BitmapFactory.decodeStream(is, null, options);
	 		is.close();

	 		if (imageBitmap != null) {
	 			imageBitmap = bitmapScale(imageBitmap, itemDefaultBitmapImageGet(context), scale);
	 		}
	 	}
		
		return imageBitmap;
	}
	public static Bitmap imageUriToBitmapConvert(Context context, String imageString, Options options,
		float scale) throws IOException {
		return imageUriToBitmapConvert(context, Uri.parse(imageString), options, scale);
	}

	public static Bitmap imageFilenameToBitmapConvert(Context context, String imageFilename, float scale) {
		Bitmap imageBitmap = null;
		
	    AppState.logX(TAG, String.format("imageFilenameToBitmapConvert: imageFilename = %s", imageFilename));

	    if (imageBitmap == null) {
	 		Bitmap bm = BitmapFactory.decodeFile(imageFilename);
	 		
	 		if (bm != null) {
	 			imageBitmap = bitmapScale(bm, itemDefaultBitmapImageGet(context), scale);
	 		} else {
	 			imageBitmap = bm;
	 		}
	 	}
	    
		return imageBitmap;
	}
	
	public static Bitmap imageResourceIdToBitmapConvert(Context context, int resid, Options options,
		float scale) {
		Bitmap imageBitmap = null;
		String imageCacheKey = Integer.toString(resid);

	    AppState.logX(TAG, "imageResourceIdToBitmapConvert");
	    
	    if (imageBitmap == null) {
	  		Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resid, options);
	  		
	  		if (bm != null) {
	  			imageBitmap = bitmapScale(bm, itemDefaultBitmapImageGet(context), scale);
	  		} else {
	  			imageBitmap = bm;
	  		}
	  	}
		
		return imageBitmap;
	}
	public static Bitmap imageResourceIdToBitmapConvert(Context context, String resid, Options options,
		float scale) {
		return imageResourceIdToBitmapConvert(context, Integer.parseInt(resid), options, scale);
	}
	
	public static byte[] imageResourceIdToBitmapBytesConvert(Context context, int resid, Options options,
		float scale) {
		Bitmap imageBitmap = null;
		byte[] imageBytes = null;
		String imageCacheKey = Integer.toString(resid);

		AppState.logX(TAG, "imageResourceIdToBitmapConvert");

		if (imageBitmap == null) {
			Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resid, options);
			if (bm != null) {
				imageBitmap = bitmapScale(bm, itemDefaultBitmapImageGet(context), scale);
			} else {
				imageBitmap = bm;
			}

			if (imageBitmap != null)
				imageBytes = bitmapToByteArrayConvert(imageBitmap);
		}

		return imageBytes;
	}
	public static byte[] imageResourceIdToBitmapBytesConvert(Context context, String resid, Options options,
		float scale) {
		return imageResourceIdToBitmapBytesConvert(context, Integer.parseInt(resid), options, scale);
	}
	
	public static Bitmap imageUrlToBitmapConvert(Context context, URL imageUrl, Options options, float scale)
		throws IOException {
		Bitmap imageBitmap = null;
        String imageCacheKey = imageUrl.toString();
        
		AppState.logX(TAG, String.format("imageUrlToBitmapConvert: imageUrl = %s", imageCacheKey));

		// retrieve the image if the imageBitmap is still null
		if (imageBitmap == null) {
			HttpURLConnection connection = (HttpURLConnection)imageUrl.openConnection();
			BufferedInputStream bufferedImageStream = null;
			InputStream imageStream = null;

			connection.connect();
			imageStream = connection.getInputStream();
			bufferedImageStream = new BufferedInputStream(imageStream);
			Options bfo = options;

			if (bfo == null) {
				bfo = new Options();
				bfo.inSampleSize = ImagesUtils.DEFAULT_BITMAP_SAMPLE_SIZE;
			}

			if (imageStream != null) {
				Bitmap bm = BitmapFactory.decodeStream(bufferedImageStream, null, bfo);

				AppState.logX(TAG, String.format("imageUrlToBitmapConvert: imageStream null, bm = %s", bm));

				if (bm != null) {
					imageBitmap = bitmapScale(bm, itemDefaultBitmapImageGet(context), scale);
					//XXX bm.recycle();
				} else {
					imageBitmap = bm;
				}

				bufferedImageStream.close();
				imageStream.close();
				connection.disconnect();
			}
			
			if (AppState.dataCacheIsEnabled(context) && imageBitmap != null) {
				byte[] imageBitmapData = bitmapToByteArrayConvert(imageBitmap);
			}
		}
		
		return imageBitmap;
	}

	public static byte[] bitmapToByteArrayConvert(Bitmap bitmap) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0, bos);
		byte[] bytes = bos.toByteArray();
		try {
			bos.close();
		} catch (IOException e) {
			AppState.logX(TAG, "bitmapToByteArrayConvert: IOException");
		}
		
		return bytes;
	}
	
	public static void genericImageSave(File directory, String imageFilename, Bitmap imageBitmap) {
		File imageBitmapFile = new File(directory, imageFilename);
		try {
			BufferedOutputStream imageBitmapOutputStream =
			    new BufferedOutputStream(new FileOutputStream(imageBitmapFile));
			
			if (imageFilename.endsWith(EXT_JPG) || imageFilename.endsWith(EXT_JPEG))
			    imageBitmap.compress(CompressFormat.JPEG, JPG_COMPRESSION_QUALITY, imageBitmapOutputStream);
			else if (imageFilename.endsWith(EXT_PNG))
				imageBitmap.compress(CompressFormat.PNG, PNG_COMPRESSION_QUALITY, imageBitmapOutputStream);
			else
				imageBitmap.compress(CompressFormat.JPEG, JPG_COMPRESSION_QUALITY, imageBitmapOutputStream);

			imageBitmapOutputStream.flush();
			imageBitmapOutputStream.close();
		} catch (FileNotFoundException e1) {
			AppState.logX(TAG, "genericImageSave: FileNotFoundException");
		} catch (IOException e) {
			AppState.logX(TAG, "genericImageSave: IOException");
		}
	}
	
	public static Bitmap bitmapResize(Resources resources, int origDrawableResId, int modelDrawableResId) {		
		Bitmap origBitmap = BitmapFactory.decodeResource(resources, origDrawableResId);
		float origWidth = origBitmap.getWidth();
		float origHeight = origBitmap.getHeight();
		Bitmap modelBitmap = BitmapFactory.decodeResource(resources, modelDrawableResId);
		float scaleWidth = modelBitmap.getWidth() / origWidth;
		float scaleHeight = modelBitmap.getHeight() / origHeight;
	    Matrix matrix = new Matrix();

		matrix.reset();
		matrix.postScale(scaleWidth, scaleHeight);
			
		return Bitmap.createBitmap(origBitmap, 0, 0, (int)origWidth, (int)origHeight, matrix, false);
	}
}