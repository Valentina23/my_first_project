package com.uploader.file;

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

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @RequestMapping(value = "/")
    public String file(Model model, Pageable pageable) {
        final Page<FileEntity> page = fileService.findPage(pageable);
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
            return ResponseEntity.badRequest().body("Couldn't find " + filename + " => " + e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = BASE_PATH)
    public String createFile(MultipartFile file, RedirectAttributes redirectAttributes) {

        if (file == null || file.getSize() == 0 || file.getOriginalFilename().equals("")) {
            return "redirect:/";
        }

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            fileService.createFile(file, auth.getPrincipal().toString());

            redirectAttributes.addFlashAttribute("flash.message", "Successfully uploaded "
                    + file.getOriginalFilename());
            redirectAttributes.addFlashAttribute("flash.messageType", "alert-success");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("flash.message", "Failed to upload "
                    + file.getOriginalFilename() + " => " + e.getMessage());
            redirectAttributes.addFlashAttribute("flash.messageType", "alert-danger");
        }

        return "redirect:/";
    }
}
