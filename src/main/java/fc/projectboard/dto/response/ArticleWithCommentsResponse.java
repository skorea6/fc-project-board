package fc.projectboard.dto.response;

import fc.projectboard.dto.ArticleCommentDto;
import fc.projectboard.dto.ArticleWithCommentsDto;
import fc.projectboard.dto.HashtagDto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public record ArticleWithCommentsResponse(
        Long id,
        String title,
        String content,
        Set<String> hashtags,
        LocalDateTime createdAt,
        String email,
        String nickname,
        String userId,
        Set<ArticleCommentResponse> articleCommentsResponse
) {
    public static ArticleWithCommentsResponse of(Long id, String title, String content, Set<String> hashtags, LocalDateTime createdAt, String email, String nickname, String userId, Set<ArticleCommentResponse> articleCommentResponses) {
        return new ArticleWithCommentsResponse(id, title, content, hashtags, createdAt, email, nickname, userId, articleCommentResponses);
    }

    public static ArticleWithCommentsResponse from(ArticleWithCommentsDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return new ArticleWithCommentsResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtagDtos().stream()
                        .map(HashtagDto::hashtagName)
                        .collect(Collectors.toUnmodifiableSet()),
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname,
                dto.userAccountDto().userId(),
                organizeChildComments(dto.articleCommentDtos())
        );
    }

    private static Set<ArticleCommentResponse> organizeChildComments(Set<ArticleCommentDto> dtos) {
        Map<Long, ArticleCommentResponse> map = dtos.stream()
                .map(ArticleCommentResponse::from) // ArticleCommentResponse 로 mapping (이 과정에서 자식 댓글들이 시간 ASC로 정렬)
                .collect(Collectors.toMap(ArticleCommentResponse::id, Function.identity())); // id, ArticleCommentResponse(자신)으로 Map

        map.values().stream()
                .filter(ArticleCommentResponse::hasParentComment) // 자식 댓글들만 filter
                .forEach(comment -> {
                    ArticleCommentResponse parentComment = map.get(comment.parentCommentId()); // 부모 댓글 찾기
                    parentComment.childComments().add(comment); // 부모 댓글의 자식 댓글 리스트에 추가
                });

        return map.values().stream()
                .filter(comment -> !comment.hasParentComment()) // 부모 댓글들만 filter
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator
                                .comparing(ArticleCommentResponse::createdAt)
                                .reversed() // [1] 최신순으로 정렬. DESC
                                .thenComparingLong(ArticleCommentResponse::id) // [2] id로 정렬. ASC
                        )
                ));
    }

}
