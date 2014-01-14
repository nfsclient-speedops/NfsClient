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

import java.util.concurrent.LinkedBlockingQueue;

import com.app.nfsclient.AppState;

import android.content.Context;

public abstract class GenericAppStateInitAsyncTask extends GenericAsyncTask<Object, Void, Void> {
	public static final String TAG = "GenericAppStateInitAsyncTask";
	
	public static final int STATE_IDLE = 0;
	public static final int STATE_PROCESSING = 0x1;
	public static final int STATE_INITIALIZED = 0x2;
	public static final int STATE_ERROR = 0x4;
	
	public static class GenericProgressUpdate {
		public String sender;
		public int progress;
		
		public GenericProgressUpdate(String sender, int progress) {
			this.sender = sender;
			this.progress = progress;
		}
	}
    
	protected Context context;
	protected String name;
	protected LinkedBlockingQueue<GenericProgressUpdate> queue;
	
	protected int state = STATE_IDLE;
	
	public GenericAppStateInitAsyncTask(String name, LinkedBlockingQueue<GenericProgressUpdate> queue) {
		this.name = name;
		this.queue = queue;
		
		AppState.logX(TAG, String.format("constructor: name = %s, queue = %s", name, queue));
	}
	
	public Context contextGet() {
		return context;
	}
	
	public String nameGet() {
		return name;
	}
	
	public LinkedBlockingQueue<GenericProgressUpdate> queueGet() {
	    return queue;	
	}
	
	public int stateGet() {
		return state;
	}
	public boolean isInitialized() {
		return state == STATE_INITIALIZED;
	}
	
	public void initTasksProgressUpdate() {
		try {
			if (queue != null)
			    queue.put(new GenericProgressUpdate(name, 100));
		} catch (InterruptedException e) {
			AppState.logX(TAG, "progressUpdate: InterruptedException");
		}
	}
	
	@Override
	protected void onPreExecute() {
		AppState.logX(TAG, "onPreExecute");
    	
		state = STATE_PROCESSING;
	}

	@Override
	protected Void doInBackground(Object... params) {
		AppState.logX(TAG, "doInBackground");
		
		initTasksProgressUpdate();
		
		return null;
	}
	
	@Override
	protected void onProgressUpdate(Void... params) {
	    AppState.logX(TAG, "onProgressUpdate");
	}
	
	@Override
	protected void onPostExecute(Void result) {
		AppState.logX(TAG, String.format("onPostExecute: task = %s", name));
		
		state = STATE_INITIALIZED;	
	}
}