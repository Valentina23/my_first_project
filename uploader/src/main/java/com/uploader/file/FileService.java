package com.uploader.file;

import com.uploader.group.GroupEntity;
import com.uploader.group.GroupRepository;
import com.uploader.user.UserEntity;
import com.uploader.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

@Service
public class FileService {

    private static String UPLOAD_ROOT = "upload-dir";

    private final FileRepository fileRepository;

    private final UserRepository userRepository;

    private final GroupRepository groupRepository;

    @Autowired
    public FileService(FileRepository fileRepository, UserRepository userRepository, GroupRepository groupRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    public Page<FileEntity> findPage(Pageable pageable) {
        return fileRepository.findAll(pageable);
    }

    public File findOneFile(String filename) {
        return new File(Paths.get(UPLOAD_ROOT).toString() + "/" + filename);
    }

    public void createFile(MultipartFile file) throws IOException {
        if (!file.isEmpty()) {

            GroupEntity groupEntity = groupRepository.findGroupByGroupname("group1");
            List<UserEntity> users = userRepository.findUsersByGroup(groupEntity);
            UserEntity user = users.get(0);

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
            return "";
        }
    }

    private String getFilename(String fullFilename) {
        try{
            return fullFilename.substring(0, fullFilename.lastIndexOf("."));
        } catch (Exception e) {
            return fullFilename;
        }
    }
}
