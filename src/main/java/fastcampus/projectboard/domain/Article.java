package fastcampus.projectboard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

// @EqualsAndHashCode 을 쓰면 모든 컬럼들을 다 검사하기 때문에 비효율적. 그러므로 아래와 같이 인텔리제이에서 따로 생성.
@Getter
@ToString(callSuper = true) // 안쪽까지 tostring 찍어내기 위함. 즉, userAccount + auditing fields까지.
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class Article extends AuditingFields{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = false) // 객체에 null 이 들어갈수도. 회원탈퇴한 사람. ManyToOne은 기본이 즉시로딩. eager loading
    private UserAccount userAccount;

    @Setter
    @Column(nullable = false)
    private String title; // 제목

    @Setter
    @Column(nullable = false, length = 10000)
    private String content; // 본문

    @Setter
    private String hashtag; // 해시태그

    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL) // 게시글이 삭제되면 댓글이 모두 삭제되게끔. OneToMany는 기본이 지연로딩. lazy loading
    @ToString.Exclude
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    protected Article() {
    }

    private Article(UserAccount userAccount, String title, String content, String hashtag) {
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    public static Article of(UserAccount userAccount, String title, String content, String hashtag) {
        return new Article(userAccount, title, content, hashtag);
    }

    // id 컬럼만 비교하여 equals and hashcode 생성. (효율적)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        // [중요] 영속화 되기 전에는 id가 null일 것이다.(영속화 후 id가 넣어지므로) 그러므로 둘이 같은지 비교할때는 null이 아닐때에만 비교해야한다.
        // id가 null이 아닌지 체크 필요.
        return id != null && Objects.equals(id, article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
