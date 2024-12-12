package com.mentit.mento.global.batch.job;

import com.mentit.mento.domain.users.constant.AccountStatus;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.infrastructure.jpaRepository.UserJPARepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DeleteOldUserJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final UserJPARepository userRepository;

    @Bean
    public Job deleteOldUserFirstJob() {
        return new JobBuilder("DeleteOldUserJob", jobRepository)
                .start(deleteOldUserStep()) //step을 만들어 넣어야 하는 자리
                .build();
        //처음 시작할 step 다음 단계가 있다면 .nextStep사용
    }

    @Bean
    public Step deleteOldUserStep() {
        return new StepBuilder("deleteFirstStep", jobRepository)
                .chunk(10, transactionManager) //transaction을 관리해준다. 실패시 롤백해주거나 커밋해주는 역할
                .reader(beforeReader())
                .processor(afterProcessor())
                .writer(afterWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<UsersEntity> beforeReader() {
        return new RepositoryItemReaderBuilder<UsersEntity>()
                .name("ItemReader")
                .pageSize(10)
                .methodName("findAll")
                .repository(userRepository)
                .sorts(Map.of("userId", Sort.Direction.ASC))
                .build();

    }

    @Bean
    public ItemProcessor<Object, Object> afterProcessor() {
        return new ItemProcessor<>() {
            @Override
            public Object process(@Nullable Object item) throws Exception {
                if (item instanceof UsersEntity user) {
                    // 현재 날짜 기준으로 1개월 이전 날짜 계산
                    LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
                    // Date -> LocalDate 변환
                    LocalDate createdDate = user.getCreatedAt()
                            .toInstant(ZoneOffset.UTC)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    if (!user.isDeleted() && user.isNewUser()&&createdDate.isBefore(oneMonthAgo)) {
                        user.setDeleted(true);
                        user.setAccountStatus(AccountStatus.DELETED);

                    }
                    return user;
                }
                return item;
            }
        };
    }


    @Bean
    public ItemWriter<Object> afterWriter() {
        return items -> {
            for (Object item : items) {
                if (item instanceof UsersEntity) {
                    userRepository.save((UsersEntity) item);
                    userRepository.flush();
                } else {
                    throw new IllegalArgumentException("Unsupported item type: " + item.getClass().getName());
                }
            }
        };
    }


}
