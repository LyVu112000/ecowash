package vuly.thesis.ecowash.core.repository.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Optional;

@Slf4j
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class BaseJpaRepositoryImp<T, ID extends Serializable>
		extends SimpleJpaRepository<T, ID> implements BaseJpaRepository<T, ID> {

	private EntityManager entityManager;

	public BaseJpaRepositoryImp(JpaEntityInformation<T, ID>
										  entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
	}

	@Transactional
	@Override
	public Optional<T> findByIdAndTenantId(ID id, long tenantId) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> cQuery = builder.createQuery(getDomainClass());
		Root<T> root = cQuery.from(getDomainClass());
		cQuery
				.select(root)
				.where(builder
						.and(builder.equal(root.<ID>get("id"),  id), builder.equal(root.<Long>get("tenantId"),  tenantId)));
		TypedQuery<T> query = entityManager.createQuery(cQuery);
		return query.getResultList().isEmpty() ? Optional.empty() : Optional.ofNullable(query.getSingleResult());
	}
}