package com.uploader.mail;

import com.uploader.UploaderApplication;
import com.uploader.user.UserEntity;
import com.uploader.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class MailController {

    private Logger log = LoggerFactory.getLogger(UploaderApplication.class);

    @Autowired
    private MailService mailService;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/email")
    @ResponseBody
    public String home(final Mail mail) {
        try {
            MailService mailService = new MailService(userRepository);
            mailService.sendMessage(mail);
            return "Email Sent!";
        }catch(Exception ex) {
            return "Error in sending email: "+ex;
        }
    }

    @RequestMapping("/sendingEmail")
    public void sendEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userFrom = userRepository.findUserByLogin(auth.getPrincipal().toString());

        List<UserEntity> listOfUsers = mailService.findUsersTo(auth.getPrincipal().toString());

        // TODO: add correct userTo
        UserEntity userTo = listOfUsers.get(0);

        log.info("Sending email about uploading files");

        Mail mail = new Mail();
        mail.setFrom("vvo.082015@gmail.com");
        mail.setTo(userTo.getEmail().toString());
        mail.setSubject("Uploading new files in your's group");

        // TODO: add correct files info
        mail.setContent("Уважаемый(-ая) " + userTo.getFirstname() + " " + userTo.getLastname() + " ! \n\n"
                + " Уведомляем Вас о том, что пользователем " + userFrom.getLogin() + " Вашей группы "
                + userFrom.getGroup().getGroupname() + " был загружен следующий файл: \n" + "TEST_FILENAME" + " . \n\n"
                + " С уважением, \n" + " Uploader.");

        mailService.sendMessage(mail);
    }

}
