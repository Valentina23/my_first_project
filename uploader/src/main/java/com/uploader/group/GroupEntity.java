package com.uploader.group;

import javax.persistence.*;

@Entity
@Table(name = "group_info")
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private long groupId;

    @Column
    private String groupname;

    public GroupEntity() {}

    public GroupEntity(String groupname) {
        this.groupname = groupname;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }
}
