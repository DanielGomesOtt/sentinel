package com.sentinel.sentinel.utils;

public final class ApiVersionUtil {

    public static String normalize(String rawVersion) {
        if (rawVersion == null || rawVersion.isBlank()) {
            throw new IllegalArgumentException("API version cannot be null or empty");
        }


        String version = rawVersion.trim().replaceAll("/+", "/");


        if (version.endsWith("/")) {
            version = version.substring(0, version.length() - 1);
        }


        if (!version.startsWith("/")) {
            version = "/" + version;
        }


        if (!version.matches("^/v\\d+$")) {
            throw new IllegalArgumentException(
                    "Invalid API version format. Expected pattern: /v{number}"
            );
        }

        return version;
    }
}
