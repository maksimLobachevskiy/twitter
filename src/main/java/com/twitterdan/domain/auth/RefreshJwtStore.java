package com.twitterdan.domain.auth;

import com.twitterdan.domain.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "refresh_jwt_store")
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class RefreshJwtStore extends BaseEntity {

  @Column(nullable = false)
  private String login;
  @Column(nullable = false)
  private String refreshToken;
}
