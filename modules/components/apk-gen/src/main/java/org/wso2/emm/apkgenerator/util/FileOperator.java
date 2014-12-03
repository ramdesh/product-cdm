/**
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.emm.apkgenerator.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.log4j.Logger;
import org.bouncycastle.openssl.PEMWriter;
import org.codehaus.plexus.util.FileUtils;
import org.wso2.emm.apkgenerator.generators.CertificateGenerationException;

/**
 * Common file operations such as read, write PEM files and .zip file creation
 * are handled by this class. These methods are added to improve reusability of
 * commonly used file operations.
 */
public class FileOperator {

	private static Logger log = Logger.getLogger(FileOperator.class);

	/**
	 * Copy file from the source path to a destination.
	 * 
	 * @param source
	 *            source file path
	 * @param destination
	 *            destination file path
	 * @throws CertificateGenerationException
	 */
	public static void copyFile(String source, String destination)
	                                                              throws CertificateGenerationException {
		try {
			FileUtils.copyFile(new File(source), new File(destination));
		} catch (IOException e) {
			log.error("Cannot find one of the files, while trying to copy file :" + source +
			          ",  to its destination: " + destination + " ," + e.getMessage(), e);
			throw new CertificateGenerationException(
			                                         "Cannot find one of the files, while trying to copy file :" +
			                                                 source +
			                                                 ",  to its destination: " +
			                                                 destination + " ," + e.getMessage(), e);
		}
	}

	/**
	 * Read a file and returns its content as a {@link String}
	 * 
	 * @param path
	 *            of the file to be read.
	 * @return the content of the file
	 * @throws FileNotFoundException
	 */
	public static String readFile(String path) throws CertificateGenerationException {
		try {
			return FileUtils.fileRead(new File(path));
		} catch (IOException e) {
			log.error("Error reading file " + path + " ," + e.getMessage(), e);
			throw new CertificateGenerationException("Error reading file " + path + " ," +
			                                         e.getMessage(), e);
		}
	}

	/**
	 * Write a PEM content to a physical PEM file
	 * 
	 * @param path
	 *            the destination file path
	 * @param content
	 *            of the PEM file
	 * @throws FileNotFoundException
	 */
	public static void fileWrite(String path, String content) throws CertificateGenerationException {
		try {
			FileUtils.fileWrite(path, content);
		} catch (IOException e) {
			log.error("Error writing to file " + path + " ," + e.getMessage(), e);
			throw new CertificateGenerationException("Error writing to file " + path + " ," +
			                                         e.getMessage(), e);
		}
	}

	/**
	 * Creates a zip file from a list of files provided.
	 * 
	 * @param zipFilePath
	 *            the path of the final zip file to be created
	 * @param files
	 *            An array of file paths that needs to be added to the zip
	 * @throws CertificateGenerationException
	 * @throws IOException
	 */
	public static void createZip(String zipFilePath, String[] files)
	                                                                throws CertificateGenerationException {
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(zipFilePath);
		} catch (FileNotFoundException e) {
			log.error("Error opening file " + zipFilePath + " ," + e.getMessage(), e);
			throw new CertificateGenerationException("Error opening file " + zipFilePath + " ," +
			                                         e.getMessage(), e);
		}
		ZipOutputStream zipOutStream = new ZipOutputStream(fileOut);
		for (int x = 0; x < files.length; x++) {
			File file = new File(files[x]);
			FileInputStream inputStream;
			try {
				inputStream = new FileInputStream(files[x]);
				zipOutStream.putNextEntry(new ZipEntry(file.getName()));
			} catch (FileNotFoundException e) {
				log.error("Cannot open the file ," + files[x] + ", " + e.getMessage(), e);
				throw new CertificateGenerationException("Cannot open the file ," + files[x] +
				                                         ", " + e.getMessage(), e);
			} catch (IOException e) {
				log.error("Error adding " + files[x] + "to zip , " + e.getMessage(), e);
				throw new CertificateGenerationException("Error adding " + files[x] + "to zip , " +
				                                         e.getMessage(), e);
			}

			byte[] bytes = new byte[1024];
			int count;
			try {
				while ((count = inputStream.read(bytes)) > 0) {
					zipOutStream.write(bytes, 0, count);
				}
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				log.error("File error while closing the file, " + files[x] + ", " + e.getMessage(),
				          e);
				throw new CertificateGenerationException("File error while closing the file, " +
				                                         files[x] + ", " + e.getMessage(), e);
			}
		}
		try {
			if (zipOutStream != null) {
				zipOutStream.close();
			}
		} catch (IOException e) {
			log.error("File error while closing the file, " + zipFilePath + ", " + e.getMessage(),
			          e);
			throw new CertificateGenerationException("File error while closing the file, " +
			                                         zipFilePath + ", " + e.getMessage(), e);
		}
	}

	/**
	 * Write a passed PEM file object to a given path.
	 * 
	 * @param path
	 *            of the file to be saved
	 * @param file
	 *            object that needs to be saved
	 */
	public static void writePem(String path, Object file) throws CertificateGenerationException {
		FileOutputStream fileOutput = null;
		try {
			fileOutput = new FileOutputStream(path);
			PEMWriter pemWriter3 = new PEMWriter(new PrintWriter(fileOutput));
			pemWriter3.writeObject(file);
			pemWriter3.flush();
			pemWriter3.close();
		} catch (IOException e) {
			log.error("Error writing file to :" + path + ", " + e.getMessage(), e);
			throw new CertificateGenerationException("Error writing file to :" + path + ", " +
			                                         e.getMessage(), e);
		} finally {
			if (fileOutput != null) {
				try {
					fileOutput.close();
				} catch (IOException e) {
					log.error("File error while closing the file, " + path + ", " + e.getMessage(),
					          e);
					throw new CertificateGenerationException("File error while closing the file, " +
					                                         path + ", " + e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Get a file input stream when the file name is provided.
	 * 
	 * @param sourceFile
	 *            Name of the source file
	 * @return the file input stream
	 * @throws CertificateGenerationException
	 */
	public static FileInputStream getFileInputStream(String sourceFile)
	                                                                   throws CertificateGenerationException {
		try {
			return new FileInputStream(sourceFile);
		} catch (FileNotFoundException e) {
			log.error("Cannot open the file ," + sourceFile + ", " + e.getMessage(), e);
			throw new CertificateGenerationException("Cannot open the file ," + sourceFile + ", " +
			                                         e.getMessage(), e);
		}
	}

	/**
	 * Generates a new folder if it doesn't exist when the path is given.
	 * 
	 * @param path
	 *            the folder path that needs to be created
	 * @throws CertificateGenerationException
	 */
	public static void makeFolder(String path) throws CertificateGenerationException {
		try {
			new File(path).mkdirs();
		} catch (SecurityException e) {
			log.error("Error when creating directory " + path + " ," + e.getMessage(), e);
			throw new CertificateGenerationException("Error when creating directory " + path +
			                                         " ," + e.getMessage(), e);
		}
	}

	/**
	 * Convert the path sent to a platform specific path.
	 * 
	 * @param path
	 *            of the file/folder
	 * @return The platform specific path
	 * @throws CertificateGenerationException
	 */
	public static String getPath(String path) throws CertificateGenerationException {
		try {
			return path.replaceAll("/", Matcher.quoteReplacement(File.separator));
		} catch (Exception e) {
			log.error("Error when getting file path:" + path, e);
			throw new CertificateGenerationException("Error when getting file path:" + path, e);
		}
	}
}
