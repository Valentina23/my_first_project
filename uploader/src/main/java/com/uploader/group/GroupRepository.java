package com.uploader.group;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface GroupRepository extends PagingAndSortingRepository<GroupEntity, Long> {

    GroupEntity findGroupByGroupname(String groupname);
}
