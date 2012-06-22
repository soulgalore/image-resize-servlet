package com.soulgalore.servlet;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpException;
import com.meterware.httpunit.WebRequest;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

public class WhenThumbnailServletIsAccessed {

	private ServletRunner sr;

	@Before
	public void setup() {
		sr = new ServletRunner();
		Hashtable<String, String> ht = new Hashtable<String, String>();
		ht.put("valid-sizes", "460x360,220x172,120x94,80x62,800x626");
		ht.put("image-request-parameter-name","img");
		ht.put("thumbs-dir", "thumbs/");
		ht.put("originals-dir","originals/");
		sr.registerServlet("thumbs", ThumbnailServlet.class.getName(), ht);

	}

	@Test
	public void missingParameterShouldReturnBadRequest() throws IOException,
			SAXException {

		ServletUnitClient sc = sr.newClient();
		WebRequest request = new GetMethodWebRequest("http://localhost/thumbs");
		try {
			sc.getResponse(request);
			fail("");
		} catch (HttpException e) {

			assertThat(e.getResponseCode(),
					is(HttpServletResponse.SC_BAD_REQUEST));

		}

	}

	@Test
	public void wrongParameterShouldReturnBadRequest() throws IOException,
			SAXException {

		ServletUnitClient sc = sr.newClient();
		WebRequest request = new GetMethodWebRequest("http://localhost/thumbs");
		request.setParameter("img2", "monkeyboy-120x94.png");
		try {
			sc.getResponse(request);
			fail("");
		} catch (HttpException e) {
			assertThat(e.getResponseCode(),
					is(HttpServletResponse.SC_BAD_REQUEST));
		}

	}

}
