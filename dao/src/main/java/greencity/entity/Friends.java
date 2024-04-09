package greencity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "friends")
public class Friends {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id", referencedColumnName = "id")
    private User friend;

    @Column(name = "status", nullable = false)
    private String status;

    public Friends() {
    }

    public Friends(Long id, User user, User friend, String status) {
        this.id = id;
        this.user = user;
        this.friend = friend;
        this.status = status;
    }

}
