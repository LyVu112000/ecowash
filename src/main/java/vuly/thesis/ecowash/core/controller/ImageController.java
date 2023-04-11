package vuly.thesis.ecowash.core.controller;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vuly.thesis.ecowash.core.entity.ImageReceipt;
import vuly.thesis.ecowash.core.entity.type.CreatedSourceType;
import vuly.thesis.ecowash.core.payload.response.AppResponse;
import vuly.thesis.ecowash.core.service.DeliveryReceiptService;
import vuly.thesis.ecowash.core.service.ImageReceiptService;
import vuly.thesis.ecowash.core.service.ReceivedReceiptService;
import vuly.thesis.ecowash.core.service.StaffService;
import vuly.thesis.ecowash.core.util.EbstUserRequest;
import vuly.thesis.ecowash.core.util.ImageUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class ImageController {
    private static final Path CURRENT_FOLDER = Paths.get(System.getProperty("user.dir"));
    private static final Path CURRENT_PATH = Paths.get("/var/www/cdn.ebst.tech/html/receipt");
//    private static final Path CURRENT_PATH_CONTRACT = Paths.get("test");
    private static final Path CURRENT_PATH_CONTRACT = Paths.get("/var/www/cdn.ebst.tech/html/contract");

    private final Path contractFilePath = Paths.get("file");

//  private static final Path CURRENT_PATH = Paths.get("test");
    private final Path imagePath = Paths.get("image");
    private final Path thumbnailPath = Paths.get("thumbnail");
    private final Path signaturePath = Paths.get("signature");
    private final Path logoPath = Paths.get("logo");

    private final Path deliveryBillPath = Paths.get("deliveryBill");
    private final Path deliveryBillThumbnailPath = Paths.get("deliveryBill/thumbnail");

    private final Path receivedBillPath = Paths.get("receivedBill");
    private final Path receivedBillThumbnailPath = Paths.get("receivedBill/thumbnail");

    @Autowired
    ReceivedReceiptService receivedReceiptService;
    @Autowired
    DeliveryReceiptService deliveryReceiptService;
    @Autowired
    StaffService staffService;
    @Autowired
    EbstUserRequest ebstUserRequest;

    private final ImageReceiptService imageReceiptService;

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam MultipartFile image) throws IOException {
        Path timePath = ImageUtil.timePath();
        if (!Files.exists(CURRENT_FOLDER.resolve(CURRENT_PATH).resolve(imagePath).resolve(timePath))) {
            Files.createDirectories(CURRENT_FOLDER.resolve(CURRENT_PATH).resolve(imagePath).resolve(timePath));
        }
        if (!Files.exists(CURRENT_FOLDER.resolve(CURRENT_PATH).resolve(thumbnailPath).resolve(timePath))) {
            Files.createDirectories(CURRENT_FOLDER.resolve(CURRENT_PATH).resolve(thumbnailPath).resolve(timePath));
        }
        Path file = CURRENT_FOLDER.resolve(CURRENT_PATH)
                .resolve(imagePath).resolve(timePath).resolve(image.getOriginalFilename());
        try (OutputStream os = Files.newOutputStream(file)) {
            ImageUtil.resizeImage(image, os);
        }
        Path thumbnailFile = CURRENT_FOLDER.resolve(CURRENT_PATH)
                .resolve(thumbnailPath).resolve(timePath).resolve(image.getOriginalFilename());
        try (OutputStream os = Files.newOutputStream(thumbnailFile)) {
            ImageUtil.createThumbnail(image, os);
        }
        return ResponseEntity.ok(AppResponse.success(timePath.resolve(image.getOriginalFilename())));
    }

    @PostMapping("/signatureCustomer")
    public ResponseEntity<?> uploadSignature(@RequestParam MultipartFile image) throws IOException {
        if (!Files.exists(CURRENT_FOLDER.resolve(CURRENT_PATH).resolve(signaturePath))) {
            Files.createDirectories(CURRENT_FOLDER.resolve(CURRENT_PATH).resolve(signaturePath));
        }
        Path file = CURRENT_FOLDER.resolve(CURRENT_PATH)
                .resolve(signaturePath).resolve(image.getOriginalFilename());
        try (OutputStream os = Files.newOutputStream(file)) {
            ImageUtil.resizeImage(image, os);
        }
        return ResponseEntity.ok(AppResponse.success(image.getOriginalFilename()));
    }

    @PostMapping("/images/received/{receiptId}")
    public ResponseEntity<?> uploadImageForReceivedReceipt(@RequestParam List<MultipartFile> images,
                                                           @PathVariable("receiptId") Long receiptId,
                                                           @RequestParam String createdSourceType) throws IOException {
        return this.uploadImageForReceipt(images, receiptId, createdSourceType, false);
    }

    @PostMapping("/images/delivery/{receiptId}")
    public ResponseEntity<?> uploadImageForDeliveryReceipt(@RequestParam List<MultipartFile> images,
                                                           @PathVariable("receiptId") Long receiptId,
                                                           @RequestParam String createdSourceType) throws IOException {
        return this.uploadImageForReceipt(images, receiptId, createdSourceType, true);
    }

    public ResponseEntity<?> uploadImageForReceipt(List<MultipartFile> images,
                                                   Long receiptId,
                                                   String createdSourceType,
                                                   boolean isDeliveryReceipt) throws IOException {
        Path timePath = ImageUtil.timePath();
        if (!Files.exists(CURRENT_FOLDER.resolve(CURRENT_PATH).resolve(imagePath).resolve(timePath))) {
            Files.createDirectories(CURRENT_FOLDER.resolve(CURRENT_PATH).resolve(imagePath).resolve(timePath));
        }
        if (!Files.exists(CURRENT_FOLDER.resolve(CURRENT_PATH).resolve(thumbnailPath).resolve(timePath))) {
            Files.createDirectories(CURRENT_FOLDER.resolve(CURRENT_PATH).resolve(thumbnailPath).resolve(timePath));
        }
        ArrayList<String> imageArr = new ArrayList<>();
        for (MultipartFile image :
                images) {
            Path file = CURRENT_FOLDER.resolve(CURRENT_PATH)
                    .resolve(imagePath).resolve(timePath).resolve(image.getOriginalFilename());

            try (OutputStream os = Files.newOutputStream(file)) {
                ImageUtil.resizeImage(image, os);
                imageArr.add(String.valueOf(timePath.resolve(image.getOriginalFilename())));
            }

            Path thumbnailFile = CURRENT_FOLDER.resolve(CURRENT_PATH)
                    .resolve(thumbnailPath).resolve(timePath).resolve(image.getOriginalFilename());
            try (OutputStream os = Files.newOutputStream(thumbnailFile)) {
                ImageUtil.createThumbnail(image, os);
            }
        }
        Object result = null;
        if (isDeliveryReceipt) {
            result = deliveryReceiptService.updateImages(receiptId, imageArr, CreatedSourceType.valueOf(createdSourceType));
        } else {
            result = receivedReceiptService.updateImages(receiptId, imageArr, CreatedSourceType.valueOf(createdSourceType));
        }

        return ResponseEntity.ok(AppResponse.success(result));
    }

    @PostMapping("/logo")
    public ResponseEntity<?> uploadLogo(@RequestParam MultipartFile image) throws IOException {

        if (!Files.exists(CURRENT_FOLDER.resolve(CURRENT_PATH).resolve(logoPath))) {
            Files.createDirectories(CURRENT_FOLDER.resolve(CURRENT_PATH).resolve(logoPath));
        }
        Path file = CURRENT_FOLDER.resolve(CURRENT_PATH)
                .resolve(logoPath).resolve(image.getOriginalFilename());
        try (OutputStream os = Files.newOutputStream(file)) {
            os.write(image.getBytes());
        }
        return ResponseEntity.ok(AppResponse.success(image.getOriginalFilename()));
    }

    @DeleteMapping("/images/received/{receiptId}")
    public ResponseEntity<?> deleteImageForReceivedReceipt(@PathVariable("receiptId") Long receiptId,
                                                           @RequestParam List<Long> imageIds,
                                                           @RequestParam String createdSourceType) throws IOException {
        return this.deleteImageForReceipt(receiptId, imageIds, createdSourceType, false);
    }

    @DeleteMapping("/images/delivery/{receiptId}")
    public ResponseEntity<?> deleteImageForDeliveryReceipt(@PathVariable("receiptId") Long receiptId,
                                                           @RequestParam List<Long> imageIds,
                                                           @RequestParam String createdSourceType) throws IOException {
        return this.deleteImageForReceipt(receiptId, imageIds, createdSourceType, true);
    }

    private ResponseEntity<?> deleteImageForReceipt(Long receiptId, List<Long> imageIds, String createdSourceType, boolean isDeliveryReceipt) throws IOException {
        List<ImageReceipt> imageReceipts = imageReceiptService.findByIdInAndCreatedSourceType(imageIds, CreatedSourceType.valueOf(createdSourceType));
        for (ImageReceipt imageReceipt : imageReceipts) {
            Files.deleteIfExists(CURRENT_FOLDER.resolve(CURRENT_PATH).resolve(imagePath).resolve(imageReceipt.getImage()));
            Files.deleteIfExists(CURRENT_FOLDER.resolve(CURRENT_PATH).resolve(thumbnailPath).resolve(imageReceipt.getImage()));
        }

        Object result;
        if (isDeliveryReceipt) {
            result = deliveryReceiptService.deleteImages(receiptId, imageReceipts);
        } else {
            result = receivedReceiptService.deleteImages(receiptId, imageReceipts);
        }

        return ResponseEntity.ok(AppResponse.success(result));
    }

    @PostMapping("/receiptImages/delivery")
    public ResponseEntity<?> uploadImageBillForDelivery(List<MultipartFile> images) throws IOException {
        Path timePath = ImageUtil.timePath();
        if (!Files.exists(CURRENT_FOLDER.resolve(CURRENT_PATH).resolve(deliveryBillPath).resolve(timePath))) {
            Files.createDirectories(CURRENT_FOLDER.resolve(CURRENT_PATH).resolve(deliveryBillPath).resolve(timePath));
        }
        if (!Files.exists(CURRENT_FOLDER.resolve(CURRENT_PATH).resolve(deliveryBillThumbnailPath).resolve(timePath))) {
            Files.createDirectories(CURRENT_FOLDER.resolve(CURRENT_PATH).resolve(deliveryBillThumbnailPath).resolve(timePath));
        }
        ArrayList<String> imageArr = new ArrayList<>();
        for (MultipartFile image :
                images) {
            Path file = CURRENT_FOLDER.resolve(CURRENT_PATH)
                    .resolve(deliveryBillPath).resolve(timePath).resolve(image.getOriginalFilename());

            try (OutputStream os = Files.newOutputStream(file)) {
                ImageUtil.resizeImage(image, os);
                imageArr.add(String.valueOf(timePath.resolve(image.getOriginalFilename())));
            }

            Path thumbnailFile = CURRENT_FOLDER.resolve(CURRENT_PATH)
                    .resolve(deliveryBillThumbnailPath).resolve(timePath).resolve(image.getOriginalFilename());
            try (OutputStream os = Files.newOutputStream(thumbnailFile)) {
                ImageUtil.createThumbnail(image, os);
            }
        }
        return ResponseEntity.ok(AppResponse.success(imageArr));
    }

    @PostMapping("/receiptImages/received")
    public ResponseEntity<?> uploadImageBillForReceived(List<MultipartFile> images) throws IOException {
        Path timePath = ImageUtil.timePath();
        if (!Files.exists(CURRENT_FOLDER.resolve(CURRENT_PATH).resolve(receivedBillPath).resolve(timePath))) {
            Files.createDirectories(CURRENT_FOLDER.resolve(CURRENT_PATH).resolve(receivedBillPath).resolve(timePath));
        }
        if (!Files.exists(CURRENT_FOLDER.resolve(CURRENT_PATH).resolve(receivedBillThumbnailPath).resolve(timePath))) {
            Files.createDirectories(CURRENT_FOLDER.resolve(CURRENT_PATH).resolve(receivedBillThumbnailPath).resolve(timePath));
        }
        ArrayList<String> imageArr = new ArrayList<>();
        for (MultipartFile image :
                images) {
            Path file = CURRENT_FOLDER.resolve(CURRENT_PATH)
                    .resolve(receivedBillPath).resolve(timePath).resolve(image.getOriginalFilename());

            try (OutputStream os = Files.newOutputStream(file)) {
                ImageUtil.resizeImage(image, os);
                imageArr.add(String.valueOf(timePath.resolve(image.getOriginalFilename())));
            }

            Path thumbnailFile = CURRENT_FOLDER.resolve(CURRENT_PATH)
                    .resolve(receivedBillThumbnailPath).resolve(timePath).resolve(image.getOriginalFilename());
            try (OutputStream os = Files.newOutputStream(thumbnailFile)) {
                ImageUtil.createThumbnail(image, os);
            }
        }
        return ResponseEntity.ok(AppResponse.success(imageArr));
    }
    @PostMapping("/contract")
    public ResponseEntity<?> uploadFileContract(List<MultipartFile> file) throws IOException {
        Path timePath = ImageUtil.timePath();
        if (!Files.exists(CURRENT_FOLDER.resolve(CURRENT_PATH_CONTRACT).resolve(contractFilePath).resolve(timePath))) {
            Files.createDirectories(CURRENT_FOLDER.resolve(CURRENT_PATH_CONTRACT).resolve(contractFilePath).resolve(timePath));
        }
        ArrayList<String> imageArr = new ArrayList<>();
        for (MultipartFile image :
                file) {
            Path filePath = CURRENT_FOLDER.resolve(CURRENT_PATH_CONTRACT)
                    .resolve(contractFilePath).resolve(timePath).resolve(image.getOriginalFilename());

            try (OutputStream os = Files.newOutputStream(filePath)) {
                os.write(image.getBytes());
                imageArr.add(String.valueOf(timePath.resolve(image.getOriginalFilename())));
            }
        }
        return ResponseEntity.ok(AppResponse.success(imageArr));
    }
}
