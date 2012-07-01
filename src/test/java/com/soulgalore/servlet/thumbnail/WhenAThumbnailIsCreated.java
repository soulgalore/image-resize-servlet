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

import static org.junit.Assert.*;

import static org.hamcrest.Matchers.is;
import org.junit.Test;

import com.soulgalore.servlet.thumbnail.Thumbnail;
import com.soulgalore.servlet.thumbnail.ThumbnailNameException;

public class WhenAThumbnailIsCreated {

	private final static String originalDir ="/src/test/resources/webapp/originals";
	private final static String thumbsDir ="src/test/resources/webapp/thumbs";
	
	
	@Test
	public void theGeneratedFilePathShouldNotBeNull()
			throws ThumbnailNameException {
		Thumbnail thumbnail = new Thumbnail("mySuperImage-120x29.png", originalDir, thumbsDir);
		assertNotNull(thumbnail.getGeneratedFilePath());
	}

	@Test
	public void thetImageDimensionsAreRight() throws ThumbnailNameException {

		String dimensions = "120x29";
		Thumbnail thumbnail = new Thumbnail("mySuperImage-" + dimensions
				+ ".png", originalDir, thumbsDir);
		assertThat(thumbnail.getImageDimensions(), is(dimensions));

		// test craze name
		thumbnail = new Thumbnail("my-Super_Im.ag._With_.png.----crazy-name-"
				+ dimensions + ".png", originalDir, thumbsDir);
		assertThat(thumbnail.getImageDimensions(), is(dimensions));
	}

	@Test
	public void theFileNameShouldBeRight() throws ThumbnailNameException {
		Thumbnail thumbnail = new Thumbnail("mySuperImage-120x29.png", originalDir, thumbsDir);
		assertThat(thumbnail.getImageFileName(), is("mySuperImage-120x29.png"));
	}

	@Test
	public void theOriginalNameShouldBeRight() throws ThumbnailNameException {
		Thumbnail thumbnail = new Thumbnail("mySuperImage-120x29.png", originalDir, thumbsDir);
		assertThat(thumbnail.getOriginalImageName(), is("mySuperImage"));

		thumbnail = new Thumbnail("-120x29-mySuperImage-120x29.png", originalDir, thumbsDir);
		assertThat(thumbnail.getOriginalImageName(), is("-120x29-mySuperImage"));

	}

	@Test
	public void theOriginalFullnameShouldBeRight()
			throws ThumbnailNameException {
		Thumbnail thumbnail = new Thumbnail("mySuperImage-120x29.png", originalDir, thumbsDir);
		assertThat(thumbnail.getOriginalImageNameWithExtension(),
				is("mySuperImage.png"));

		thumbnail = new Thumbnail("imagee-120x29-1267x98.jpg", originalDir, thumbsDir);
		assertThat(thumbnail.getOriginalImageNameWithExtension(),
				is("imagee-120x29.jpg"));

	}

	@Test
	public void theFileEndingShoukdBeRight() throws ThumbnailNameException {
		Thumbnail thumbnail = new Thumbnail("mySuperImage-120x29.png",originalDir, thumbsDir);
		assertThat(thumbnail.getImageFileExtension(), is(".png"));

		thumbnail = new Thumbnail("mySuper.gif.Image-120x29.jpg", originalDir, thumbsDir);
		assertThat(thumbnail.getImageFileExtension(), is(".jpg"));

	}

	@Test
	public void anExceptionShouldBeThrownIfTheNameIsNotValid() {

		try {
			new Thumbnail("mySuperImage-.png",originalDir, thumbsDir);
			fail("Missing dimensions should fail");
		} catch (ThumbnailNameException e) {
		}

		try {
			new Thumbnail("mySuperImage-120x29.", originalDir, thumbsDir);
			fail("Missing file extension should fail");
		} catch (ThumbnailNameException e) {
		}

		try {
			new Thumbnail("mySuperImage120x29.jpg", originalDir, thumbsDir);
			fail("Missing - should fail");
		} catch (ThumbnailNameException e) {
		}
		try {
			new Thumbnail("-120x29.jpg", originalDir, thumbsDir);
			fail("Missing name should fail");
		} catch (ThumbnailNameException e) {
		}
		try {
			new Thumbnail(null, originalDir, thumbsDir);
			fail("Missing name should fail");
		} catch (ThumbnailNameException e) {
		}

	}
	
	
}
