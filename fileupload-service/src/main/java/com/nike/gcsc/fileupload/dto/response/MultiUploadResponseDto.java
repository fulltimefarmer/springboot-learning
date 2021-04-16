 package com.nike.gcsc.fileupload.dto.response;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author roger yang
 * @date 11/04/2019
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiUploadResponseDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -744921128500008556L;

    private Long sliceSize;
    
    private float completionRatio;
    
    private String s3FilePath;
    
    private List<SliceInfo> slices;
}
