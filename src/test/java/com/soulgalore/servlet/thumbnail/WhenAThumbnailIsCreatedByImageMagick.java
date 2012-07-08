/******************************************************
 * Image resize servlet
 * 
 *
 * Copyright (C) 2012 by Peter Hedenskog (http://peterhedenskog.com)
 *
 ******************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at
 * 
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is 
 * distributed  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   
 * See the License for the specific language governing permissions and limitations under the License.
 *
 *******************************************************
 */
package com.soulgalore.servlet.thumbnail;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import org.junit.Test;

public class WhenAThumbnailIsCreatedByImageMagick {

	@Test
	public void theFileShouldBeCreated() throws Exception {
		
		URL url = this.getClass().getResource("/webapp/originals/");
		File testPng = new File(url.getFile());
		String originalsDir = testPng.getPath();
		String thumbName = "test-120x94.png";
		Thumbnail thumb = new Thumbnail(thumbName, originalsDir, originalsDir);
		ImageMagickFileThumbnailCreator creator = new ImageMagickFileThumbnailCreator(thumb);
		File destFile = creator.call();
		assertTrue("The thumbnail doesn't exist:" + destFile.getAbsolutePath(), destFile.exists());
	}

}
