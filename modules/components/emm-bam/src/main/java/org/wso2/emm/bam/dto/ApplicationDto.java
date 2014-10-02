package org.wso2.emm.bam.dto;

public class ApplicationDto extends NotificationsDto{
String packageName,icon,appName;

public String getPackageName() {
	return packageName;
}

public void setPackageName(String packageName) {
	this.packageName = packageName;
}

public String getIcon() {
	return icon;
}

public void setIcon(String icon) {
	this.icon = icon;
}

public String getAppName() {
	return appName;
}

public void setAppName(String appName) {
	this.appName = appName;
}

}
