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
import java.util.concurrent.Callable;

import net.coobird.thumbnailator.Thumbnails;

/**
 * Backend using Thumbnailator. http://code.google.com/p/thumbnailator/
 * 
 */
public class ThumbnailatorThumbnailCreator implements Callable<File> {

	private final Thumbnail thumbnail;

	public ThumbnailatorThumbnailCreator(Thumbnail thumb) {
		thumbnail = thumb;
	}

	@Override
	public File call() throws Exception {

		final int x = Integer.valueOf(thumbnail.getImageDimensions().substring(
				0, thumbnail.getImageDimensions().indexOf("x")));
		final int y = Integer.valueOf(thumbnail.getImageDimensions().substring(
				thumbnail.getImageDimensions().indexOf("x") + 1,
				thumbnail.getImageDimensions().length()));

		try {
			Thumbnails
					.of(thumbnail.getOriginalBaseDir() + File.separator
							+ thumbnail.getOriginalImageNameWithExtension())
					.size(x, y)
					.toFile(thumbnail.getDestinationDir() + File.separator
							+ thumbnail.getImageFileName());
		} catch (IOException e) {
			throw e;
		}

		return new File(thumbnail.getDestinationDir() + File.separator
				+ thumbnail.getImageFileName());

	}

}
