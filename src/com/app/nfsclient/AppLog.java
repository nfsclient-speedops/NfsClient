/*
 * Copyright (c) 4 SpeedOps
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
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

public class AppLog {
	private static final String TAG = "AppLog";

	private boolean debugOn = true;
	private boolean logToFile = false;
	
	public PrintStream logStream;
		
	private static final String D_TYPE = "D";
	private static final String E_TYPE = "E";
	private static final String I_TYPE = "I";
	private static final String V_TYPE = "V";
    private static final String W_TYPE = "W";
	
    private static final String timezone = "America/Los_Angeles";

	public AppLog(String filename) {
		if (debugOn && !TextUtils.isEmpty(filename)) {
			try {
				logStream = new PrintStream(filename);
				Log.e(TAG, String.format("AppLog: created logfile = %s", filename));
			} catch (FileNotFoundException e) {
				Log.e(TAG, String.format("AppLog: unable to create logfile = %s", filename));
				logStream = null;
			}
		} else {
			logStream = null;
		}
	}
	public AppLog(File file) {
		if (debugOn) {
			try {
				logStream = new PrintStream(file);
				Log.e(TAG, String.format("AppLog: created logfile = %s", file.getName()));
				System.setOut(logStream); System.setErr(logStream);
			} catch (FileNotFoundException e) {
				Log.e(TAG, String.format("AppLog: unable to create logfile = %s", file.getName()));
				logStream = null;
			}
		} else {
			logStream = null;
		}
	}

	private void print(String type, String tag, String msg) {
        if (debugOn) {
        	String dateTime = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
        	    .format(Calendar.getInstance(TimeZone.getTimeZone(timezone)).getTime());
            String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(Calendar.getInstance()
            	.getTime());
        	int pid = Process.myPid();

            if (logToFile && logStream != null) {
        		logStream.printf("%s\t%s\t%d\t%s\t%s\n", dateTime, type, pid, tag, msg);
        		logStream.flush();
        	}

            msg = String.format("%s: %s", time, msg);
            
        	if (type.equals(D_TYPE))
        		Log.d(tag, msg);
        	else if (type.equals(E_TYPE))
        		Log.e(tag, msg);
        	else if (type.equals(I_TYPE))
        		Log.i(tag, msg);
        	else if (type.equals(V_TYPE))
        		Log.v(tag, msg);
        	else if (type.equals(W_TYPE))
        		Log.w(tag, msg);
        }

	}
	public void d(String tag, String msg) {
		print(D_TYPE, tag, msg);
	}
	public void e(String tag, String msg) {
		print(E_TYPE, tag, msg);
	}
	public void i(String tag, String msg) {
		print(I_TYPE, tag, msg);
	}
	public void v(String tag, String msg) {
		print(V_TYPE, tag, msg);
	}
	public void w(String tag, String msg) {
		print(W_TYPE, tag, msg);
	}
}