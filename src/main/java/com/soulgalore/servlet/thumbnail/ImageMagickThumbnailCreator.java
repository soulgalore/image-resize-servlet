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

/**
 * Image magick backend for creating thumbnails.
 * 
 */
public class ImageMagickThumbnailCreator implements ThumbnailCreator {

	public ImageMagickThumbnailCreator() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soulgalore.servlet.thumbnail.ThumbnailCreator#createThumbnail(com
	 * .soulgalore.servlet.thumbnail.Thumbnail, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public File createThumbnail(Thumbnail thumbnail)
			throws InterruptedException, IOException {

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

		} catch (IOException e1) {
			throw e1;
		}
	}
}
