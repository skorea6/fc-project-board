package fc.projectboard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

// @EqualsAndHashCode 을 쓰면 모든 컬럼들을 다 검사하기 때문에 비효율적. 그러므로 아래와 같이 인텔리제이에서 따로 생성.
@Getter
@ToString(callSuper = true) // 안쪽까지 tostring 찍어내기 위함. 즉, userAccount + auditing fields까지.
@Table(indexes = {
        @Index(columnList = "title"),
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
    @JoinColumn(name = "userId")
    private UserAccount userAccount;

    @Setter
    @Column(nullable = false)
    private String title; // 제목

    @Setter
    @Column(nullable = false, length = 10000)
    private String content; // 본문


    @ToString.Exclude
    @JoinTable( // ManyToMany의 주인에서만 붙임. 중간 테이블 정의.
            name = "article_hashtag",
            joinColumns = @JoinColumn(name = "articleId"),
            inverseJoinColumns = @JoinColumn(name = "hashtagId")
    )
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}) // article 테이블에 insert, update시에 hashtag 테이블 데이터도 변경. delete 안넣는 이유: 게시글을 지우면 해당되는 해시태그가 전부삭제될텐데, 그러면 다른 게시글의 해시테그도 삭제되는 꼴.
    private Set<Hashtag> hashtags = new LinkedHashSet<>();


    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL) // 게시글이 삭제되면 댓글이 모두 삭제되게끔. OneToMany는 기본이 지연로딩. lazy loading
    @ToString.Exclude
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    protected Article() {
    }

    private Article(UserAccount userAccount, String title, String content) {
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
    }

    public static Article of(UserAccount userAccount, String title, String content) {
        return new Article(userAccount, title, content);
    }

    public void addHashtag(Hashtag hashtag) {
        this.getHashtags().add(hashtag);
    }

    public void addHashtags(Collection<Hashtag> hashtags) {
        this.getHashtags().addAll(hashtags);
    }

    public void clearHashtags() {
        this.getHashtags().clear();
    }

    // id 컬럼만 비교하여 equals and hashcode 생성. (효율적)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article that)) return false;
        // [중요] 영속화 되기 전에는 id가 null일 것이다.(영속화 후 id가 넣어지므로) 그러므로 둘이 같은지 비교할때는 null이 아닐때에만 비교해야한다.
        // id가 null이 아닌지 체크 필요.
        return this.getId() != null && this.getId().equals(that.getId()); // 그냥 id가 아니라 getId로 조회 필요
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
