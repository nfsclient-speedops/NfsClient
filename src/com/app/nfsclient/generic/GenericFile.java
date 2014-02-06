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

import java.io.IOException;

public abstract class GenericFile {
    private static final String TAG = "GenericFile";
	
    private String path;
    
	public GenericFile(String path) {
	    this.path = path;
	}
	
	public GenericFile getParentFile() {
		return null;
	}
	
	public GenericFile[] listFiles() {
		return null;
	}

	public boolean isDirectory() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getAbsolutePath() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean canWrite() {
		// TODO Auto-generated method stub
		return false;
	}

	public int length() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean delete() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean createNewFile() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean mkdirs() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isFile() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean renameTo(GenericFile newFile) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getPath() {
		// TODO Auto-generated method stub
		return null;
	}
}