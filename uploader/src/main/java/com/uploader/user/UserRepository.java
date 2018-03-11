package com.uploader.user;

import com.uploader.group.GroupEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {

    List<UserEntity> findUsersByGroup(GroupEntity group);
}
