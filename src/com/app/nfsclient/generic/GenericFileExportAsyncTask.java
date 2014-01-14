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

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.app.nfsclient.AppState;
import com.app.nfsclient.R;
import com.app.nfsclient.filemanager.util.FileUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

public class GenericFileExportAsyncTask extends GenericAsyncTask<Void, Void, Boolean> {
	private static final String TAG = "GenericFileExportAsyncTask";

	private Context context;
	private String text;
	private String filename;
	private String format;
	private ProgressDialog progressDialog;
	
	public GenericFileExportAsyncTask(Context context, String filename, String format, String text) {
		this.context = context;
		this.text = text;
		this.filename = filename;
		this.format = format;
	}
	
	@Override
	protected void onPreExecute() {
		AppState.logX(TAG, "onPreExecute");

		progressDialog = GenericListItemsListFragment.onCreateDialog(context, GenericListItem
			.DIALOG_FILE_EXPORT);
		progressDialog.show();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		AppState.logX(TAG, String.format("doInBackground: filename = %s, format = %s, text = %s", filename,
			format, text));

		String ext = FileUtils.getExtension(filename);
		String ext_txt = context.getString(R.string.file_extension_txt);
		
		Boolean result = false;
		if (!TextUtils.isEmpty(text)) {
			AppState.logX(TAG, "doInBackground: exporting as a text file");

			if (TextUtils.isEmpty(ext) || !ext.equalsIgnoreCase(ext_txt))
				filename = filename.concat(ext_txt);

			try {
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(filename));
				stream.write(text.getBytes());
				stream.flush();
				stream.close();
				result = true;
			} catch (FileNotFoundException e) {
				AppState.logX(TAG, "doInBackground: FileNotFoundException");
			} catch (IOException e) {
				AppState.logX(TAG, "doInBackground: IOException");
			}

		}
		
		return result;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		AppState.logX(TAG, "onPostExecute");

		progressDialog.dismiss();
/*
		if (result)
		    Utils.alertDialogShow(context, "Export successful", String.format("The data was exported to the " +
		    	"file '%s'", filename));
		else
			Utils.alertDialogShow(context, "Export failed", String.format("An error occurred while trying to " +
				"export data to the file %s", filename));
*/
	}
	
	public static void fileExportConfirm(final Context context, final String fileName, final String fileFormat,
		final String fileTextBody) {
		String ext = FileUtils.getExtension(fileName);
		String ext_txt = context.getString(R.string.file_extension_txt);
		String ext_png = context.getString(R.string.file_extension_png);
		
		String tmp = fileName;
		if (fileFormat.equalsIgnoreCase(context.getString(R.string.genericFileFormatText)))	{	
		    if (TextUtils.isEmpty(ext) || !ext.equalsIgnoreCase(ext_txt))
		    	tmp = tmp.concat(ext_txt);
		}
		
		AppState.logX(TAG, String.format("fileExportConfirm: filename = %s, format = %s, textBodyText = %s",
			fileName, fileFormat, fileTextBody));
		
		final String exportFilename = tmp;
		final GenericAlertDialog dialog = new GenericAlertDialog(context);
		dialog
    	    .cancelableSet(false)
		    .titleSet(R.string.genericConfirm)
		    .messageSet(String.format("Export to the file %s?\n\nNote: This file will be overwritten if it " +
		    	"already exists.", tmp))
		    .positiveButtonSet(R.string.genericExportButtonLabel, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new GenericFileExportAsyncTask(context, exportFilename, fileFormat, fileTextBody)
				        .executeOnExecutor(GenericAsyncTask.THREAD_POOL_EXECUTOR);
					dialog.dismiss();
				}
			})
			.negativeButtonSet(R.string.genericCancelButtonLabel, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
                    dialog.dismiss();
				}
			});
		dialog.show();
	}
}