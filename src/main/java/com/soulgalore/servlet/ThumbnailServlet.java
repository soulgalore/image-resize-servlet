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

import com.google.common.collect.ImmutableSet;

/**
 * Example of a imagemagick convert servlet. Will convert an already existing
 * image to a configured size, accessed size.
 * 
 * User scenario:
 * <ol>
 * <li>Access the servlet with a specified name:
 * /SERVLET/?img=MY_ORIGINAL_IMAGE-120x94.png</li>
 * <li>The servlet will check if the original image exist (named
 * MY_ORIGINAL_IMAGE.png)</li>
 * <li>The servet will check if that size of the image already exist on disk, if
 * it exist, it will be returned
 * <li>
 * <li>If the images don't exist, the size will be created using imagemagick if
 * the size is configured as a {@link #validSizes}.</li>
 * <li>The image is returned</li>
 * </ul> No cache header is added within the servlet. Note also that the
 * imagemagick needs to be in the path of the user that's starts the servlet
 * runner.
 * 
 * 
 * 
 * 
 */
public class ThumbnailServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2092311650388075782L;

	/**
	 * The valid sizes of an image. Use them to make sure the servlet can't be
	 * missused.
	 */
	private  Set<String> validSizes = new HashSet<String>();

	/**
	 * The name of the request parameter.
	 */
	private static final String IMG_REQUEST_PARAMETER = "img";

	/**
	 * The web base dir of all thumbnails.
	 */
	private static final String THUMB_WEB_DIR = "thumbs/";

	/**
	 * The web base dir of all original images.
	 */
	private static final String ORIGINAL_WEB_DIR = "originals/";

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

		String sizes = config.getInitParameter("valid-sizes");
		
		StringTokenizer token = new StringTokenizer(sizes,",");
		while (token.hasMoreTokens()) {
			validSizes.add(token.nextToken());	
		}
		
		destinationBaseDir = getServletContext().getRealPath(
				"/" + THUMB_WEB_DIR);

		originalBaseDir = getServletContext().getRealPath("/")
				+ ORIGINAL_WEB_DIR;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// fetch filename
		String filename = req.getParameter(IMG_REQUEST_PARAMETER);

		if (!isRequestValid(filename, resp))
			return;

		String generatedPath = getGeneratedFilePath(filename);

		File file = new File(originalBaseDir + generatedPath + filename);

		// does it exist?
		if (file.exists()) {
			// yes return it
			returnTheFile(req, resp, generatedPath + filename);
			return;
		}

		try {
			createThumbnail(filename);
			returnTheFile(req, resp, generatedPath + filename);
			return;
		} catch (Exception e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Couldn't create thumbnail");
			return;
		}

	}

	private boolean isRequestValid(String filename, HttpServletResponse resp)
			throws IOException {

		if (filename == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Missing parameter " + IMG_REQUEST_PARAMETER);
			return false;
		}

		String size = getAskedFileSize(filename);

		if (!validSizes.contains(size)) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Not a valid image size");
			return false;
		} else
			// TODO add regexp to verify filename standard
			return true;
	}

	private void returnTheFile(HttpServletRequest req,
			HttpServletResponse resp, String pathToFile)
			throws ServletException, IOException {
		RequestDispatcher rd = getServletContext().getRequestDispatcher(
				"/" + THUMB_WEB_DIR + pathToFile);
		rd.forward(req, resp);
	}

	private void createThumbnail(String filename) throws InterruptedException,
			IOException {

		File dir = new File(destinationBaseDir + getGeneratedFilePath(filename));

		if (!dir.exists())
			dir.mkdirs();

		String originalName = getOriginalFileName(filename);

		ProcessBuilder pb = new ProcessBuilder("convert", "-thumbnail",
				getAskedFileSize(filename), originalBaseDir + originalName
						+ getFileEnding(filename), destinationBaseDir
						+ getGeneratedFilePath(filename) + originalName + "-"
						+ getAskedFileSize(filename) + getFileEnding(filename));

		pb.directory(new File(originalBaseDir));
		try {
			Process p = pb.start();
			// wait until it created
			p.waitFor();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw e1;
		}
	}

	private String getOriginalFileName(String filename) {
		return filename.substring(0, filename.lastIndexOf("-"));
	}

	private String getFileEnding(String filename) {
		return filename.substring(filename.lastIndexOf("."), filename.length());
	}

	private String getAskedFileSize(String filename) {
		return filename.substring(filename.lastIndexOf("-") + 1,
				filename.lastIndexOf("."));
	}

	private String getGeneratedFilePath(String fileName) {

		int hashcode = fileName.hashCode();
		
		StringBuilder path = new StringBuilder(File.separator);
		// first dir
		path.append(String.format("%03d", hashcode & 255));
		path.append(File.separator);
		// second dir
		path.append(String.format("%03d", (hashcode >> 8) & 255));
		path.append(File.separator);

		return path.toString();
	}

}
