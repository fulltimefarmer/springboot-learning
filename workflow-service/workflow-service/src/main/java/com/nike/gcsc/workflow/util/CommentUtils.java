 package com.nike.gcsc.workflow.util;

import com.nike.gcsc.workflow.dto.param.CommentDto;

import lombok.NonNull;

/**
 * @author roger yang
 * @date 7/02/2019
 */
public class CommentUtils {
    private static final String APPROVED_OPINION_SEPARATOR = ":";
    private static final String TRUE = String.valueOf(Boolean.TRUE);
    private static final String FALSE = String.valueOf(Boolean.FALSE);
    
    public static String generateComment(@NonNull Boolean approved, @NonNull String opinion) {
        return String.valueOf(approved).concat(APPROVED_OPINION_SEPARATOR).concat(opinion);
    }
    
    public static CommentDto getCommentObject(@NonNull String comment) {
        CommentDto dto = new CommentDto();
        if(comment.startsWith(TRUE) || comment.startsWith(FALSE)) {
            dto.setApproved(Boolean.valueOf(comment.substring(0, comment.indexOf(APPROVED_OPINION_SEPARATOR))));
            dto.setOpinion(comment.substring(comment.indexOf(APPROVED_OPINION_SEPARATOR)+APPROVED_OPINION_SEPARATOR.length(), comment.length()));
        } else {
            dto.setOpinion(comment);
        }
        return dto;
    }
}
