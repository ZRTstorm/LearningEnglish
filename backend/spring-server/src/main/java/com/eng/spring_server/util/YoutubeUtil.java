package com.eng.spring_server.util;

import java.net.URI;
import java.net.URISyntaxException;

public class YoutubeUtil {

    public static String  extractYoutubeVideoId(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            String query = uri.getQuery();

            if (host.contains("youtu.be")) {
                return uri.getPath().substring(1); // /dQw4w9WgXcQ → dQw4w9WgXcQ
            }

            if (host.contains("youtube.com") && query != null) {
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    if (pair.length == 2 && pair[0].equals("v")) {
                        return pair[1]; // dQw4w9WgXcQ
                    }
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
