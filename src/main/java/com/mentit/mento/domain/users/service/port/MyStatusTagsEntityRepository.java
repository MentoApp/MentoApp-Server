package com.mentit.mento.domain.users.service.port;

import com.mentit.mento.domain.users.domain.MyStatusTags;
import com.mentit.mento.domain.users.domain.UserStatusTag;
import org.springframework.stereotype.Repository;

@Repository
public interface MyStatusTagsEntityRepository {
    MyStatusTags save(MyStatusTags myStatus, UserStatusTag userStatusTagEntity);

    MyStatusTags save(MyStatusTags item);
}
