package com.mentit.mento.domain.users.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagListDTO {

    private List<String> userJobsKeywords;     // user_jobs_keywords_static_data
    private List<String> myStatusTags;        // my_status_tag_static_data
    private List<String> myCareerTags;        // my_career_tags_static_data
    private List<String> corporateTags;       // coporate_tags_static_data
    private List<String> boardKeywords;       // board_keyword_static_data
}
