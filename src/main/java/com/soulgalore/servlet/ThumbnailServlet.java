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

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * Example of a image-magick resize image servlet, that can resize an image on
 * demand. Will resize an already existing original image to different
 * configured size(s).
 * 
 * User scenario:
 * </p>
 * <ol>
 * <li>Access the servlet with a name that holds the new size of the original
 * image <i>/SERVLET/?img=MY_ORIGINAL_IMAGE-120x94.png</i> (need to be of the 
 * format of {@link Thumbnail#MATCHING_NAME_REGEXP}</li>
 * <li>The servlet will check if the original image exist (named
 * MY_ORIGINAL_IMAGE.png in this example), meaning it is a valid request</li>
 * <li>if the new size is configured in {@link #validSizes}, the image can</li>
 * <li>The servlet will check if that size of the image already exist on disk,
 * if it exist, it will be returned.
 * <li>
 * <li>If the images don't exist, the size will be created using image-magick</li>
 * <li>The image is returned</li>
 * </ol>
 * 
 * <p>
 * The servlet <b>needs</b> to be configured by the following parameters:
 * <ul>
 * <li>{@link #INIT_PARAMETER_VALID_SIZES} that will configure the valid image
 * sizes. Configure by the format: <i>460x360,220x172,120x94</i></li>
 * <li>{@link #INIT_PARAMETER_ORIGINAL_WEB_DIR} that is the relative path to the
 * orignals web folder.</li>
 * <li>{@link #INIT_PARAMETER_THUMB_WEB_DIR} that is the base web dir for the
 * thumbnails.</li>
 * <li>{@link #INIT_PARAMETER_IMG_REQUEST_PARAMETER} that is the name of the
 * request parameter that will hold the value of the image that you want to
 * fetch.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * No cache header is added within the servlet. Note also that the imagemagick
 * needs to be in the path of the user that's starts the servlet runner.
 * </p>
 * 
 * 
 * 
 * 
 */
public class ThumbnailServlet extends HttpServlet {

	private static final long serialVersionUID = -2092311650388075782L;

	/**
	 * The name of the servlet init parameter for valid image sizes.
	 */
	private static final String INIT_PARAMETER_VALID_SIZES = "valid-sizes";

	/**
	 * The name of the servlet init parameter for the request parameter.
	 */
	private static final String INIT_PARAMETER_IMG_REQUEST_PARAMETER = "image-request-parameter-name";

	/**
	 * The name of the servlet init parameter for the base dir for thumbnail
	 * images.
	 */
	private static final String INIT_PARAMETER_THUMB_WEB_DIR = "thumbs-dir";

	/**
	 * The name of the servlet init parameter for where the original images are
	 * located.
	 */
	private static final String INIT_PARAMETER_ORIGINAL_WEB_DIR = "originals-dir";

	/**
	 * The valid sizes of an image. Fetched from the servlet init parameter
	 * {@link #INIT_PARAMETER_VALID_SIZES}. If the parameter is empty all sizes
	 * can be created.
	 */
	private Set<String> validSizes = new HashSet<String>();

	/**
	 * Web thumbnail directory.
	 */
	private String thumbsDir;
	/**
	 * Directory for originals.
	 */
	private String originalsDir;

	/**
	 * The name of the request parameter.
	 */
	private String requestParameterName;

	/**
	 * Where the original image will be located, need to be changed in a love
	 * environment.
	 */
	private String originalBaseDir;

	/**
	 * The base dir where the thumbnails will be created.
	 */
	private String destinationBaseDir;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		final String sizes = config
				.getInitParameter(INIT_PARAMETER_VALID_SIZES);

		if (sizes != null) {
			final StringTokenizer token = new StringTokenizer(sizes, ",");
			while (token.hasMoreTokens()) {
				validSizes.add(token.nextToken());
			}
		} else
			System.out.println("Running " + getServletName()
					+ " without configured valid "
					+ "sizes, use the servlet init parameter "
					+ INIT_PARAMETER_VALID_SIZES + " to set it up.");

		requestParameterName = config
				.getInitParameter(INIT_PARAMETER_IMG_REQUEST_PARAMETER);
		thumbsDir = config.getInitParameter(INIT_PARAMETER_THUMB_WEB_DIR);
		originalsDir = config.getInitParameter(INIT_PARAMETER_ORIGINAL_WEB_DIR);

		destinationBaseDir = getServletContext().getRealPath("/" + thumbsDir);

		originalBaseDir = getServletContext().getRealPath("/") + originalsDir;

		System.out.println(getServletName()
				+ " is configured with request parameter name:"
				+ requestParameterName + " origiginalDir:" + originalsDir
				+ " thumbDir:" + thumbsDir + " and valid sizes:"
				+ (sizes == null ? "" : sizes));
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Thumbnail thumbnail = null;

		try {
			thumbnail = new Thumbnail(req.getParameter(requestParameterName));
		} catch (ThumbnailNameException e1) {
			resp.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"Thumbnail name isn't valid");
			return;
		}

		if (!isSizeValid(thumbnail)) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Not a valid image size");
			return;
		}

		// do the thumbnail exist?
		if (doTheThumbnailExist(thumbnail)) {
			// yes return it
			returnTheThumbnail(req, resp, thumbnail);
			return;
		}

		// do the original image exist
		if (doTheOriginalImageExist(thumbnail)) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Requested non existing original image");
			return;
		}

		try {
			setupThumbDirs(thumbnail);

			ThumbnailCreator.getInstance().createThumbnail(thumbnail,
					originalBaseDir, destinationBaseDir);

			returnTheThumbnail(req, resp, thumbnail);
			return;
		} catch (IOException e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Couldn't create thumbnail");
			return;
		} catch (InterruptedException e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Couldn't create thumbnail");
			return;
		}

	}

	/**
	 * Check if the thumbnail already exists.
	 * 
	 * @param thumbnail the thumbnail
	 * @return true if the thumbnails exist
	 */
	protected boolean doTheThumbnailExist(Thumbnail thumbnail) {
		final File theImageThumbnail = new File(originalBaseDir
				+ thumbnail.getGeneratedFilePath()
				+ thumbnail.getImageFileName());

		return theImageThumbnail.exists();
	}

	/**
	 * Check if the original image exists.
	 * 
	 * @param thumbnail
	 *            the thumbnail
	 * @return true if it exists.
	 */
	protected boolean doTheOriginalImageExist(Thumbnail thumbnail) {
		final File originalFile = new File(originalBaseDir
				+ thumbnail.getOriginalImageNameWithExtension());
		return originalFile.exists();
	}

	/**
	 * Check if the size of the thumbnail is valid.
	 * 
	 * @param thumbnail
	 *            the thumbnail
	 * @return true if the size is valid.
	 */
	protected boolean isSizeValid(Thumbnail thumbnail) {

		// skip validation if no sizes has been setup
		if (validSizes.isEmpty())
			return true;
		else if (!validSizes.contains(thumbnail.getImageDimensions()))
			return false;
		return true;

	}

	/**
	 * Return the thumbnail to the user.
	 * 
	 * @param req
	 *            the request
	 * @param resp
	 *            the response
	 * @param thumbnail 
	 * 			  the thumbnail that will be returned
	 * @throws ServletException
	 *             if the thumbnail couldn't be delivered
	 * @throws IOException
	 *             if the thumbnail couldn't be delivered
	 */
	protected void returnTheThumbnail(HttpServletRequest req,
			HttpServletResponse resp, Thumbnail thumbnail)
			throws ServletException, IOException {
		final RequestDispatcher rd = getServletContext().getRequestDispatcher(
				"/" + thumbsDir + thumbnail.getGeneratedFilePath()
						+ thumbnail.getImageFileName());
		rd.forward(req, resp);
	}

	/**
	 * Setup the thumbnail dir if it doesn't exist.
	 * 
	 * @param thumbnail
	 *            the thumbnail
	 * @return the dir           
	 */
	protected File setupThumbDirs(Thumbnail thumbnail) {

		final File dir = new File(destinationBaseDir
				+ thumbnail.getGeneratedFilePath());

		// bad error handling
		if (!dir.exists()) {
			if (!dir.mkdirs())
				System.err.println("Couldn't create dir:"
						+ dir.getAbsolutePath());
		}
		
		return dir;
	}

}
