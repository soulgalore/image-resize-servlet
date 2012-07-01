/******************************************************
 * Imagemagick resize servlet
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

import java.io.File;
import java.io.IOException;

interface ThumbnailCreator {

	/**
	 * Create a resized thumbnail image from a original image. The thumbnail 
	 * will be of the same type as the original image.
	 * 
	 * @param thumbnail
	 * @return the thumbnail file
	 * @throws InterruptedException
	 * @throws IOException
	 * 
	 */
	public File createThumbnail(Thumbnail thumbnail)
			throws InterruptedException, IOException;

}