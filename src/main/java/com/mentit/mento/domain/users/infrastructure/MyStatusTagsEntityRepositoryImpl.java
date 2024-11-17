package com.mentit.mento.domain.users.infrastructure;

import com.mentit.mento.domain.users.domain.MyStatusTags;
import com.mentit.mento.domain.users.domain.UserStatusTag;
import com.mentit.mento.domain.users.domain.entity.MyStatusTagsEntity;
import com.mentit.mento.domain.users.infrastructure.jpaRepository.MyStatusTagsEntityJPARepository;
import com.mentit.mento.domain.users.service.port.MyStatusTagsEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MyStatusTagsEntityRepositoryImpl implements MyStatusTagsEntityRepository {
    private final MyStatusTagsEntityJPARepository myStatusTagsEntityJPARepository;

    @Override
    public MyStatusTags save(MyStatusTags myStatus, UserStatusTag userStatusTagEntity) {
        return myStatusTagsEntityJPARepository.save(MyStatusTagsEntity.from(myStatus,userStatusTagEntity)).to();
    }

    @Override
    public MyStatusTags save(MyStatusTags item) {
        return myStatusTagsEntityJPARepository.save(MyStatusTagsEntity.from(item)).to();
    }
}
