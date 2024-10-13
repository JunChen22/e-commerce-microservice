package com.itsthatjun.ecommerce.util;

import java.text.Normalizer;

public class StringUtil {

    /**
     * Convert a string to a "slug" format, which is a URL-friendly version of the string.
     *
     * @param input The string to convert to a slug
     * @return The slug version of the input string
     */
    public static String slugify(String input) {
        // Convert to lowercase
        String slug = input.toLowerCase();

        // Normalize the string to remove accents (e.g., "é" becomes "e")
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
        slug = slug.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Replace spaces with hyphens
        slug = slug.replaceAll("\\s+", "-");

        // Remove all non-alphanumeric characters except hyphens
        slug = slug.replaceAll("[^a-z0-9-]", "");

        // Remove leading and trailing hyphens
        slug = slug.replaceAll("^-+", "").replaceAll("-+$", "");

        return slug;
    }
}
