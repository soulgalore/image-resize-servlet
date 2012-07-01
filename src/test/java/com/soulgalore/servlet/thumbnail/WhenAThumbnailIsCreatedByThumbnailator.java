package com.soulgalore.servlet.thumbnail;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;

public class WhenAThumbnailIsCreatedByThumbnailator {

	@Test
	public void theFileShouldBeCreated() throws ThumbnailNameException, InterruptedException, IOException {
		ThumbnailCreator creator = new ThumbnailatorThumbnailCreator();
		URL url = this.getClass().getResource("/webapp/originals/");
		File testPng = new File(url.getFile());
		String originalsDir = testPng.getPath();
		String thumbName = "test-120x94.png";
		Thumbnail thumb = new Thumbnail(thumbName);
		creator.createThumbnail(thumb, originalsDir, originalsDir);
		File destFile = new File(originalsDir + File.separator + thumbName);
		assertTrue("The thumbnail doesn't exist:" + destFile.getAbsolutePath(), destFile.exists());
	}

}
