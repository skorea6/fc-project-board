package fastcampus.projectboard.repository;

import fastcampus.projectboard.domain.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource // repository 어노테이션 명시.
public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
}