package com.uploader.file;

import com.uploader.mail.EmailSendException;
import com.uploader.mail.MailService;
import com.uploader.user.UserEntity;
import com.uploader.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
public class FileController {

    private static final String BASE_PATH = "/files";
    private static final String FILENAME = "{filename:.+}";

    private final FileService fileService;

    private final MailService mailService;

    private final UserRepository userRepository;

    private Logger log = LoggerFactory.getLogger(FileController.class);

    @Autowired
    public FileController(FileService fileService, MailService mailService, UserRepository userRepository) {
        this.fileService = fileService;
        this.mailService = mailService;
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/")
    public String file(Pageable pageable, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        final Page<FileEntity> page = fileService.findPage(pageable, auth.getPrincipal().toString());

        model.addAttribute("page", page);
        if (page.hasPrevious()) {
            model.addAttribute("prev", pageable.previousOrFirst());
        }
        if (page.hasNext()) {
            model.addAttribute("next", pageable.next());
        }

        return "file";

    }

    @RequestMapping(method = RequestMethod.GET, value = BASE_PATH + "/" + FILENAME)
    @ResponseBody
    public ResponseEntity<?> downloadFile(@PathVariable String filename) {
        try {
            File file = fileService.findOneFile(filename);

            return ResponseEntity.ok()
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(new FileInputStream(file)));
        } catch (IOException e) {
            log.error("Unable to download file!", e.getMessage());

            return ResponseEntity.badRequest().body("Couldn't find " + filename + " .");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = BASE_PATH)
    public String createFile(MultipartFile file, RedirectAttributes redirectAttributes) {

        if (file == null || file.getSize() == 0 || file.getOriginalFilename().equals("")) {
            return "redirect:/";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userFrom = userRepository.findUserByLogin(auth.getPrincipal().toString());

        try {

            fileService.createFile(file, auth.getPrincipal().toString());

            String result = mailService.sendEmail(file.getOriginalFilename());

            redirectAttributes.addFlashAttribute("flash.message", "Successfully uploaded "
                    + file.getOriginalFilename() + "!" + result);
            redirectAttributes.addFlashAttribute("flash.messageType", "alert-success");

            redirectAttributes.addFlashAttribute("flash.action", "inAction");
        } catch (IOException e) {
            log.error("Unable to upload file!", e.getMessage());

            redirectAttributes.addFlashAttribute("flash.message", "Failed to upload "
                    + file.getOriginalFilename() + " .");
            redirectAttributes.addFlashAttribute("flash.messageType", "alert-danger");

            redirectAttributes.addFlashAttribute("flash.action", "inAction");
        } catch (EmailSendException e) {
            log.error("Unable to send email to group " + userFrom.getGroup().getGroupname() + " !", e.getMessage());

            redirectAttributes.addFlashAttribute("flash.message", "Successfully uploaded "
                    + file.getOriginalFilename() + " , but failed to send email to group " + userFrom.getGroup().getGroupname()
                    + " .");
            redirectAttributes.addFlashAttribute("flash.messageType", "alert-danger");

            redirectAttributes.addFlashAttribute("flash.action", "inAction");
        }

        return "redirect:/";
    }
}
