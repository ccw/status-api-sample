package twitter.api.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import twitter.api.domain.Ownable;

/**
 * To customize existent Repository interface with security features.
 *
 * @author ccw
 */
@NoRepositoryBean
public interface SecuredJpaRepository<T extends Ownable, ID extends Long> extends PagingAndSortingRepository<T, ID> {
}
