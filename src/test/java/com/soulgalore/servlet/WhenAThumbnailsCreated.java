package com.soulgalore.servlet;

import static org.junit.Assert.*;

import static org.hamcrest.Matchers.is;
import org.junit.Test;

public class WhenAThumbnailsCreated {

	@Test
	public void theGeneratedFilePathShouldNotBeNull() {
		Thumbnail thumbnail = new Thumbnail("mySuperImage-120x29.png");
		assertNotNull(thumbnail.getGeneratedFilePath());
	}

	@Test
	public void thetImageDimensionsAreRight() {

		String dimensions = "120x29";
		Thumbnail thumbnail = new Thumbnail("mySuperImage-" + dimensions
				+ ".png");
		assertThat(thumbnail.getImageDimensions(), is(dimensions));

		thumbnail = new Thumbnail("my-Super_Im.ag._With_.png.----crazy-name-"
				+ dimensions + ".png");
		assertThat(thumbnail.getImageDimensions(), is(dimensions));
	}

	@Test
	public void theFileNameShouldBeRight() {
		Thumbnail thumbnail = new Thumbnail("mySuperImage-120x29.png");
		assertThat(thumbnail.getOriginalImageName(),
				is("mySuperImage-120x29.png"));
	}

	@Test
	public void theOriginalNameShouldBeRight() {
		Thumbnail thumbnail = new Thumbnail("mySuperImage-120x29.png");
		assertThat(thumbnail.getOriginalImageName(), is("mySuperImage"));

		thumbnail = new Thumbnail("-120x29-mySuperImage-120x29.png");
		assertThat(thumbnail.getOriginalImageName(), is("-120x29-mySuperImage"));

	}

	@Test
	public void theOriginalFullnameShouldBeRght() {
		Thumbnail thumbnail = new Thumbnail("mySuperImage-120x29.png");
		assertThat(thumbnail.getOriginalImageNameWithExtension(),
				is("mySuperImage.png"));

		thumbnail = new Thumbnail("imagee-120x29-1267x98.jpg");
		assertThat(thumbnail.getOriginalImageNameWithExtension(), is("imagee.png"));

	}

	@Test
	public void theFileEndingShoukdBeRight() {
		Thumbnail thumbnail = new Thumbnail("mySuperImage-120x29.png");
		assertThat(thumbnail.getImageFileExtension(), is(".png"));

		thumbnail = new Thumbnail("mySuper.gif.Image-120x29.jpg");
		assertThat(thumbnail.getImageFileExtension(), is(".jpg"));

	}

}
