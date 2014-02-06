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

package com.app.nfsclient.servers;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import com.app.nfsclient.App;
import com.app.nfsclient.AppState;
import com.app.nfsclient.Background;
import com.app.nfsclient.R;
import com.app.nfsclient.Utils;
import com.app.nfsclient.generic.GenericAlertDialog;
import com.app.nfsclient.generic.GenericDialog;
import com.app.nfsclient.generic.GenericOnOffSwitch;
import com.app.nfsclient.generic.GenericOnOffSwitchPreference;
import com.app.nfsclient.generic.GenericPreferencesActivity;
import com.app.nfsclient.generic.GenericStringListPreference;
import com.app.nfsclient.generic.GenericTextPreference;

public class ServerPreferencesActivity extends GenericPreferencesActivity {
	private static final String TAG = "ServerPreferencesActivity";
	
	private static final boolean persistentState = true;
	private boolean modified = false;

	private boolean isNewServer = true;

	private PreferenceScreen serverEditPreferenceScreen;
	private PreferenceCategory serverSettingsPreferenceCategory;

	private String serverHostNamePreferenceKey;
	private GenericTextPreference serverHostNamePreference;

	private String serverInternetAddressPreferenceKey;
	private GenericTextPreference serverInternetAddressPreference;

	private String serverUserNamePreferenceKey;
	private GenericTextPreference serverUserNamePreference;

	private String serverUserPasswordPreferenceKey;
	private GenericOnOffSwitchPreference serverUserPasswordPreference;

	private String serverExportDirectoriesPreferenceKey;
	private GenericStringListPreference serverExportDirectoriesPreference;
	
    private GenericDialog preferenceDialog;

	private void currentServerInit() {
		AppState.logX(TAG, String.format("currentServerInit: exports = %s", getIntent().getStringExtra(
			ServersListFragment.SERVER_EXPORT_DIRECTORIES_KEY)));
				
	    AppState.stringSet(this, ServersListFragment.SERVER_HOST_NAME_KEY, getIntent().getStringExtra(
	    	ServersListFragment.SERVER_HOST_NAME_KEY));
	    AppState.stringSet(this, ServersListFragment.SERVER_INTERNET_ADDRESS_KEY, getIntent().getStringExtra(
		    ServersListFragment.SERVER_INTERNET_ADDRESS_KEY));
	    AppState.stringSet(this, ServersListFragment.SERVER_USER_NAME_KEY, getIntent().getStringExtra(
		    ServersListFragment.SERVER_USER_NAME_KEY));
	    AppState.stringSet(this, ServersListFragment.SERVER_USER_PASSWORD_KEY, getIntent().getStringExtra(
		    ServersListFragment.SERVER_USER_PASSWORD_KEY));
		AppState.stringArraySet(this, ServersListFragment.SERVER_EXPORT_DIRECTORIES_KEY, getIntent()
	    	.getStringArrayExtra(ServersListFragment.SERVER_EXPORT_DIRECTORIES_KEY));
		
		isNewServer = TextUtils.isEmpty(AppState.stringGet(this, ServersListFragment.SERVER_INTERNET_ADDRESS_KEY,
			null));
	}
	
	@Override
	protected void dataInit() {
		AppState.logX(TAG, "dataInit");

    	// set the window layout
		preferencesLayout = R.layout.server_preferences;
		modified = false;

		// set the current server attributes
	 	currentServerInit();
	}
	
