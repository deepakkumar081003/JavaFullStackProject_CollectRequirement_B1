package com.ford.collectionRequirements.repository;

import com.ford.collectionRequirements.user.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

}
