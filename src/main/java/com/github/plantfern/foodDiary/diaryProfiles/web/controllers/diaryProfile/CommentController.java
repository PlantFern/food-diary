package com.github.plantfern.foodDiary.diaryProfiles.web.controllers.diaryProfile;


import com.github.plantfern.foodDiary.diaryProfiles.api.CommentableType;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.DiaryCommentDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.services.DiaryCommentService;
import com.github.plantfern.foodDiary.diaryProfiles.domain.services.DiaryProfileService;
import com.github.plantfern.foodDiary.diaryProfiles.web.requests.CommentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/diary-profile/comment")
public class CommentController {


    private final DiaryCommentService diaryCommentService;

    public CommentController(DiaryCommentService diaryCommentService) {
        this.diaryCommentService = diaryCommentService;
    }

    @PostMapping("/{diaryProfile}")
    public ResponseEntity<DiaryCommentDto> create(
            @PathVariable Long diaryProfile,
            @ModelAttribute CommentRequest request
    ) {

        return ResponseEntity.ok(diaryCommentService.create(
                        diaryProfile,
                        request.commentableType(),
                        request.commentableId(),
                        request.body()
                )
        );
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<DiaryCommentDto> update(
            @PathVariable Long commentId,
            @ModelAttribute CommentRequest request
    ) {

        return ResponseEntity.ok(diaryCommentService.update(
                        commentId,
                        request.commentableType(),
                        request.commentableId(),
                        request.body()
                )
        );
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<DiaryCommentDto> getById(
            @PathVariable Long commentId
    ) {

        return ResponseEntity.ok(diaryCommentService.getById(commentId));
    }

    @GetMapping("/get-by-comment-type/{diaryProfileId}")
    public ResponseEntity<List<DiaryCommentDto>>  getAllByCommentType(
            @PathVariable Long diaryProfileId,
            @RequestParam String commentType
    ) {

        return ResponseEntity.ok(diaryCommentService
                .getByDiaryProfileAndCommentableType(
                        diaryProfileId,
                        CommentableType.valueOf(commentType)
                ));
    }

    @PutMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long commentId
    ) {

        diaryCommentService.delete(commentId);
    }

    @GetMapping("/get-by-ids/}")
    public ResponseEntity<List<DiaryCommentDto>> getAllByIds(
            @RequestParam Set<Long> commentIds
    ) {

        return ResponseEntity.ok(
                diaryCommentService.getByCommentableIds(commentIds)
        );
    }
}
