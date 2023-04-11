package vuly.thesis.ecowash.core.util;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Service
public class ImageUtil {

    public static final String CDN_BASE_URL = "https://cdn.ebst.tech";

    private static final Long MAX_IMAGE_SIZE = 3L * 1024 * 1024;
    private static final Integer MAX_WIDTH = 1200;
    private static final Integer MAX_HEIGHT = 1200;
    private static final Integer MAX_WIDTH_THUMBNAIL = 150;
    private static final Integer MAX_HEIGHT_THUMBNAIL = 150;

    private static EbstUserRequest ebstUserRequest;

    public ImageUtil(EbstUserRequest ebstUserRequest) {
        this.ebstUserRequest = ebstUserRequest;
    }

    public static void resizeImage(MultipartFile image, OutputStream os) throws IOException {
        String format = image.getContentType().split("/")[1]; // get file format
        BufferedImage scaledImg;
        if (image.getSize() > MAX_IMAGE_SIZE) {
            scaledImg = Thumbnails.of(ImageIO.read(image.getInputStream()))
                    .size(MAX_WIDTH, MAX_HEIGHT)
                    .asBufferedImage();
        } else {
            scaledImg = Thumbnails.of(ImageIO.read(image.getInputStream()))
                    .scale(0.8)
                    .asBufferedImage();
        }
        ImageIO.write(scaledImg, "jpg", os);
    }

    public static void createThumbnail(MultipartFile image, OutputStream os) throws IOException {
        String format = image.getContentType().split("/")[1]; // get file format
        BufferedImage scaledImg = Thumbnails.of(ImageIO.read(image.getInputStream()))
                .size(MAX_WIDTH_THUMBNAIL, MAX_HEIGHT_THUMBNAIL)
                .asBufferedImage();
        ImageIO.write(scaledImg, "jpg", os);
    }

    public static Path timePath() {
        return Paths.get(DateTimeFormatter.ofPattern("yyyyMM")
                .withZone(ebstUserRequest.currentZoneId()).format(Instant.now()));
    }
}
