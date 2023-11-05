package fc.projectboard.dto.request;

import fc.projectboard.dto.ArticleCommentDto;
import fc.projectboard.dto.UserAccountDto;

/**
 * DTO for {@link fc.projectboard.domain.ArticleComment}
 */
public record ArticleCommentRequest(Long articleId, Long parentCommentId, String content) {
    public static ArticleCommentRequest of(Long articleId, String content) {
        return ArticleCommentRequest.of(articleId, null, content);
    }

    public static ArticleCommentRequest of(Long articleId, Long parentCommentId, String content) {
        return new ArticleCommentRequest(articleId, parentCommentId, content);
    }

    public ArticleCommentDto toDto(UserAccountDto userAccountDto) {
        return ArticleCommentDto.of(
                articleId,
                userAccountDto,
                parentCommentId,
                content
        );
    }
}