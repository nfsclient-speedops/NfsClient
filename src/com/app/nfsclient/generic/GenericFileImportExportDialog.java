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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.nfsclient.AppState;
import com.app.nfsclient.Background;
import com.app.nfsclient.R;
import com.app.nfsclient.Utils;
import com.app.nfsclient.filemanager.FileManagerActivity;
import com.app.nfsclient.filemanager.intents.FileManagerIntents;

public class GenericFileImportExportDialog extends GenericDialog {
	private static final String TAG = "GenericFileImportExportLocationDialog";

	private Fragment fragment = null;
	private String title;
	private String[] formatOptions;
	private String buttonLabel;
	private int selectionType;

	public GenericFileImportExportDialog(Activity activity, String title, String[] formatOptions,
		String buttonLabel, int selectionType) {
		super(activity, title, activity.getString(
	       	R.string.genericFileImportSelectionDialogLocationSelectionLabel), null);
		AppState.logX(TAG, "constructor 1");

		this.title = title;
		this.formatOptions = formatOptions;
		this.buttonLabel = buttonLabel;
		this.selectionType = selectionType;
		
		this.positiveButtonLabel = R.string.genericSelectButtonLabel;
		this.negativeButtonLabel = R.string.genericCancelButtonLabel;
	}
	
	public GenericFileImportExportDialog(Fragment fragment, String title, String[] formatOptions,
		String buttonLabel, int selectionType) {
		this(fragment.getActivity(), title, formatOptions, buttonLabel, selectionType);
		AppState.logX(TAG, "constructor 2");

		this.fragment = fragment;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppState.logX(TAG, "onCreate");

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
    	view = (View)(inflater.inflate(R.layout.generic_dialog_file_import_export_selection, null));
    	final GenericListItemList providers = AppState.storageProvidersGet(context);
    	final GenericSpinnerAdapter[] providerLocationsAdapters = new GenericSpinnerAdapter[providers.size()];

        AppState.logX(TAG, String.format("onCreate: providers.size = %d", providers.size()));

    	// initialize the first locations adapter to the local storage
        GenericListItemList locationsList = new GenericListItemList();
    	locationsList.add(GenericListItem.ITEM_TYPE_LOCAL_STORAGE, GenericListItem
    		.ITEM_TYPE_LOCAL_STORAGE);
    	providerLocationsAdapters[0] = new GenericSpinnerAdapter(context, locationsList);

    	// initialize the adapters for the cloud storage providers
    	final GenericListItemList cloudProviders = AppState.cloudStorageProvidersGet(context);
        for (int i = 0; i < cloudProviders.size(); i++) {
        	providerLocationsAdapters[i + 1] = new GenericSpinnerAdapter(context,
        		AppState.storageProviderLocationsGetByProvider(context, cloudProviders.get(i).firstGet()));
       	}
    	
        // providers
		final GenericSpinnerAdapter providersAdapter = new GenericSpinnerAdapter(context, providers);
    	final TextView providersLabel = (TextView)view.findViewById(
    		R.id.genericDialogFileImportExportLocationProviderSpinnerLabel);
    	providersLabel.setText(R.string.genericProviderLabel);
    	providersLabel.setTextColor(Background.textColorGet(context));
    	providersLabel.setBackgroundColor(Background.windowBackgroundGet(context));

    	final Spinner providersSpinner = (Spinner)view.findViewById(
    		R.id.genericDialogFileImportExportLocationProviderSpinner);
        providersSpinner.setAdapter(providersAdapter);
        providersSpinner.setBackgroundColor(Background.windowBackgroundGet(context));
        
        // locations
    	final TextView locationLabel = (TextView)view.findViewById(
       		R.id.genericDialogFileImportExportLocationNameSpinnerLabel);
       	locationLabel.setText(R.string.genericLocationLabel);
       	locationLabel.setTextColor(Background.textColorGet(context));
       	locationLabel.setBackgroundColor(Background.windowBackgroundGet(context));

        final Spinner locationsSpinner = (Spinner)view.findViewById(
       		R.id.genericDialogFileImportExportLocationNameSpinner);
        locationsSpinner.setBackgroundColor(Background.windowBackgroundGet(context));

       	// set the provider onItemSelected listener
       	providersSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				AppState.logX(TAG, String.format("onCreate:onItemSelected: position = %d, id = %d", position,
					id));

				// set the text color of the provider text
                ((TextView)parent.getChildAt(0)).setTextColor(Background.textColorGet(context));   

				// set the locations spinner
                locationsSpinner.setAdapter(providerLocationsAdapters[position]);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				AppState.logX(TAG, "fileImportLocationDialogCreate:onNothingSelected");
			}
        });
       	
       	if (positiveButton != null)
        	positiveButton.setOnClickListener(new SelectButtonListener(providersSpinner, locationsSpinner));
       	
       	if (negativeButton != null)
		    negativeButton.setOnClickListener(new View.OnClickListener() {
		    @Override
			public void onClick(View v) {
				AppState.logX(TAG, "fileImportLocationDialogCreate: negative button");

		        dismiss();
		    }
		});
       	
       	// goto the only provider
       	if (providers.size() == 1 && providerLocationsAdapters[0].getCount() == 1)
       		positiveButton.performClick();
	}
	
	@Override
	public void show() {
    	GenericListItemList providers = AppState.storageProvidersGet(context);
        AppState.logX(TAG, String.format("show: providers.size = %d", providers.size()));
        
    	if (providers.size() == 1) {
    		GenericListItemList locations = AppState.storageProviderLocationsGetByProvider(context,
    			providers.get(0).firstGet());
            AppState.logX(TAG, String.format("show: locations.size = %d", locations.size()));

    		if (locations.size() == 1) 
    			selectionActivityStart(providers.get(0), locations.get(0));
    	} else {
    		int direction = selectionType & GenericListItem.ACTIVITY_RESULT_FILE_SELECTION_TYPE_SUPER_MASK;
    		if (direction == GenericListItem.ACTIVITY_RESULT_FILE_SELECTION_TYPE_EXPORT ||
    			direction == GenericListItem.ACTIVITY_RESULT_FILE_SELECTION_TYPE_IMPORT)
    			super.show();
    		else
    			Utils.alertDialogShow(context, context.getString(R.string.genericInvalidRequest),
    				String.format("Invalid file selection type %d", selectionType));
    	}
	}
	
	protected class SelectButtonListener implements View.OnClickListener {
		private static final String TAG = GenericFileImportExportDialog.TAG + ":SelectButtonListener";
		
		private Spinner providersSpinner;
		private Spinner locationsSpinner;
		
		public SelectButtonListener(Spinner providersSpinner, Spinner locationsSpinner) {
			AppState.logX(TAG, "constructor");
			
			this.providersSpinner = providersSpinner;
			this.locationsSpinner = locationsSpinner;
		}
		
		@Override
		public void onClick(View v) {
			AppState.logX(TAG, "onClick: positive button");

			// storage locations
			GenericListItem provider = (GenericListItem)providersSpinner.getSelectedItem();
			GenericListItem location = (GenericListItem)locationsSpinner.getSelectedItem();

			selectionActivityStart(provider, location);
			if (provider.itemTypeGet().equalsIgnoreCase(GenericListItem.ITEM_TYPE_LOCAL_STORAGE)) {
				Intent intent = new Intent(context, FileManagerActivity.class);
				Uri uriFile = Uri.parse(AppState.appDataDirectoryFileGet(context).getAbsolutePath());
                int requestCode = selectionType | GenericListItem
                	.ACTIVITY_RESULT_FILE_SELECTION_PROVIDER_LOCAL_STORAGE;
				intent.setAction(FileManagerIntents.ACTION_PICK_FILE);
                intent.setData(uriFile);
				
				// Set the title and the button
                intent.putExtra(FileManagerIntents.EXTRA_TITLE, title);
				intent.putExtra(FileManagerIntents.EXTRA_FILE_FORMAT, formatOptions);
				intent.putExtra(FileManagerIntents.EXTRA_BUTTON_TEXT, buttonLabel);
				
				if (fragment != null)
					fragment.startActivityForResult(intent, requestCode);
				else
				    ((Activity)context).startActivityForResult(intent, requestCode);
			}
	        dismiss();
		}
	}
	
	public void selectionActivityStart(GenericListItem provider, GenericListItem location) {
		AppState.logX(TAG, String.format("selectionActivityStart: provider = %s, location = %s",
			provider.firstGet(), location.firstGet()));

		// storage locations
		if (provider.itemTypeGet().equalsIgnoreCase(GenericListItem.ITEM_TYPE_LOCAL_STORAGE)) {
			Intent intent = new Intent(context, FileManagerActivity.class);
			Uri uriFile = Uri.parse(AppState.appDataDirectoryFileGet(context).getAbsolutePath());
            int requestCode = selectionType | GenericListItem
            	.ACTIVITY_RESULT_FILE_SELECTION_PROVIDER_LOCAL_STORAGE;
			intent.setAction(FileManagerIntents.ACTION_PICK_FILE);
            intent.setData(uriFile);
			
			// Set the title and the button
            intent.putExtra(FileManagerIntents.EXTRA_TITLE, title);
			intent.putExtra(FileManagerIntents.EXTRA_FILE_FORMAT, formatOptions);
			intent.putExtra(FileManagerIntents.EXTRA_BUTTON_TEXT, buttonLabel);
			
			if (fragment != null)
				fragment.startActivityForResult(intent, requestCode);
			else
			    ((Activity)context).startActivityForResult(intent, requestCode);
		}
	}
}