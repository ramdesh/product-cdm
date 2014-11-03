/*
 * *
 * * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights
 * Reserved.
 * *
 * * Licensed under the Apache License, Version 2.0 (the "License");
 * * you may not use this file except in compliance with the License.
 * * You may obtain a copy of the License at
 * *
 * * http://www.apache.org/licenses/LICENSE-2.0
 * *
 * * Unless required by applicable law or agreed to in writing, software
 * * distributed under the License is distributed on an "AS IS" BASIS,
 * * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * * See the License for the specific language governing permissions and
 * * limitations under the License.
 */
package org.wso2.emm.apkgenerator.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.bouncycastle.openssl.PEMWriter;
import org.wso2.emm.apkgenerator.generators.CertificateGenerationException;

/**
 * Common file operations such as read ,write PEM files and zip file creation
 * are handled by this class
 * 
 */
public class FileOperator {

	private static Logger log = Logger.getLogger(FileOperator.class);

	/**
	 * Copy file from the source path to a destination
	 * 
	 * @param source
	 *            source file path
	 * @param destination
	 *            destination file path
	 * @throws CertificateGenerationException
	 */
	public static void copyFile(String source, String destination)
			throws CertificateGenerationException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(new File(source));
			output = new FileOutputStream(new File(destination));
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} catch (FileNotFoundException e) {
			log.error(
					"cannot find one of the files, while trying to copy file :"
							+ source + "\n to its destination: " + destination,
					e);
			throw new CertificateGenerationException(
					"cannot find one of the files, while trying to copy file :"
							+ source + "\n to its destination: " + destination,
					e);
		} catch (IOException e) {
			log.error(
					"Error opening/working with file, while trying to copy file :"
							+ source + "\n to its destination: " + destination,
					e);
			throw new CertificateGenerationException(
					"Error opening/working with file, while trying to copy file :"
							+ source + "\n to its destination: " + destination,
					e);
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				log.error(
						"Error in closing file Stream , while trying to copy file "
								+ source + "\n to its destination: "
								+ destination, e);
				throw new CertificateGenerationException(
						"Error in closing file Stream , while trying to copy file "
								+ source + "\n to its destination: "
								+ destination, e);
			}
			try {
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				log.error(
						"Error in closing file Stream , while trying to copy file "
								+ source + "\n to its destination: "
								+ destination, e);
				throw new CertificateGenerationException(
						"Error in closing file Stream , while trying to copy file "
								+ source + "\n to its destination: "
								+ destination, e);
			}

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
	public static String readFile(String path)
			throws CertificateGenerationException {
		Scanner scanner;
		try {
			scanner = new Scanner(new File(path));
		} catch (FileNotFoundException e) {
			log.error("Error reading file " + path + " ," + e.getMessage(), e);
			throw new CertificateGenerationException("Error reading file "
					+ path + " ," + e.getMessage(), e);
		}
		String fileContent = scanner.useDelimiter("\\Z").next();
		scanner.close();
		return fileContent;
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
	public static void fileWrite(String path, String content)
			throws CertificateGenerationException {
		PrintWriter out;
		try {
			out = new PrintWriter(path);
		} catch (FileNotFoundException e) {
			log.error("Error writing to file " + path + " ," + e.getMessage(),
					e);
			throw new CertificateGenerationException("Error writing to file "
					+ path + " ," + e.getMessage(), e);
		}
		out.println(content);
		out.close();
	}

	/**
	 * Creates a zip file from a set of files provided
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
		FileOutputStream fout;
		try {
			fout = new FileOutputStream(zipFilePath);
		} catch (FileNotFoundException e) {
			log.error(
					"Error opening file " + zipFilePath + " ," + e.getMessage(),
					e);
			throw new CertificateGenerationException("Error opening file "
					+ zipFilePath + " ," + e.getMessage(), e);
		}
		ZipOutputStream zout = new ZipOutputStream(fout);
		for (int x = 0; x < files.length; x++) {
			File f = new File(files[x]);
			FileInputStream in;
			try {
				in = new FileInputStream(files[x]);
				zout.putNextEntry(new ZipEntry(f.getName()));
			} catch (FileNotFoundException e) {
				log.error(
						"Cannot open the file ," + files[x] + ", "
								+ e.getMessage(), e);
				throw new CertificateGenerationException(
						"Cannot open the file ," + files[x] + ", "
								+ e.getMessage(), e);
			} catch (IOException e) {
				log.error(
						"Error adding " + files[x] + "to zip , "
								+ e.getMessage(), e);
				throw new CertificateGenerationException("Error adding "
						+ files[x] + "to zip , " + e.getMessage(), e);
			}

			byte[] b = new byte[1024];
			int count;
			try {
				while ((count = in.read(b)) > 0) {
					zout.write(b, 0, count);
				}
				in.close();
			} catch (IOException e) {
				log.error("file error while closing the file, " + files[x]
						+ ", " + e.getMessage(), e);
				throw new CertificateGenerationException(
						"file error while closing the file, " + files[x] + ", "
								+ e.getMessage(), e);
			}
		}
		try {
			zout.close();
		} catch (IOException e) {
			log.error("file error while closing the file, " + zipFilePath
					+ ", " + e.getMessage(), e);
			throw new CertificateGenerationException(
					"file error while closing the file, " + zipFilePath + ", "
							+ e.getMessage(), e);
		}
	}

	/**
	 * Write a passed PEM file object to a given path
	 * 
	 * @param path
	 *            of the file to be saved
	 * @param file
	 *            object that needs to be saved
	 */
	public static void writePem(String path, Object file)
			throws CertificateGenerationException {
		FileOutputStream fos3;
		try {
			fos3 = new FileOutputStream(path);
			PEMWriter pemWriter3 = new PEMWriter(new PrintWriter(fos3));
			pemWriter3.writeObject(file);
			pemWriter3.flush();
			pemWriter3.close();
		} catch (IOException e) {
			log.error("Error writing file to :" + path + ", " + e.getMessage(),
					e);
			throw new CertificateGenerationException("Error writing file to :"
					+ path + ", " + e.getMessage(), e);
		}
	}

	/**
	 * Get a file input stream when the file name is provided
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
			log.error(
					"Cannot open the file ," + sourceFile + ", "
							+ e.getMessage(), e);
			throw new CertificateGenerationException("Cannot open the file ,"
					+ sourceFile + ", " + e.getMessage(), e);
		}
	}

	/**
	 * Generates a new folder if it doesn't exist when the path is given
	 * 
	 * @param path
	 *            the folder path that needs to be created
	 * @throws CertificateGenerationException
	 */
	public static void makeFolder(String path)
			throws CertificateGenerationException {
		try {
			new File(path).mkdirs();
		} catch (Exception e) {
			log.error(
					"Error when creating directory " + path + " ,"
							+ e.getMessage(), e);
			throw new CertificateGenerationException(
					"Error when creating directory " + path + " ,"
							+ e.getMessage(), e);
		}
	}

	/**
	 * convert the path sent to a platform specific path
	 * 
	 * @param path
	 *            of the file/folder
	 * @return The platform specific path
	 * @throws CertificateGenerationException
	 */
	public static String getPath(String path)
			throws CertificateGenerationException {
		try {
			return path.replaceAll("/",
					Matcher.quoteReplacement(File.separator));
		} catch (Exception e) {
			log.error("Error when getting file path:" + path, e);
			throw new CertificateGenerationException(
					"Error when getting file path:" + path, e);
		}
	}
}

