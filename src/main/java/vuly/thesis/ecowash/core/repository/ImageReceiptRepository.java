package vuly.thesis.ecowash.core.repository;

import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.ImageReceipt;
import vuly.thesis.ecowash.core.entity.type.CreatedSourceType;
import vuly.thesis.ecowash.core.repository.core.IImageReceiptRepository;

import java.util.List;

@Service
public class ImageReceiptRepository extends BaseRepository<ImageReceipt, Long, IImageReceiptRepository> {

    public ImageReceiptRepository(IImageReceiptRepository repository) {
        super(repository);
    }

    public List<ImageReceipt> findByIdInAndCreatedSourceType(List<Long> ids, CreatedSourceType createdSourceType) {
        return repository.findByIdInAndCreatedSourceType(ids, createdSourceType);
    }
}
