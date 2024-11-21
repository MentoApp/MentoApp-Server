package com.mentit.mento.domain.users.service.port;

import com.mentit.mento.domain.users.domain.MyStatusTags;
import com.mentit.mento.domain.users.domain.UserStatusTag;
import com.mentit.mento.domain.users.domain.entity.MyStatusTagsEntity;
import com.mentit.mento.domain.users.domain.entity.UserStatusTagEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MyStatusTagsEntityRepository {
    MyStatusTagsEntity save(MyStatusTagsEntity myStatus);

    List<MyStatusTagsEntity> saveAll(List<MyStatusTagsEntity> myStatusTags);
}
