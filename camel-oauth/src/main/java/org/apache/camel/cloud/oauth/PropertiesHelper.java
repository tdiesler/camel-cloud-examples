package org.apache.camel.cloud.oauth;

import org.apache.camel.CamelContext;

public class PropertiesHelper {

    public static String getProperty(CamelContext ctx, String key) {
        return ctx.resolvePropertyPlaceholders("{{" + key + "}}");
    }
}
