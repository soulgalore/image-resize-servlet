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
import java.io.IOException;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Image magick backend for creating thumbnails.
 * 
 */
class ImageMagickFileThumbnailCreator implements Callable<File> {

	private final Thumbnail thumbnail;

	private final Logger logger = LoggerFactory
			.getLogger(ImageMagickFileThumbnailCreator.class);

	/**
	 * Create a thumbnail creator.
	 * 
	 * @param thumb
	 *            the thumbnail that will be created.
	 */
	ImageMagickFileThumbnailCreator(Thumbnail thumb) {
		thumbnail = thumb;
	}

	@Override
	public File call() throws Exception {
		final ProcessBuilder pb = new ProcessBuilder("convert", "-thumbnail",
				thumbnail.getImageDimensions(), thumbnail.getOriginalBaseDir()
						+ File.separator
						+ thumbnail.getOriginalImageNameWithExtension(),
				thumbnail.getDestinationDir() + File.separator
						+ thumbnail.getImageFileName());

		pb.directory(new File(thumbnail.getOriginalBaseDir()));
		
		try {
			final Process p = pb.start();
			// wait until it's created
			p.waitFor();

			return new File(thumbnail.getDestinationDir() + File.separator
					+ thumbnail.getImageFileName());

		} catch (IOException e) {
			if (logger.isErrorEnabled())
				logger.error("Couldn't create thumbnail", e);
			throw e;
		}

	}
}
