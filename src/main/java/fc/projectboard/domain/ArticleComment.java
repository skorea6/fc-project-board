package fc.projectboard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class ArticleComment extends AuditingFields{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = false) // cascade 는 기본값 none. 댓글이 삭제 됬다고 해서 Article이 삭제되어야하는건 아님.
    private Article article; // 게시글 (ID)

    @Setter
    @JoinColumn(name = "userId")
    @ManyToOne(optional = false)
    private UserAccount userAccount;

    @Setter
    @Column(updatable = false)
    private Long parentCommentId; // 부모 댓글 ID

    @ToString.Exclude
    @OrderBy("createdAt ASC")
    @OneToMany(mappedBy = "parentCommentId", cascade = CascadeType.ALL)
    private Set<ArticleComment> childComments = new LinkedHashSet<>();

    @Setter
    @Column(nullable = false, length = 500)
    private String content; // 본문


    protected ArticleComment() {

    }

    private ArticleComment(Article article, UserAccount userAccount, Long parentCommentId, String content) {
        this.article = article;
        this.userAccount = userAccount;
        this.parentCommentId = parentCommentId;
        this.content = content;
    }

    public static ArticleComment of(Article article, UserAccount userAccount, String content) {
        return new ArticleComment(article, userAccount, null, content);
    }

    public void addChildComment(ArticleComment child){
        child.setParentCommentId(this.getId());
        this.getChildComments().add(child);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleComment that)) return false;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
