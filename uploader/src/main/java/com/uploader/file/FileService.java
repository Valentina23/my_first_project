package com.uploader.file;

import com.uploader.group.GroupEntity;
import com.uploader.user.UserEntity;
import com.uploader.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FileService {

    private static String UPLOAD_ROOT = "upload-dir";

    private final FileRepository fileRepository;

    private final UserRepository userRepository;

    private Logger log = LoggerFactory.getLogger(FileService.class);

    @Autowired
    public FileService(FileRepository fileRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    public Page<FileEntity> findPage(Pageable pageable, String login) {
        UserEntity user = userRepository.findUserByLogin(login);
        GroupEntity group = user.getGroup();

        Page<FileEntity> files  = fileRepository.findAll(pageable);
        List<FileEntity> list = new ArrayList<>();
        for (FileEntity file : files) {
            if (file.getGroup().getGroupId() == group.getGroupId()) {
                list.add(file);
            }
        }

        return new PageImpl<>(list, pageable, list.size());
    }

    public File findOneFile(String filename) {
        return new File(Paths.get(UPLOAD_ROOT).toString() + "/" + filename);
    }

    public void createFile(MultipartFile file, String login) throws IOException {
        if (!file.isEmpty()) {
            UserEntity user = userRepository.findUserByLogin(login);

            String filename = getFilename(file.getOriginalFilename());
            String fileExtension = getFileExtension(file.getOriginalFilename());

            Files.copy(file.getInputStream(), Paths.get(UPLOAD_ROOT, file.getOriginalFilename()));
            fileRepository.save(new FileEntity(filename, file.getSize(), fileExtension, new Date(),
                    UPLOAD_ROOT + "/" + filename, user));
        }
    }

    private String getFileExtension(String fullFilename) {
        try {
            return fullFilename.substring(fullFilename.lastIndexOf(".") + 1);
        } catch (Exception e) {
            log.error("Unable to get file extension!", e);

            return "";
        }
    }

    private String getFilename(String fullFilename) {
        try{
            return fullFilename.substring(0, fullFilename.lastIndexOf("."));
        } catch (Exception e) {
            log.error("Unable to get filename!", e);
            return fullFilename;
        }
    }
}
