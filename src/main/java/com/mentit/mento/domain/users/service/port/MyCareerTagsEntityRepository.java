package com.mentit.mento.domain.users.service.port;

import com.mentit.mento.domain.users.domain.MyCareerTags;
import com.mentit.mento.domain.users.domain.UserStatusTag;
import com.mentit.mento.domain.users.domain.entity.MyCareerTagsEntity;
import com.mentit.mento.domain.users.domain.entity.UserStatusTagEntity;

public interface MyCareerTagsEntityRepository {

    MyCareerTagsEntity save(MyCareerTagsEntity myCareerTagsEntity);

}
