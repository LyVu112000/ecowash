package vuly.thesis.ecowash.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import vuly.thesis.ecowash.core.repository.core.BaseJpaRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T, K extends Serializable, R extends BaseJpaRepository<T, K>> {
	protected R repository;

	public BaseRepository(R repository) {
		this.repository = repository;
	}

	public Optional<T> findById(K id) {
		// FIXME: BY PASS BY JOB (JOB dont have specific tenantID)
		return repository.findById(id);
	}

	public T save(T entity) {
		return repository.save(entity);
	}

	public T saveAndFlush(T entity) {
		return repository.saveAndFlush(entity);
	}

	public List<T> saveAll(Iterable<T> entities) {
		return repository.saveAll(entities);
	}
	public Page<T> findAll(Specification<T> specification, Pageable pageable) {
		return repository.findAll(specification, pageable);
	}

	public List<T> findAll(Specification<T> specification) {
		return repository.findAll(specification);
	}

	public void delete(T entity) {
		repository.delete(entity);
	}

	public List<T> saveAll(List<T> entities){
		return repository.saveAll(entities);
	}

	public void flush() {
		repository.flush();
	}

	public void deleteAll(Iterable<T> entities) {
		repository.deleteAll(entities);
	}
}
