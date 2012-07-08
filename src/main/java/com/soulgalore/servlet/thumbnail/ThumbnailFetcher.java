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

import java.io.File;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fetch a thumbnail object.
 *
 */
public class ThumbnailFetcher {

	/**
	 * Where the original image will be located, need to be changed in a love
	 * environment.
	 */
	private final String originalBaseDir;

	/**
	 * The base dir where the thumbnails will be created.
	 */
	private final String destinationBaseDir;

	/**
	 * The valid sizes of an image.If the parameter is empty all sizes
	 * can be created.
	 */
	private final Set<String> validSizes;
	
	/**
	 * My logger.
	 */
	private final Logger logger = LoggerFactory
			.getLogger(ThumbnailServlet.class);

	ThumbnailFetcher(String theOriginalBaseDir, String theDestinationBaseDir,
			Set<String> theValidSizes) {
		originalBaseDir = theOriginalBaseDir;
		destinationBaseDir = theDestinationBaseDir;
		validSizes = theValidSizes;
	}

	/**
	 * Get a thumbnail object.
	 * @param fileName the name of the thumbnail
	 * @return the thumbnail
	 * @throws ThumbnailException if the size isn't valid or the original image doesn't exist
	 */
	Thumbnail get(String fileName) throws ThumbnailException {

		final Thumbnail thumbnail = new Thumbnail(fileName, originalBaseDir,
				destinationBaseDir);

		if (!isSizeValid(thumbnail))
			throw new ThumbnailException(
					Thumbnail.ERROR_MESSAGE_THUMBNAIL_SIZE_IS_NOT_VALID);

		// do the original image exist
		if (!doTheOriginalImageExist(thumbnail))
			throw new ThumbnailException(
					Thumbnail.ERROR_MESSAGE_ORIGINAL_IMAGE_DO_NOT_EXIST);
		
		return thumbnail;
	}


	/**
	 * Check if the original image exists.
	 * 
	 * @param thumbnail
	 *            the thumbnail
	 * @return true if it exists.
	 */
	protected boolean doTheOriginalImageExist(Thumbnail thumbnail) {
		final File originalFile = new File(thumbnail.getOriginalBaseDir()
				+ File.separator
				+ thumbnail.getOriginalImageNameWithExtension());
		if (logger.isDebugEnabled())
			logger.debug("Do the file {} exist {}",
					originalFile.getAbsoluteFile(), originalFile.exists());
		
		return originalFile.exists();
	}

	/**
	 * Check if the size of the thumbnail is valid.
	 * 
	 * @param thumbnail
	 *            the thumbnail
	 * @return true if the size is valid.
	 */
	protected boolean isSizeValid(Thumbnail thumbnail) {
		// skip validation if no sizes has been setup
		if (validSizes.isEmpty())
			return true;
		else if (!validSizes.contains(thumbnail.getImageDimensions()))
			return false;
		return true;

	}

}
