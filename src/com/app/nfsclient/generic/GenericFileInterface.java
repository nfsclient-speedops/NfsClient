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

import java.io.File;
import java.io.IOException;

public interface GenericFileInterface {	    
	public GenericFileInterface getParentFile();
	public GenericFileInterface[] listFiles();
	public boolean isDirectory();
	public String getAbsolutePath();
	public String getName();
	public boolean canWrite();
	public long length();
	public boolean exists();
	public String getParent();
	public boolean delete();
	public boolean createNewFile() throws IOException;
	public boolean mkdirs();
	public boolean isFile();
	public boolean renameTo(GenericFileInterface newFile);
	public String getPath();
	
	public File getNativeFile();
}