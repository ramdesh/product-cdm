package org.wso2.emm.bam.dto;

/**
 * Created with IntelliJ IDEA.
 * User: chan
 * Date: 9/26/14
 * Time: 8:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class TemperatureDto {
    private int tenantId;
    private String deviceId;
    private double tempeature;

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public double getTempeature() {
        return tempeature;
    }

    public void setTempeature(double tempeature) {
        this.tempeature = tempeature;
    }
}
