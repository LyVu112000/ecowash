package vuly.thesis.ecowash.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.ImageReceipt;
import vuly.thesis.ecowash.core.entity.type.CreatedSourceType;
import vuly.thesis.ecowash.core.repository.ImageReceiptRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageReceiptService {

    private final ImageReceiptRepository imageReceiptRepository;

    public List<ImageReceipt> findByIdInAndCreatedSourceType(List<Long> imageReceiptIds, CreatedSourceType createdSourceType) {
        return imageReceiptRepository.findByIdInAndCreatedSourceType(imageReceiptIds, createdSourceType);
    }
}
