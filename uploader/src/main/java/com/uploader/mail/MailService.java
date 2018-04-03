package com.uploader.mail;

import com.uploader.user.UserEntity;
import com.uploader.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MailService {

    private final UserRepository userRepository;

    @Autowired
    private JavaMailSender emailSender;

    public MailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void sendMessage(final Mail mail){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(mail.getSubject());
        message.setText(mail.getContent());
        message.setTo(mail.getTo());
        message.setFrom(mail.getFrom());

        emailSender.send(message);
    }

    public List<UserEntity> findUsersTo(String username) {
        UserEntity currentUser = userRepository.findUserByLogin(username);

        // TODO: add correct select users by group
        Iterable<UserEntity> users = userRepository.findAll();
        List<UserEntity> listOfUsers = new ArrayList<>();

        for (UserEntity user : users) {
            if (user.getGroup().getGroupId() == currentUser.getGroup().getGroupId()
                    && user.getLogin() != currentUser.getLogin()) {

            listOfUsers.add(user);
            }
        }
        return listOfUsers;
    }

}
