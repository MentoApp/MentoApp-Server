package com.mentit.mento.domain.users.service.port;

import com.mentit.mento.domain.users.domain.MyCareerTags;
import com.mentit.mento.domain.users.domain.UserStatusTag;

public interface MyCareerTagsEntityRepository {

    MyCareerTags save(MyCareerTags myCareerTags);

    MyCareerTags save(MyCareerTags myCareerTags, UserStatusTag userStatusTag);
}
