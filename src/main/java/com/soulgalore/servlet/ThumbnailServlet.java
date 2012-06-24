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
 * image <i>/SERVLET/?img=MY_ORIGINAL_IMAGE-120x94.png</i></li>
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
			System.out.println("Running " + this.getClass().getName()
					+ " without configured valid "
					+ "sizes, use the servlet init parameter "
					+ INIT_PARAMETER_VALID_SIZES + " to set it up");

		requestParameterName = config
				.getInitParameter(INIT_PARAMETER_IMG_REQUEST_PARAMETER);
		thumbsDir = config.getInitParameter(INIT_PARAMETER_THUMB_WEB_DIR);
		originalsDir = config.getInitParameter(INIT_PARAMETER_ORIGINAL_WEB_DIR);

		destinationBaseDir = getServletContext().getRealPath("/" + thumbsDir);

		originalBaseDir = getServletContext().getRealPath("/") + originalsDir;

		System.out.println(this.getClass().getName() + " as "
				+ config.getServletName()
				+ " configured with request parameter name:"
				+ requestParameterName + " origiginalDir:" + originalsDir
				+ " thumbDir:" + thumbsDir + " and valid sizes:"
				+ (sizes == null ? "" : sizes));
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		final String imageName = req.getParameter(requestParameterName);

		if (!isRequestValid(imageName, resp))
			return;

		final Thumbnail thumbnail = new Thumbnail(imageName);

		if (!isSizeValid(thumbnail, resp))
			return;

		final String generatedPath = thumbnail.getGeneratedFilePath();

		final File theImage = new File(originalBaseDir + generatedPath
				+ imageName);

		// does it exist?
		if (theImage.exists()) {
			// yes return it
			returnTheImage(req, resp, generatedPath + imageName);
			return;
		}

		final File originalFile = new File(originalBaseDir
				+ thumbnail.getOriginalImageNameWithExtension());

		if (!originalFile.exists()) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Requested non existing original image");
			return;
		}

		try {
			createThumbnail(thumbnail);
			returnTheImage(req, resp, generatedPath + imageName);
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

	private void createThumbnail(Thumbnail thumbnail)
			throws InterruptedException, IOException {

		setupThumbDirs(thumbnail);

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

	/**
	 * Validate a request. If the request isn't valid, this method will send a
	 * error on the response.
	 * 
	 * @param filename
	 * @param resp
	 * @return true if the request is valid.
	 * @throws IOException
	 */
	private boolean isRequestValid(String filename, HttpServletResponse resp)
			throws IOException {

		if (filename == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Missing parameter " + requestParameterName);
			return false;
		}

		// TODO add regexp to verify filename standard

		return true;
	}

	private boolean isSizeValid(Thumbnail thumbnail, HttpServletResponse resp)
			throws IOException {

		// skip validation if no sizes has been setup
		if (validSizes.isEmpty())
			return true;

		final String size = thumbnail.getImageDimensions();

		if (!validSizes.contains(size)) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Not a valid image size:" + size);
			return false;
		} else
			return true;

	}

	private void returnTheImage(HttpServletRequest req,
			HttpServletResponse resp, String pathToFile)
			throws ServletException, IOException {
		final RequestDispatcher rd = getServletContext().getRequestDispatcher(
				"/" + thumbsDir + pathToFile);
		rd.forward(req, resp);
	}

	private void setupThumbDirs(Thumbnail thumbnail) {

		final File dir = new File(destinationBaseDir
				+ thumbnail.getGeneratedFilePath());

		if (!dir.exists()) {
			if (!dir.mkdirs())
				System.err.println("Couldn't create dir:"
						+ dir.getAbsolutePath());
		}
	}

}
