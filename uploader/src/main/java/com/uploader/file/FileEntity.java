package com.uploader.file;


import com.uploader.group.GroupEntity;
import com.uploader.user.UserEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "file")
public class FileEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long fileId;

    @Column
    private String filename;

    @Column
    private long size;

    @Column
    private String format;

    @Column
    private Date uploadDate;

    @Column
    private String path;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupEntity groupId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userId;

    public FileEntity() {}

    public FileEntity(String filename, long size, String format, Date uploadDate, String path, UserEntity userId) {
        this.filename = filename;
        this.size = size;
        this.format = format;
        this.uploadDate = uploadDate;
        this.path = path;
        this.groupId = userId.getGroup();
        this.userId = userId;
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public GroupEntity getGroupId() {
        return groupId;
    }

    public void setGroupId(GroupEntity groupId) {
        this.groupId = groupId;
    }

    public UserEntity getUserId() {
        return userId;
    }

    public void setUserId(UserEntity userId) {
        this.userId = userId;
    }
}
