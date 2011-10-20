package org.neo4j.rest.graphdb.converter;

/**
 * User: KBurchardi
 * Date: 19.10.11
 * Time: 10:37
 */
public class ConversionInfo {

    private Object conversionData;
    private boolean successfulConversion;

    public ConversionInfo(Object conversionData, boolean successfulConversion) {
        this.conversionData = conversionData;
        this.successfulConversion = successfulConversion;
    }

    public boolean isSuccessfulConversion() {
        return successfulConversion;
    }

    public Object getConversionData() {
        return conversionData;
    }
}
