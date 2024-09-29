package com.mentit.mento.domain.dotoriToken.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mentit.mento.domain.users.entity.Users;
import com.mentit.mento.global.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Entity
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = " update dotori_token set is_deleted = true where user_id = ?")
public class DotoriToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dotoriTokenId;

    @Min(value = 0L,message = "token은 음수가 될 수 없습니다.")
    private int count;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Users users;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = Boolean.FALSE;

}
