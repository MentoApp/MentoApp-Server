package com.mentit.mento.domain.users.infrastructure;

import com.mentit.mento.domain.users.domain.entity.MyStatusTagsEntity;
import com.mentit.mento.domain.users.infrastructure.jpaRepository.MyStatusTagsEntityJPARepository;
import com.mentit.mento.domain.users.service.port.MyStatusTagsEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MyStatusTagsEntityRepositoryImpl implements MyStatusTagsEntityRepository {
    private final MyStatusTagsEntityJPARepository myStatusTagsEntityJPARepository;

    @Override
    public MyStatusTagsEntity save(MyStatusTagsEntity myStatusTagsEntity) {
        return myStatusTagsEntityJPARepository.save(myStatusTagsEntity);
    }

    @Override
    public List<MyStatusTagsEntity> saveAll(List<MyStatusTagsEntity> myStatusTags) {
        return myStatusTagsEntityJPARepository.saveAll(myStatusTags);
    }
}
