package com.soulgalore.servlet.thumbnail;

import java.io.File;
import java.io.IOException;

import net.coobird.thumbnailator.Thumbnails;
/**
 * Backend using Thumbnailator.
 * http://code.google.com/p/thumbnailator/
 *
 */
public class ThumbnailatorThumbnailCreator implements ThumbnailCreator {

	@Override
	public void createThumbnail(Thumbnail thumbnail, String originalBaseDir,
			String destinationDir) throws InterruptedException, IOException {
	
		final int x = Integer.valueOf(thumbnail.getImageDimensions().substring(
				0, thumbnail.getImageDimensions().indexOf("x")));
		final int y = Integer.valueOf(thumbnail.getImageDimensions().substring(
				thumbnail.getImageDimensions().indexOf("x") + 1,
				thumbnail.getImageDimensions().length()));
	
		Thumbnails
				.of(originalBaseDir + File.separator
						+ thumbnail.getOriginalImageNameWithExtension())
				.size(x, y)
				.toFile(destinationDir + File.separator
						+ thumbnail.getImageFileName());				
			

	}

}
