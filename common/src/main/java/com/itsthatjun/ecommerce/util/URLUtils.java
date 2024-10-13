package com.itsthatjun.ecommerce.util;

import org.springframework.web.reactive.function.server.ServerRequest;

public class URLUtils {

    /**
     * Get the base URL of the application
     * @param request
     * @return
     */
    public static String getBaseUrl(ServerRequest request) {
        String scheme = request.uri().getScheme();
        String serverName = request.uri().getHost();
        int serverPort = request.uri().getPort();
        String contextPath = request.path();

        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);

        if ((serverPort != 80) && (serverPort != 443) && (serverPort != -1)) {
            url.append(":").append(serverPort);
        }

        url.append(contextPath);

        if (!url.toString().endsWith("/")) {
            url.append("/");
        }

        return url.toString();
    }
}
