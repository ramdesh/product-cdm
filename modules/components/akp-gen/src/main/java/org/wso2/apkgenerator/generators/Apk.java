package org.wso2.apkgenerator.generators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.wso2.apkgenerator.util.Constants;
import org.wso2.apkgenerator.util.FileOperator;

/*
 * Using the BKS created and the EMM agent source included with the project,
 * apk file can be generated with this class. Also the final zip out put is 
 * generated here
 */
public class Apk {
	private static Log log = LogFactory.getLog(Apk.class);

	// carry out the sequesnce of tasks neccessery to generate the apk
	public static String generateApk(String commonUtilPath, String serverIp,
			String password, String zipFileName, String zipPath) {
		// Some configurations, such as server IP, trust store password has to
		// be
		// added to the source code of the EMM agent, prior to compiling it
		String fileContent = null;
		try {
			fileContent = FileOperator.readFile(commonUtilPath);
		} catch (FileNotFoundException e) {
			log.error("Error reading file " + commonUtilPath, e);
			return null;
		}

		try {
			changeContent(commonUtilPath, fileContent, serverIp, password);
		} catch (FileNotFoundException e) {
			log.error("Error changing content of android source "
					+ commonUtilPath, e);
			return null;
		}
		/*
		 * To solve the issue of the pom file include with the EMM agent source
		 * getting deleted while packaging the component, it was renamed as a
		 * txt file and renamed as a pom file in run time(This only happen once)
		 */
		renameApk();

		// This is where the actual android source compilation happens
		if(!buildApk()){
			return null;
		}

		// file paths of the four files that needs to be outputted
		String apkPath = APKGenerator.workingDir + Constants.ANDROID_AGENT_APK;
		String wso2carbon = APKGenerator.workingDir + Constants.WSO2CARBON_JKS;
		String client_truststore = APKGenerator.workingDir
				+ Constants.CLIENT_TRUST_JKS;
		String wso2mobilemdm = APKGenerator.workingDir + Constants.WSO2EMM_JKS;

		try {
			// Zip the above four files and create a zip file in the
			// [EMM_HOME]/APK folder
			FileOperator.createZip(zipPath + zipFileName, new String[] {
					apkPath, wso2carbon, client_truststore, wso2mobilemdm });
		} catch (FileNotFoundException e) {
			log.error("Error while trying to create the final zip file", e);
			return null;
		} catch (IOException e) {
			log.error("Error while trying to create the final zip file", e);
			return null;
		}

		return zipPath + zipFileName;
	}

	private static void renameApk() {
		File fakePOMFile = null;
		File realPOMFile = null;

		// create new File objects
		fakePOMFile = new File(APKGenerator.workingDir
				+ Constants.ANDROID_AGENT_POM_FAKE);
		realPOMFile = new File(APKGenerator.workingDir + Constants.ANDROID_AGENT_POM);

		// rename file
		fakePOMFile.renameTo(realPOMFile);
	}

	// change the content of the commonUtils file and change ip address and
	// password
	private static void changeContent(String path, String content,
			String hostName, String password) throws FileNotFoundException {
		int startInd = content.indexOf("String SERVER_IP = \"");
		int lastInd = content.indexOf("\";", startInd);
		String changedContent = content.substring(0, startInd)
				+ "String SERVER_IP = \"" + hostName
				+ content.substring(lastInd);

		startInd = changedContent.indexOf("String TRUSTSTORE_PASSWORD = \"");
		lastInd = changedContent.indexOf("\";", startInd);
		changedContent = changedContent.substring(0, startInd)
				+ "String TRUSTSTORE_PASSWORD = \"" + password
				+ changedContent.substring(lastInd);
		FileOperator.fileWrite(path, changedContent);

	}

	// build the apk using maven
	static boolean buildApk() {
		try {
			List<String> PUBLISH_GOALS = Arrays.asList("clean", "package");
			InvocationRequest request = new DefaultInvocationRequest();
			request.setPomFile(new File(APKGenerator.workingDir
					+ Constants.ANDROID_AGENT + File.separator));
			request.setGoals(PUBLISH_GOALS);

			DefaultInvoker invoker = new DefaultInvoker();
			// Constants.MAVEN_HOME_FOLDER = getMavenHome("MAVEN_HOME");
			invoker.setMavenHome(new File(getMavenHome("MAVEN_HOME")));
			invoker.execute(request);
			return true;
		} catch (NullPointerException e) {
			log.error(
					"Could not find MAVEN_HOME, please set it globally, so that"
							+ " the user that starts java can access it", e);
		} catch (MavenInvocationException e) {
			log.error("Error while executing maven invoker", e);
		} catch (IOException e) {
			log.error("Error when getting MAVEN_HOME", e);
		}
		return false;
	}

	/*
	 * Go through environment variables and find the environment variable
	 */
	private static String getMavenHome(String environmentVar)
			throws IOException {
		String home = "";
		Map<String, String> variables = System.getenv();

		for (Map.Entry<String, String> entry : variables.entrySet()) {
			String name = entry.getKey();
			String value = entry.getValue();
			if (name.equalsIgnoreCase(environmentVar)) {
				home = value;
			}
		}
		File homePath = new File(home);
		String filePath = homePath.getCanonicalPath();// handle symlinks
		// maven invoker adds "bin/mvn" when setMavenHome is called. so if it is
		// present in the MAVEN_HOME, it has to be removed
		if (filePath.contains("/bin/mvn")) {
			filePath = filePath.substring(0, filePath.indexOf("/bin/mvn"));
		}
		return filePath;
	}

}
