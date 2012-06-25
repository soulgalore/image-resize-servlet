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
package com.soulgalore.servlet;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Hashtable;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

public class WhenTheServletIsAccessed {

	private ServletRunner sr;

	@Before
	public void setup() throws IOException, SAXException {
		
		File webXml = new File("src/test/resources/WEB-INF/web.xml");
		sr = new ServletRunner(webXml);
		Hashtable<String, String> ht = new Hashtable<String, String>();
		ht.put("valid-sizes", "460x360,220x172,120x94,80x62,800x626");
		ht.put("thumbs-dir", "thumbs/");
		ht.put("originals-dir", "originals/");
		ht.put("image-request-parameter-name", "img");

		sr.registerServlet("thumbs", ThumbnailServlet.class.getName(), ht);
	}
	

	@Test
	public void theThumbnailDirShouldBeCreated() throws MalformedURLException,
			IOException, ServletException, ThumbnailNameException {

		ServletUnitClient sc = sr.newClient();
		WebRequest request = new GetMethodWebRequest("http://localhost/thumbs");
		request.setParameter("img", "mySuperImage-120x94.png");

		InvocationContext ic = sc.newInvocation(request);
		ThumbnailServlet ts = (ThumbnailServlet) ic.getServlet();
		
		// relies on that the dir don't exist, hmm
		Thumbnail thumbnail = new Thumbnail("mySuperImage-120x94.png");
		File dir = ts.setupThumbDirs(thumbnail);
		
		if (dir.exists())
			dir.delete();
		else
			fail("Couldn't create dir:" + dir.getAbsolutePath());
	}

	@Test
	public void theImageSizeShouldBeTested() throws MalformedURLException,
			IOException, ServletException, ThumbnailNameException {

		ServletUnitClient sc = sr.newClient();
		WebRequest request = new GetMethodWebRequest("http://localhost/thumbs");
		request.setParameter("img", "mySuperImage-120x94.png");

		InvocationContext ic = sc.newInvocation(request);
		ThumbnailServlet ts = (ThumbnailServlet) ic.getServlet();

		Thumbnail validThumbNail = new Thumbnail("mySuperImage-120x94.png");
		assertTrue(ts.isSizeValid(validThumbNail));

		Thumbnail invalidThumbNail = new Thumbnail("mySuperImage-120x941.png");
		assertFalse(ts.isSizeValid(invalidThumbNail));

	}
	
	@Test
	public void theOriginalImageShouldExistsAndTheThumbnailShouldNot() throws IOException,
			ServletException, ThumbnailNameException {

		ServletUnitClient sc = sr.newClient();
		WebRequest request = new GetMethodWebRequest("http://localhost/thumbs");
		request.setParameter("img", "test-120x94.png");

		InvocationContext ic = sc.newInvocation(request);
		ThumbnailServlet ts = (ThumbnailServlet) ic.getServlet();
		Thumbnail thumbnail = new Thumbnail("test-120x94.png");
		assertTrue(ts.doTheOriginalImageExist(thumbnail));
		assertFalse(ts.doTheThumbnailExist(thumbnail));
	
	}
	
	

}
