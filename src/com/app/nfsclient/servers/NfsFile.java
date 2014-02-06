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

import java.io.File;
import java.io.IOException;

import com.app.nfsclient.AppState;
import com.app.nfsclient.generic.GenericFileInterface;
import com.app.nfsclient.protocol.xfile.XFile;

public class NfsFile extends XFile implements GenericFileInterface {
	private static final String TAG = "NfsFile";
        
	public NfsFile(String path) {
		super(path);
		AppState.log(TAG, "constructor");
		
	}

	@Override
	public GenericFileInterface getParentFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenericFileInterface[] listFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean createNewFile() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean renameTo(GenericFileInterface newFile) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public File getNativeFile() {
        return getNative();
	}
}