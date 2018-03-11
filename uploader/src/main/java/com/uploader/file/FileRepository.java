package com.uploader.file;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface FileRepository extends PagingAndSortingRepository<FileEntity, Long> {

    FileEntity findFileByFilename(String filename);
}
