package twitter.api.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import twitter.api.domain.Ownable;
import twitter.api.security.AuthUser;
import twitter.api.security.SecurityHelper;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * The default implementation of SecuredJpaRepository
 *
 * @author ccw
 */
@Transactional(readOnly = true)
public class SecuredJpaRepositoryImpl<T extends Ownable, ID extends Long> extends SimpleJpaRepository<T, ID> implements SecuredJpaRepository<T, ID> {

    protected JpaEntityInformation<T, ?> ei;
    protected EntityManager em;

    public SecuredJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);

        this.ei = entityInformation;
        this.em = entityManager;
    }

    public SecuredJpaRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);

        this.ei = JpaEntityInformationSupport.getEntityInformation(domainClass, em);
        this.em = em;
    }

    protected Specification<T> secureSpec(final Specification<T> spec) {
        final AuthUser user = SecurityHelper.getAuthUser();
        if (!SecurityHelper.isSuperUser(user)) {
            final Specifications<T> specs = Specifications.where(new Specification<T>() {
                @Override
                public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    return cb.equal(root.get("userId"), user.getUserId());
                }
            });
            if (spec == null) {
                return specs;
            }
            return specs.and(spec);
        }
        return spec;
    }

    @Override
    protected TypedQuery<T> getQuery(Specification<T> spec, Pageable pageable) {
        return super.getQuery(this.secureSpec(spec), pageable);
    }

    @Override
    protected TypedQuery<T> getQuery(Specification<T> spec, Sort sort) {
        return super.getQuery(this.secureSpec(spec), sort);
    }

    @Override
    protected TypedQuery<Long> getCountQuery(Specification<T> spec) {
        return super.getCountQuery(this.secureSpec(spec));
    }

    @Override
    public T findOne(ID id) {
        final AuthUser user = SecurityHelper.getAuthUser();
        T one = super.findOne(id);
        return SecurityHelper.isSuperUser(user)? one : (
                    one != null ? (one.getOwnerId().equals(user.getUserId()) ? one : null) : null
                );
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(ID id) {
        final T one = this.findOne(id);
        if (one != null) {
            super.delete(one);
        }
    }

    @Transactional(readOnly = false)
    @Override
    public <S extends T> S save(S entity) {
        if (entity.getId() == null) {
            return super.save(entity);
        } else {
            final T one = this.findOne((ID) entity.getId());
            return one == null ? null : super.save(entity);
        }
    }
}
