package org.wso2.apkgenerator.util;

import java.io.ByteArrayOutputStream;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.openssl.PEMWriter;
import org.wso2.apkgenerator.data.ObjectReader;

/*
 * Common file operations are handled by this class
 */
public class FileOperator {
	private static FileInputStream fis;
	private static Log log = LogFactory.getLog(FileOperator.class);

	// convert the path sent to a platform specific path
	public static String getPath(String path) {
		try {
			return path.replaceAll("/",
					Matcher.quoteReplacement(File.separator));
		} catch (Exception e) {
			log.error("Common error when getting file path", e);
		}
		return null;
	}

	public static boolean copyFile(String source, String dest) {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(new File(source));
			output = new FileOutputStream(new File(dest));
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
			return true;
		} catch (FileNotFoundException e) {
			log.error(
					"cannot find one of the files, while trying to copy file :"
							+ source + "\n to its destination: " + dest, e);
		} catch (IOException e) {
			log.error(
					"Error opening/working with file, while trying to copy file :"
							+ source + "\n to its destination: " + dest, e);
		} finally {
			try {
				input.close();
				output.close();
			} catch (IOException e) {
				log.error(
						"Error in closing file Stream , while trying to copy file "
								+ source + "\n to its destination: " + dest, e);
			}

		}
		return false;
	}

	public static String readFile(String path) throws FileNotFoundException {
		String content = "";
		return new Scanner(new File(path)).useDelimiter("\\Z").next();

	}

	public static void fileWrite(String path, String content)
			throws FileNotFoundException {
		PrintWriter out;
		out = new PrintWriter(path);
		out.println(content);
		out.close();
	}

	public static void createZip(String zipFilePath, String[] files)
			throws FileNotFoundException, IOException {
		FileOutputStream fout = new FileOutputStream(zipFilePath);
		ZipOutputStream zout = new ZipOutputStream(fout);
		for (int x = 0; x < files.length; x++) {
			File f = new File(files[x]);
			FileInputStream in = new FileInputStream(files[x]);
			zout.putNextEntry(new ZipEntry(f.getName()));

			byte[] b = new byte[1024];

			int count;

			while ((count = in.read(b)) > 0) {
				zout.write(b, 0, count);
			}
			in.close();
		}
		zout.close();
	}

	public static boolean writePem(String path, Object file) {
		FileOutputStream fos3;
		try {
			fos3 = new FileOutputStream(path);
			PEMWriter pemWriter3 = new PEMWriter(new PrintWriter(fos3));
			pemWriter3.writeObject(file);
			pemWriter3.flush();
			pemWriter3.close();
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

}
