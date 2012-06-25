/******************************************************
 * Imagemagick resize example servlet
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
package com.soulgalore.servlet;

import java.io.File;
import java.io.IOException;

class ThumbnailCreator {

	private static final ThumbnailCreator INSTANCE = new ThumbnailCreator();


	private ThumbnailCreator() {

	}

	/**
	 * Get the instance.
	 * 
	 * @return the singleton instance.
	 */
	public static ThumbnailCreator getInstance() {
		return INSTANCE;
	}

	
	/**
	 * Create a resized thumbnail image from a original image, using imagemagick.
	 * @param thumbnail 
	 * @param originalBaseDir
	 * @param destinationBaseDir
	 * @throws InterruptedException
	 * @throws IOException
	 */
	void createThumbnail(Thumbnail thumbnail, String originalBaseDir, String destinationBaseDir)
			throws InterruptedException, IOException {


		final ProcessBuilder pb = new ProcessBuilder("convert", "-thumbnail",
				thumbnail.getImageDimensions(), originalBaseDir
						+ thumbnail.getOriginalImageNameWithExtension(),
				destinationBaseDir + thumbnail.getGeneratedFilePath()
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
