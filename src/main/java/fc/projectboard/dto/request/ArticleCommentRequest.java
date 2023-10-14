package fc.projectboard.dto.request;

import fc.projectboard.dto.ArticleCommentDto;
import fc.projectboard.dto.UserAccountDto;

/**
 * DTO for {@link fc.projectboard.domain.ArticleComment}
 */
public record ArticleCommentRequest(Long articleId, String content) {
    public static ArticleCommentRequest of(Long articleId, String content) {
        return new ArticleCommentRequest(articleId, content);
    }

    public ArticleCommentDto toDto(UserAccountDto userAccountDto) {
        return ArticleCommentDto.of(
                articleId,
                userAccountDto,
                content
        );
    }
}