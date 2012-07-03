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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpException;
import com.meterware.httpunit.WebRequest;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import com.soulgalore.servlet.thumbnail.Thumbnail;
import com.soulgalore.servlet.thumbnail.ThumbnailException;
import com.soulgalore.servlet.thumbnail.ThumbnailServlet;

public class WhenTheServletIsAccessed {

	private ServletRunner sr;

	private final static String originalDir = "src/test/resources/webapp/originals";
	private final static String thumbsDir = "src/test/resources/webapp/thumbs";

	@Before
	public void setup() throws IOException, SAXException {

		File webXml = new File("src/test/resources/webapp/WEB-INF/web.xml");
		sr = new ServletRunner(webXml);
		Hashtable<String, String> ht = new Hashtable<String, String>();
		ht.put("valid-sizes", "460x360,220x172,120x94,80x62,800x626");
		ht.put("thumbs-dir", thumbsDir);
		ht.put("originals-dir", originalDir);
		ht.put("image-request-parameter-name", "img");

		sr.registerServlet("thumbs", ThumbnailServlet.class.getName(), ht);
	}

	@Test
	public void wrongParameterShouldFail() throws SAXException, IOException {
		ServletUnitClient sc = sr.newClient();
		WebRequest request = new GetMethodWebRequest("http://localhost/thumbs");
		request.setParameter("wrongParam", "test-120x94.png");

		try {
			sc.getResponse(request);
			fail("Wrong parameter name should fail");
		} catch (HttpException e) {
			assertThat(e.getResponseCode(),
					is(HttpServletResponse.SC_BAD_REQUEST));
			assertThat(
					e.getResponseMessage(),
					is(Thumbnail.ERROR_MESSAGE_THUMBNAIL_NAME_IS_NOT_VALID));
		}
	}

	@Test
	public void wrongSizeShouldFail() throws SAXException, IOException {
		ServletUnitClient sc = sr.newClient();
		WebRequest request = new GetMethodWebRequest("http://localhost/thumbs");
		request.setParameter("img", "test-21220x941.png");

		try {
			sc.getResponse(request);
			fail("Wrong size should fail");
		} catch (HttpException e) {
			assertThat(e.getResponseCode(),
					is(HttpServletResponse.SC_BAD_REQUEST));
			assertThat(
					e.getResponseMessage(),
					is(Thumbnail.ERROR_MESSAGE_THUMBNAIL_SIZE_IS_NOT_VALID));
		}

	}

	@Test
	public void nonExistingOriginalImageShouldFail() throws SAXException,
			IOException {
		ServletUnitClient sc = sr.newClient();
		WebRequest request = new GetMethodWebRequest("http://localhost/thumbs");
		request.setParameter("img", "no-120x94.png");

		try {
			sc.getResponse(request);
			fail("Non existing original image should work");
		} catch (HttpException e) {
			assertThat(e.getResponseCode(),
					is(HttpServletResponse.SC_BAD_REQUEST));
			assertThat(
					e.getResponseMessage(),
					is(Thumbnail.ERROR_MESSAGE_ORIGINAL_IMAGE_DO_NOT_EXIST));
		}

	}
	
	
	@Test
	public void theOriginalImageShouldExis()
			throws IOException, ServletException, ThumbnailException {

		Set<String> validSizes = new HashSet<String>();
		validSizes.add("120x94");
		ThumbnailFactory getter = new ThumbnailFactory(originalDir, thumbsDir,
				validSizes);
		Thumbnail thumbnail = new Thumbnail("test-120x94.png", originalDir, thumbsDir);
		assertTrue("The orginal image should exist",
				getter.doTheOriginalImageExist(thumbnail));

	}

	/*
	 * TODO need to setup the last forward in configuration
	 * 
	 * @Test public void rightParametersShouldWork() throws SAXException,
	 * IOException { ServletUnitClient sc = sr.newClient(); WebRequest request =
	 * new GetMethodWebRequest("http://localhost/thumbs");
	 * request.setParameter("img", "test-120x94.png");
	 * 
	 * try { WebResponse wr = sc.getResponse(request);
	 * assertThat(wr.getResponseCode(), is(HttpServletResponse.SC_OK)); } catch
	 * (HttpException e) { fail("Right parameters should work:" +
	 * e.getResponseCode() + " " + e.getMessage()); } }
	 */

}
