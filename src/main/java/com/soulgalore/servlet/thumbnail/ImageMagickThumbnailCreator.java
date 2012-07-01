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

	/* (non-Javadoc)
	 * @see com.soulgalore.servlet.thumbnail.ThumbnailCreator#createThumbnail(com.soulgalore.servlet.thumbnail.Thumbnail, java.lang.String, java.lang.String)
	 */
	@Override
	public void createThumbnail(Thumbnail thumbnail, String originalBaseDir,
			String destinationDir) throws InterruptedException, IOException {

		final ProcessBuilder pb = new ProcessBuilder("convert", "-thumbnail",
				thumbnail.getImageDimensions(), originalBaseDir
						+ File.separator
						+ thumbnail.getOriginalImageNameWithExtension(),
				destinationDir + File.separator
						+ thumbnail.getOriginalImageName() + "-"
						+ thumbnail.getImageDimensions()
						+ thumbnail.getImageFileExtension());

	pb.directory(new File(originalBaseDir));
		try {
			final Process p = pb.start();
			// wait until it's created
			p.waitFor();

		} catch (IOException e1) {
			throw e1;
		}
	}
}
