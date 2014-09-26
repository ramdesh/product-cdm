package org.wso2.emm.bam;

import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.commons.AttributeType;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.emm.bam.dto.TemperatureDto;
import org.wso2.emm.bam.util.Constants;
import org.wso2.emm.bam.util.JSONReader;

/**
 * Created with IntelliJ IDEA.
 * User: chan
 * Date: 9/26/14
 * Time: 8:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class TemperatureStream implements EMMStreamDefinition{
    private static Logger log = Logger.getLogger(TemperatureStream.class);
    private StreamDefCreator definition;
    @Override
    public StreamDefinition getStreamDefinition() {
        definition = new StreamDefCreator(Constants.TEMPERTURE_STREAM,
                Constants.TEMPERTURE_STREAM_VERSION,
                Constants.TEMPERTURE_STREAM_NICKNAME,
                Constants.TEMPERTURE_STREAM_DESCRIPTION);

        definition.setPayloadDefinition("tenantId", AttributeType.INT);
        definition.setPayloadDefinition("deviceId", AttributeType.STRING);
        definition.setPayloadDefinition("temperature", AttributeType.DOUBLE);
        try{
            return definition.getStreamDef();
        } catch (MalformedStreamDefinitionException e) {
            log.error("Error getting stream definition for registration stream", e);
        }
        return null;
    }

    @Override
    public Object[] getPayload(JSONReader js) {
        if(log.isDebugEnabled()){
            log.debug("Call to publish device registration details");
        }
        TemperatureDto dto = new TemperatureDto();
        dto.setTenantId(Integer.parseInt(js.read("tenantId")));
        dto.setDeviceId(js.read("deviceId"));
        dto.setTempeature(Double.valueOf(js.read("temperature")));

        return new Object[] {  dto.getTenantId(), dto.getDeviceId(),
                dto.getTempeature(), };
    }
}
