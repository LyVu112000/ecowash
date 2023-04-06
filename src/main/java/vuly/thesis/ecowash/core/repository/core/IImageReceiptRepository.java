package vuly.thesis.ecowash.core.repository.core;

import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.entity.ImageReceipt;
import vuly.thesis.ecowash.core.entity.type.CreatedSourceType;

import java.util.List;

@Repository
public interface IImageReceiptRepository extends BaseJpaRepository<ImageReceipt, Long> {

    List<ImageReceipt> findByIdInAndCreatedSourceTypeAndTenantId(List<Long> ids, CreatedSourceType createdSourceType, Long tenantId);
}
