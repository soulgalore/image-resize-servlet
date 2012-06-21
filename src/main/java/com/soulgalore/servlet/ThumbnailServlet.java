package com.soulgalore.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableSet;

public class ThumbnailServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2092311650388075782L;

	/**
	 * The valid sizes of an image. Use them to make sure the servlet can't be
	 * missused.
	 */
	private static final ImmutableSet<String> VALID_SIZES = new ImmutableSet.Builder<String>()
			.add("460x360", "220x172", "120x94", "80x62", "800x626").build();

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

		if (!VALID_SIZES.contains(size)) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Not a valid image size");
			return false;
		} else
		// TODO add regexp to verify filename standard
		return true;
	}

	private void returnTheFile(HttpServletRequest req,
			HttpServletResponse resp, String pathToFile) throws ServletException,
			IOException {
		RequestDispatcher rd = getServletContext().getRequestDispatcher(
				"/"+ THUMB_WEB_DIR + pathToFile);
		rd.forward(req, resp);
	}

	private void createThumbnail(String filename) throws InterruptedException, IOException {

		File dir = new File(destinationBaseDir
				+ getGeneratedFilePath(filename));

		if (!dir.exists())
			dir.mkdirs();

		String originalName = getOriginalFileName(filename);
		
		ProcessBuilder pb = new ProcessBuilder("convert", "-thumbnail",
				getAskedFileSize(filename), originalBaseDir + originalName + getFileEnding(filename),
				destinationBaseDir + getGeneratedFilePath(filename)
						+ originalName + "-" + getAskedFileSize(filename)
						+ getFileEnding(filename));
		
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
		int mask = 255;
		int firstDir = hashcode & mask;
		int secondDir = (hashcode >> 8) & mask;

		StringBuilder path = new StringBuilder(File.separator);
		path.append(String.format("%03d", firstDir));
		path.append(File.separator);
		path.append(String.format("%03d", secondDir));
		path.append(File.separator);

		return path.toString();
	}

}
