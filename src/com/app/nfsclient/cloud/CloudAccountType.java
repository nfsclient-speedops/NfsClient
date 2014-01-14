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

package com.app.nfsclient.cloud;

import com.app.nfsclient.generic.GenericStorageInterface;

public enum CloudAccountType {
	BOX(GenericStorageInterface.STORAGE_BOX),
	DROPBOX(GenericStorageInterface.STORAGE_DROPBOX),
	GOOGLE_DRIVE(GenericStorageInterface.STORAGE_GOOGLE_DRIVE),
	LOCAL_STORAGE(GenericStorageInterface.STORAGE_LOCAL);

    private final String name;

    private CloudAccountType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}