package com.uploader.mail;

import com.uploader.UploaderApplication;
import com.uploader.user.UserEntity;
import com.uploader.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Service
public class MailService {

    private final UserRepository userRepository;

    @Autowired
    private JavaMailSender emailSender;

    private Logger log = LoggerFactory.getLogger(UploaderApplication.class);

    @Autowired
    private MailService mailService;

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

    public String sendEmail(String filename, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userFrom = userRepository.findUserByLogin(auth.getPrincipal().toString());

        List<UserEntity> listOfUsers = mailService.findUsersTo(auth.getPrincipal().toString());

        // TODO: add correct userTo
        UserEntity userTo = listOfUsers.get(0);

        log.info("Sending email about uploading files to " + userTo.getFirstname() + " " + userTo.getLastname());

        Mail mail = new Mail();
        mail.setFrom("vvo.082015@gmail.com");
        mail.setTo(userTo.getEmail().toString());
        mail.setSubject("Uploading new files in your's group");

        mail.setContent("Уважаемый(-ая) " + userTo.getFirstname() + " " + userTo.getLastname() + " ! \n\n"
                + "Уведомляем Вас о том, что пользователем " + userFrom.getLogin() + " Вашей группы "
                + userFrom.getGroup().getGroupname() + " был загружен следующий файл: \n" + filename + " . \n\n"
                + "С уважением, \n" + "Uploader.");
        try {
            mailService.sendMessage(mail);

            redirectAttributes.addFlashAttribute("flash.message", "Email sent to " + userTo.getFirstname()
                    + " " + userTo.getLastname() + " successfully!");
            redirectAttributes.addFlashAttribute("flash.messageType", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("flash.message", "Error in sending email to " + userTo.getFirstname()
                    + " " + userTo.getLastname() + " : " + e.getMessage());
            redirectAttributes.addFlashAttribute("flash.messageType", "alert-danger");
        }
        return "redirect:/";
    }
}