	@Override
	protected void viewsInit() {
		super.viewsInit();
		AppState.logX(TAG, "viewsInit");

	    // preference screen
		serverEditPreferenceScreen = getPreferenceScreen();

		// server settings preference category title
		String currentServerHostName = AppState.stringGet(this, ServersListFragment.SERVER_HOST_NAME_KEY, null);
		serverSettingsPreferenceCategory = (PreferenceCategory)serverEditPreferenceScreen.getPreference(0);
		serverSettingsPreferenceCategory.setTitle(getString(R.string.serverPreferencesSettingsTitle,
			isNewServer ? "New" : String.format("\"%s\"", currentServerHostName)));
		
		serverHostNamePreferenceKey = getString(R.string.serverPreferencesServerHostNameKey);
		serverHostNamePreference = (GenericTextPreference)findPreference(serverHostNamePreferenceKey);
		serverHostNamePreference.setPersistent(persistentState);
		serverHostNamePreference.valueSet(AppState.stringGet(ServerPreferencesActivity.this,
			ServersListFragment.SERVER_HOST_NAME_KEY, Utils.EMPTY_STRING));
		serverHostNamePreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    	View view = (View)(inflater.inflate(R.layout.generic_dialog_edit_text, null));
		    	
		    	String hostName = AppState.stringGet(ServerPreferencesActivity.this,
			   		ServersListFragment.SERVER_HOST_NAME_KEY, Utils.EMPTY_STRING);
		    	final EditText editText = (EditText)view.findViewById(R.id.genericDialogEditText);
		    	editText.setText(hostName);
		    	
		    	preferenceDialog = new GenericDialog(ServerPreferencesActivity.this,
					getString(R.string.serverPreferencesServerHostNameDialogTitle), Utils.EMPTY_STRING, view);
		    	preferenceDialog.positiveButtonListenerSet(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String host = editText.getText().toString();
						if (!TextUtils.isEmpty(host)) {
							host = host.trim();
							if (AppState.serversGetByHostName(ServerPreferencesActivity.this, host) == null) {
								AppState.stringSet(ServerPreferencesActivity.this,
									ServersListFragment.SERVER_HOST_NAME_KEY, host);
								serverHostNamePreference.valueSet(host);
								modified = true;
							} else {
								Utils.alertDialogShow(ServerPreferencesActivity.this, getString(
									R.string.genericInvalidSetting), String.format("There is already a " +
									"server with the host name \"%s\"", host));
							}
						} else {
							Utils.alertDialogShow(ServerPreferencesActivity.this, getString(
								R.string.genericInvalidSetting), "The host name is empty.");	
						}

						preferenceDialog.dismiss();
					}
				});
		    	preferenceDialog.show();

				return false;
			}
		});

		serverInternetAddressPreferenceKey = getString(R.string.serverPreferencesServerInternetAddressKey);
		serverInternetAddressPreference = (GenericTextPreference)findPreference(
			serverInternetAddressPreferenceKey);
		serverInternetAddressPreference.setPersistent(persistentState);
		serverInternetAddressPreference.valueSet(AppState.stringGet(ServerPreferencesActivity.this,
	   		ServersListFragment.SERVER_INTERNET_ADDRESS_KEY, Utils.EMPTY_STRING));
		serverInternetAddressPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    	View view = (View)(inflater.inflate(R.layout.generic_dialog_edit_text, null));
		    	
		    	String internetAddress = AppState.stringGet(ServerPreferencesActivity.this,
		    		ServersListFragment.SERVER_INTERNET_ADDRESS_KEY, Utils.EMPTY_STRING);
		    	final EditText editText = (EditText)view.findViewById(R.id.genericDialogEditText);
		    	editText.setText(internetAddress);
		    	
		    	preferenceDialog = new GenericDialog(ServerPreferencesActivity.this, getString(
		    		R.string.serverPreferencesServerInternetAddressDialogTitle), getString(
					R.string.serverPreferencesServerInternetAddressDialogMessage), view);
		    	preferenceDialog.positiveButtonListenerSet(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String addr = editText.getText().toString();
						if (!TextUtils.isEmpty(addr)) {
							addr = addr.trim();

							AppState.logX(TAG, String.format("inet preferenceDialog: addr = %s", addr));
							
							if (isDotNotationAddress(addr)) {
								if (AppState.serversGetByInternetAddress(ServerPreferencesActivity.this, addr)
									== null) {
									AppState.stringSet(ServerPreferencesActivity.this,
										ServersListFragment.SERVER_INTERNET_ADDRESS_KEY, addr);
									serverInternetAddressPreference.valueSet(addr);
									modified = true;
								} else {
									Utils.alertDialogShow(ServerPreferencesActivity.this, getString(
										R.string.genericInvalidSetting), String.format("There is already a " +
										"server with the IP address \"%s\"", addr));
								}
							} else {
								Utils.alertDialogShow(ServerPreferencesActivity.this, getString(
									R.string.genericInvalidSetting), "The server IP address is invalid");
							}
						} else {
							Utils.alertDialogShow(ServerPreferencesActivity.this, getString(
								R.string.genericInvalidSetting), "The IP address is empty.");	
						}

						preferenceDialog.dismiss();
					}
				});
		    	preferenceDialog.show();

				return false;
			}
		});
		
		serverUserNamePreferenceKey = getString(R.string.serverPreferencesServerUserNameKey);
		serverUserNamePreference = (GenericTextPreference)findPreference(serverUserNamePreferenceKey);
		serverUserNamePreference.setPersistent(persistentState);
		serverUserNamePreference.valueSet(AppState.stringGet(ServerPreferencesActivity.this,
	   		ServersListFragment.SERVER_USER_NAME_KEY, Utils.EMPTY_STRING));
		serverUserNamePreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    	View view = (View)(inflater.inflate(R.layout.generic_dialog_edit_text, null));
		    	
		    	String userName = AppState.stringGet(ServerPreferencesActivity.this,
		    		ServersListFragment.SERVER_USER_NAME_KEY, Utils.EMPTY_STRING);
		    	final EditText editText = (EditText)view.findViewById(R.id.genericDialogEditText);
		    	editText.setText(userName);
		    	
		    	preferenceDialog = new GenericDialog(ServerPreferencesActivity.this, getString(
		    		R.string.serverPreferencesServerUserNameDialogTitle), Utils.EMPTY_STRING, view);
		    	preferenceDialog.positiveButtonListenerSet(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String name = editText.getText().toString();
						if (!TextUtils.isEmpty(name)) {
							name = name.trim();
							serverUserNamePreference.valueSet(name);
							AppState.stringSet(ServerPreferencesActivity.this,
								ServersListFragment.SERVER_USER_NAME_KEY, name);
							modified = true;
						} else {
							Utils.alertDialogShow(ServerPreferencesActivity.this, getString(
								R.string.genericInvalidSetting), "The server user name is empty");
						}

						preferenceDialog.dismiss();
					}
				});
		    	preferenceDialog.show();

				return false;
			}
		});
		
		serverUserPasswordPreferenceKey = getString(R.string.serverPreferencesServerUserPasswordKey);
		serverUserPasswordPreference = (GenericOnOffSwitchPreference)findPreference(
			serverUserPasswordPreferenceKey);
		serverUserPasswordPreference.setPersistent(persistentState);
		serverUserPasswordPreference.valueSet(AppState.stringGet(ServerPreferencesActivity.this,
	   		ServersListFragment.SERVER_USER_PASSWORD_KEY, Utils.EMPTY_STRING));
		serverUserPasswordPreference.setOnOffText(getString(R.string.genericPasswordShowLabel), 
			getString(R.string.genericPasswordHideLabel));
	    serverUserPasswordPreference.valueInputTypeSet(InputType.TYPE_CLASS_TEXT |
	    	InputType.TYPE_TEXT_VARIATION_PASSWORD);
		serverUserPasswordPreference.onPreferenceClickListenerSet(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    	View view = (View)(inflater.inflate(R.layout.generic_dialog_password, null));
		    	
		    	String userPassword = AppState.stringGet(ServerPreferencesActivity.this,
		    		ServersListFragment.SERVER_USER_PASSWORD_KEY, Utils.EMPTY_STRING);
		    	final EditText passwordView = (EditText)view.findViewById(R.id.genericPasswordDialogPassword);
		    	passwordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		    	passwordView.setText(userPassword);
		    	
		    	final EditText confirmView = (EditText)view.findViewById(R.id.genericPasswordDialogConfirm);
		    	confirmView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		    	confirmView.setText(userPassword);
		    	
		    	final GenericOnOffSwitch hideShowSwitch = (GenericOnOffSwitch)view.findViewById(
		           	R.id.genericPasswordDialogHideShowSwitch);
		    	hideShowSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		    		@Override
		    		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		    			int inputType = InputType.TYPE_CLASS_TEXT;
		    			if (!isChecked)
		    				inputType |= InputType.TYPE_TEXT_VARIATION_PASSWORD;

		    			passwordView.setInputType(inputType);
		    			passwordView.setSelection(passwordView.getText().length());

		    			confirmView.setInputType(inputType);
		    			confirmView.setSelection(confirmView.getText().length());
		    		}
		    	});
		    	
		    	preferenceDialog = new GenericDialog(ServerPreferencesActivity.this, getString(
		    		R.string.serverPreferencesServerUserPasswordDialogTitle), Utils.EMPTY_STRING, view);
		    	
		    	preferenceDialog.positiveButtonListenerSet(R.string.genericApplyButtonLabel,
		    		new View.OnClickListener() {
				    @Override
					public void onClick(View v) {
						String pass = (String)passwordView.getText().toString();
						String conf = (String)confirmView.getText().toString();

						if (pass.equals(conf)) {
							if (!TextUtils.isEmpty(pass)) {
								pass = pass.trim();
								serverUserPasswordPreference.valueSet(pass);
								AppState.stringSet(ServerPreferencesActivity.this,
									ServersListFragment.SERVER_USER_PASSWORD_KEY, pass);
								modified = true;
							} else {
								Utils.alertDialogShow(ServerPreferencesActivity.this, getString(
									R.string.genericInvalidSetting), "The password is empty");
							}
						} else {
							Utils.alertDialogShow(ServerPreferencesActivity.this, getString(
								R.string.genericInvalidSetting), "The password and the confirmation are not " +
									"the same");
						}
						
						preferenceDialog.dismiss();
					}
				});
		    	preferenceDialog.negativeButtonListenerSet(R.string.genericCancelButtonLabel,
		    		new View.OnClickListener() {
				    @Override
					public void onClick(View v) {
						AppState.logX(TAG, "applicationPasswordDialogCreate: negative button");

						preferenceDialog.dismiss();
				    }
				});
				
		    	preferenceDialog.show();

				return false;
			}
		});
		serverUserPasswordPreference.onCheckedChangeListenerSet(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {				
				int inputType = InputType.TYPE_CLASS_TEXT;
    			if (!isChecked)
    				inputType |= InputType.TYPE_TEXT_VARIATION_PASSWORD;
				serverUserPasswordPreference.valueInputTypeSet(inputType);
				serverUserPasswordPreference.stateSet(isChecked);
			}
		});
		
		serverExportDirectoriesPreferenceKey = getString(R.string.serverPreferencesServerExportDirectoriesKey);
		serverExportDirectoriesPreference = (GenericStringListPreference)findPreference(
			serverExportDirectoriesPreferenceKey);
		serverExportDirectoriesPreference.setPersistent(persistentState);
		serverExportDirectoriesPreference.listSet(AppState.stringArrayGet(ServerPreferencesActivity.this,
		   	ServersListFragment.SERVER_EXPORT_DIRECTORIES_KEY, Utils.EMPTY_STRING));
		serverExportDirectoriesPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				AppState.logX(TAG, "serverExportDirectoriesPreference:onPreferenceClickListener");
				
				LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    	View view = (View)(inflater.inflate(R.layout.generic_expandable_list, null));
		    	final List<LinearLayout> directoryRows = new ArrayList<LinearLayout>();
	
		    	final LinearLayout container = (LinearLayout)view.findViewById(
		    		R.id.genericExpandableListContainer);
		    	String[] directories = AppState.stringArrayGet(ServerPreferencesActivity.this,
				   	ServersListFragment.SERVER_EXPORT_DIRECTORIES_KEY, Utils.EMPTY_STRING);
			    for (String directory : directories)
		    		exportRowAdd(container, directoryRows, directory);
		    	
			    // add an empty row
			    exportRowAdd(container, directoryRows, Utils.EMPTY_STRING);
			    
			    // add the add_directory button
			    Button addButton = (Button)view.findViewById(R.id.genericExpandableListAddButton);
			    if (addButton != null) {
			    	addButton.setText(R.string.serverExportDirectoriesAddButton);
			    	addButton.setTextColor(getResources().getColor(R.color.blue));
			    	addButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
						    exportRowAdd(container, directoryRows, Utils.EMPTY_STRING);
						}
			    	});
			    }
			    
				preferenceDialog = new GenericDialog(ServerPreferencesActivity.this, getString(
					R.string.serverPreferencesServerExportDirectoriesDialogTitle), Utils.EMPTY_STRING, view);
				preferenceDialog.positiveButtonListenerSet(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (directoryRows.size() > 0) {
							List<String> exports = new ArrayList<String>(directoryRows.size());

							for (LinearLayout row : directoryRows) {
								EditText textView = (EditText)row.findViewById(
									R.id.genericExpandableListEditText);
								String directory = textView.getText().toString();
								directory = directory.trim();
								if (!TextUtils.isEmpty(directory))
									exports.add(directory);
							}
							
							if (exports.size() > 0) {
								Object[] exportsArray = exports.toArray();
								AppState.stringArraySet(ServerPreferencesActivity.this,
									ServersListFragment.SERVER_EXPORT_DIRECTORIES_KEY, exportsArray);
								serverExportDirectoriesPreference.listSet(exports);
								modified = true;
							} else {
								Utils.alertDialogShow(ServerPreferencesActivity.this, getString(
									R.string.genericInvalidSetting), "No export directories were added.");
							}
						}
						
						preferenceDialog.dismiss();
					}
				});
				preferenceDialog.show();

				return false;
			}
		});
		
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        	applyButtonResid = R.id.genericPreferencesLeftButton;
        	cancelButtonResid = R.id.genericPreferencesRightButton;
        }
        
		if ((applyButton = (Button)findViewById(applyButtonResid)) != null) {
			applyButton.setText(applyButtonLabel);
			applyButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					AppState.logX(TAG, "applyButton: onClick");

					preferencesSave();
				}
			});
			
        	applyButton.setTextColor(Background.textColorGet(this));
			applyButton.setBackgroundDrawable(Background.positiveButtonSelectorGet(this));
		}

		if ((cancelButton = (Button)findViewById(cancelButtonResid)) != null) {
			cancelButton.setText(cancelButtonLabel);
			cancelButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					AppState.logX(TAG, "cancelButton: onClick");

					finish(RESULT_CANCELED);
				}
			});

        	cancelButton.setTextColor(Background.textColorGet(this));
            cancelButton.setBackgroundDrawable(Background.positiveButtonSelectorGet(this));
		}
	}
	
	private void exportRowAdd(LinearLayout container, List<LinearLayout> directoryRows, String directoryName) {
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout row = (LinearLayout)(inflater.inflate(R.layout.generic_expandable_list_row, null));
		EditText textView = (EditText)row.findViewById(R.id.genericExpandableListEditText);
		textView.setText(directoryName);

		ImageButton removeButton = (ImageButton)row.findViewById(R.id.genericExpandableListButton);
		removeButton.setOnClickListener(new ExportRemoveListener(container, directoryRows, row));
		
		directoryRows.add(row);
		container.addView(row);
		row.requestFocus();
		container.refreshDrawableState();
	}
	private class ExportRemoveListener implements OnClickListener {
        private static final String TAG = ServerPreferencesActivity.TAG + "ExportRemoveListenter";
        
        private LinearLayout container;
        private List<LinearLayout> directoryRows;
        private LinearLayout row;
        
        public ExportRemoveListener(LinearLayout container, List<LinearLayout> directoryRows, LinearLayout row) {
        	AppState.logX(TAG, "constructor");
        	
        	this.container = container;
        	this.directoryRows = directoryRows;
        	this.row = row;
        }
        
		@Override
		public void onClick(View v) {
			directoryRows.remove(row);
			container.removeView(row);
			
			if (directoryRows.size() == 0)
			    exportRowAdd(container, directoryRows, Utils.EMPTY_STRING);

			container.refreshDrawableState();
		}
	}
	
	// check if a string is a valid dot notation internet address, i.e. n.n.n.n
	public final static boolean isDotNotationAddress(String internetAddress) {
		boolean check = true;
		internetAddress = internetAddress.trim();
		if (!TextUtils.isEmpty(internetAddress) && internetAddress.length() > 6 &&
			internetAddress.length() < 16) {
			StringTokenizer token = new StringTokenizer(internetAddress, ".");
			if (token.countTokens() == 4) {
				while (token.hasMoreTokens()) {
					String addrByteStr = token.nextToken();
					try {
						int addrByteInt = Integer.valueOf(addrByteStr).intValue();
						if (addrByteInt < 0 || addrByteInt > 255) {
							check = false;
							break;
						}
					} catch (NumberFormatException ex) {
						check = false;
						break;
					}
				}
			}
		}

		return check;
	}
	  
	@Override
	protected void onResume() {
		super.onResume();
		AppState.logX(TAG, "onResume");
		
		currentServerInit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	AppState.logX(TAG, "onCreateOptionsMenu");
    	
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.server_preferences_options_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		AppState.logX(TAG, "onOptionsItemSelected");
		
		boolean value = false;
		switch (item.getItemId()) {
		case android.R.id.home:
			AppState.logX(TAG, "onOptionsItemSelected: home selected");
			
			Intent intent = new Intent(this, App.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

			value = true;
			break;
		default:
			value = super.onOptionsItemSelected(item);
		}
		
		return value;
	}
	
	@Override
	public void onBackPressed() {
		AppState.logX(TAG, "onBackPressed");

		if (modified) {
			final GenericAlertDialog dialog = new GenericAlertDialog(this);
			dialog
	            .cancelableSet(false)
                .titleSet(R.string.genericConfirm)
                .messageSet("This server has been modified.")
                .positiveButtonSet(R.string.genericEditButtonLabel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
		                dialog.dismiss();
	                }
                })
                .negativeButtonSet(R.string.genericExitButtonLabel, new View.OnClickListener() {
        	        @Override
	                public void onClick(View v) {
		                dialog.dismiss();
				        finish(RESULT_CANCELED);
	                }
                });
            dialog.show();
		} else {
			super.onBackPressed();
		}
	}
	
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event)  { 
		AppState.logX(TAG, "onKeyDown"); 

		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR &&
			keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { 
			onBackPressed(); 
		}

		return super.onKeyDown(keyCode, event); 
	}
	
	public void finish(int resultCode) {
		AppState.logX(TAG, String.format("finish: resultCode = %d", resultCode));
		
		Intent data = new Intent();
        data.putExtra(ServersListFragment.FRAGMENT_TAG, getIntent().getBundleExtra(
        	ServersListFragment.FRAGMENT_TAG));
        setResult(resultCode, data);
        		
		super.finish();
 	}
	
	private void preferencesSave() {		
		String serverAddr = AppState.stringGet(ServerPreferencesActivity.this,
			ServersListFragment.SERVER_INTERNET_ADDRESS_KEY, Utils.EMPTY_STRING);
		String serverHost = AppState.stringGet(ServerPreferencesActivity.this,
			ServersListFragment.SERVER_HOST_NAME_KEY, Utils.EMPTY_STRING);
		String serverUserName = AppState.stringGet(ServerPreferencesActivity.this,
			ServersListFragment.SERVER_USER_NAME_KEY, Utils.EMPTY_STRING);
		String serverUserPassword = AppState.stringGet(ServerPreferencesActivity.this,
			ServersListFragment.SERVER_USER_PASSWORD_KEY, Utils.EMPTY_STRING);
		String[] serverExportsStr = AppState.stringArrayGet(ServerPreferencesActivity.this,
			ServersListFragment.SERVER_EXPORT_DIRECTORIES_KEY, Utils.EMPTY_STRING);
		
		AppState.logX(TAG, String.format("preferencesSave: inet = %s, host = %s, user name = %s, exports = %s",
			serverAddr, serverHost, serverUserName, Server.serverExportDirectoriesSet(serverExportsStr)));

		if (TextUtils.isEmpty(serverHost)) {
			final GenericAlertDialog dialog = new GenericAlertDialog(this);
			dialog
			    .cancelableSet(false)
			    .titleSet(R.string.genericConfirm)
			    .messageSet("Invalid host name.")
			    .positiveButtonSet(R.string.genericEditButtonLabel, new View.OnClickListener() {
				    @Override
				    public void onClick(View v) {
					    dialog.dismiss();
				    }
			    })
			    .negativeButtonSet(R.string.genericExitButtonLabel, new View.OnClickListener() {
				    @Override
				    public void onClick(View v) {
					    dialog.dismiss();
					    finish(RESULT_CANCELED);
				    }
			    });
			dialog.show();
		} else if (TextUtils.isEmpty(serverAddr)) {
			final GenericAlertDialog dialog = new GenericAlertDialog(this);
			dialog
	            .cancelableSet(false)
                .titleSet(R.string.genericConfirm)
                .messageSet("Invalid Internet address.")
                .positiveButtonSet(R.string.genericEditButtonLabel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
		                dialog.dismiss();
	                }
                })
                .negativeButtonSet(R.string.genericExitButtonLabel, new View.OnClickListener() {
        	        @Override
	                public void onClick(View v) {
		                dialog.dismiss();
				        finish(RESULT_CANCELED);
	                }
                });
            dialog.show();
		} else if (TextUtils.isEmpty(serverUserName)) {
			final GenericAlertDialog dialog = new GenericAlertDialog(this);
			dialog
	            .cancelableSet(false)
                .titleSet(R.string.genericConfirm)
                .messageSet("Invalid user name.")
                .positiveButtonSet(R.string.genericEditButtonLabel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
		                dialog.dismiss();
	                }
                })
                .negativeButtonSet(R.string.genericExitButtonLabel, new View.OnClickListener() {
        	        @Override
	                public void onClick(View v) {
		                dialog.dismiss();
				        finish(RESULT_CANCELED);
	                }
                });
            dialog.show();
		}  else if (TextUtils.isEmpty(serverUserPassword)) {
			final GenericAlertDialog dialog = new GenericAlertDialog(this);
			dialog
	            .cancelableSet(false)
                .titleSet(R.string.genericConfirm)
                .messageSet("Invalid password.")
                .positiveButtonSet(R.string.genericEditButtonLabel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
		                dialog.dismiss();
	                }
                })
                .negativeButtonSet(R.string.genericExitButtonLabel, new View.OnClickListener() {
        	        @Override
	                public void onClick(View v) {
		                dialog.dismiss();
				        finish(RESULT_CANCELED);
	                }
                });
            dialog.show();
		} else if (serverExportsStr == null || serverExportsStr.length == 0) {
			final GenericAlertDialog dialog = new GenericAlertDialog(this);
			dialog
	            .cancelableSet(false)
                .titleSet(R.string.genericConfirm)
                .messageSet("Invalid export directories.")
                .positiveButtonSet(R.string.genericEditButtonLabel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
		                dialog.dismiss();
	                }
                })
                .negativeButtonSet(R.string.genericExitButtonLabel, new View.OnClickListener() {
        	        @Override
	                public void onClick(View v) {
		                dialog.dismiss();
				        finish(RESULT_CANCELED);
	                }
                });
            dialog.show();
		} else {
			Server server = new Server(serverHost, serverAddr, serverUserName, serverUserPassword,
				serverExportsStr);
			
			if (isNewServer)
				AppState.serversAdd(this, server);
			else
				AppState.serversUpdate(this, server);
			
			finish(RESULT_OK);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		ProgressDialog dialog = new ProgressDialog(this);

		AppState.logX(TAG, String.format("onCreateDialog, id = %d", id));

		dialog.setTitle(Utils.EMPTY_STRING);
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);

		switch(id) {
		case DIALOG_LOADING:
			dialog.setMessage(getString(R.string.genericProgressDialogLoading));
			break;
		case DIALOG_APPLYING:
			dialog.setMessage(getString(R.string.genericProgressDialogApplyingPreferences));
			break;
		default:
			dialog = null;
		}

		return dialog;
	}
}