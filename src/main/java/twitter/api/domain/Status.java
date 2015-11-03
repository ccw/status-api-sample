package twitter.api.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Status domain object, the major resource of the RESTful service.
 *
 * @author ccw
 */
@Entity
@Table(
    name = "status"
)
@Data
@RequiredArgsConstructor
public class Status implements Ownable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "content")
    private final String content;

    @Setter
    @Column(name = "user_id")
    private Long userId;

    Status() {
        this.content = null;
        this.userId = null;
    }

    public Long getOwnerId() {
        return this.userId;
    }
}