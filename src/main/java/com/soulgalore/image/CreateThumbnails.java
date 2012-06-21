package com.soulgalore.image;

import java.io.File;
import java.io.IOException;


public class CreateThumbnails {

	private static final String[] SIZES = {"460x360", "220x172", "120x94", "80x62", "800x626"};		
	
	public static void main(String[] args) {
		String fileName = "original.png";
		
		CreateThumbnails test = new CreateThumbnails();
		// read from conf
		String where = test.createThumbnails(fileName, "/Users/ph/newest/image/src/main/java/com/soulgalore/image", "/Users/ph/newest/image/src/main/java/com/soulgalore/image");
    
		System.out.println("My path is:" + where);
	}
	
	
	public String createThumbnails(String fileName, String originalDir, String destinationPath) {
		String fileNameBeforeDot = fileName.substring(0, fileName.lastIndexOf("."));
		String fileEnding = fileName.substring(fileName.lastIndexOf("."), fileName.length());
		
		
		String path = getFilePath(fileName);

		String destinationDir = destinationPath + getFilePath(fileName);
		
		File dir = new File(destinationDir);
		if (!dir.exists())
			dir.mkdirs();
		
		// Move the original image, so we have it if it's needed later
		File originalFile = new File(originalDir + File.separator + fileName);
		File originalFileWithNewPath = new File(destinationDir + fileName);
		originalFile.renameTo(originalFileWithNewPath);
		
		
		for (String size : SIZES) {	
			ProcessBuilder pb = new ProcessBuilder("convert","-thumbnail", size, originalFileWithNewPath.getName(), destinationDir + fileNameBeforeDot + "-"+size + fileEnding);
	        pb.directory(new File(destinationDir));
	        try {
				Process p = pb.start();
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		
		}
		
		return path;
	}
	
	
	private String getFilePath(String fileName) {
		
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
