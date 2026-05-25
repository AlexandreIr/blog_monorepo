package br.com.libertadfacilities.blog.util;

import java.text.Normalizer;
import java.util.Locale;

public final class SlugUtil {

    private SlugUtil() {}

    public static  String toSlug(String input){
        if(input == null || input.isBlank()){
            return "";
        }

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return normalized
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]","")
                .trim()
                .replaceAll("\\s","")
                .replaceAll("-+","");
    }
}
