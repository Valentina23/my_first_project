package com.uploader.group;

import com.uploader.user.UserEntity;
import com.uploader.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {

    private final UserRepository userRepository;

    @Autowired
    public GroupService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserEntity> findPageByUser(Pageable pageable, String username) {
        UserEntity currentUser = userRepository.findUserByLogin(username);

        // TODO: add correct select users by group
        Page<UserEntity> users = userRepository.findAll(pageable);
        List<UserEntity> list = new ArrayList<>();
        for (UserEntity user : users) {
            if (user.getGroup().getGroupId() == currentUser.getGroup().getGroupId()) {
                list.add(user);
            }
        }

        return new PageImpl<>(list, pageable, list.size());
    }
}
