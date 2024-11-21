package com.mentit.mento.domain.users.infrastructure;

import com.mentit.mento.domain.users.domain.MyCareerTags;
import com.mentit.mento.domain.users.domain.UserStatusTag;
import com.mentit.mento.domain.users.domain.entity.MyCareerTagsEntity;
import com.mentit.mento.domain.users.domain.entity.UserStatusTagEntity;
import com.mentit.mento.domain.users.infrastructure.jpaRepository.MyCareerTagsEntityJPARepository;
import com.mentit.mento.domain.users.service.port.MyCareerTagsEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MyCareerTagsEntityRepositoryImpl implements MyCareerTagsEntityRepository {

    private final MyCareerTagsEntityJPARepository myCareerTagsEntityJPARepository;

    @Override
    public MyCareerTagsEntity save(MyCareerTagsEntity myCareerTagsEntity) {
        return myCareerTagsEntityJPARepository.save(myCareerTagsEntity);
    }
}
