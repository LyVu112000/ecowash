package vuly.thesis.ecowash.core.util;

import org.springframework.http.MediaType;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;

public class MediaTypeUtils {
    public static MediaType getMediaTypeForFile(File file) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String mimeType = fileNameMap.getContentTypeFor(file.getName());

        //  String mineType = servletContext.getMimeType(fileName);
        try {
            MediaType mediaType = MediaType.parseMediaType(mimeType);
            return mediaType;
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
