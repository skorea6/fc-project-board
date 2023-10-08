package fastcampus.projectboard.dto;

import java.time.LocalDateTime;

/**
 * DTO for {@link fastcampus.projectboard.domain.ArticleComment}
 */
public record ArticleCommentDto(LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy, String content) {
    public static ArticleCommentDto of(LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy, String content) {
        return new ArticleCommentDto(createdAt, createdBy, modifiedAt, modifiedBy, content);
    }
}