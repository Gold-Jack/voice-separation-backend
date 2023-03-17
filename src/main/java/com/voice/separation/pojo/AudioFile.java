package com.voice.separation.pojo;

import cn.hutool.core.date.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "audioFile")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AudioFile {

    @Id
    private String audioId;

    private String audioName;
    private Binary binary;
    private String owner;
    private String downloadUrl;
    private Date createTime;
    private Date updateTime;

    public AudioFile(String audioName, String owner, Binary binary) {
        this.audioName = audioName;
        this.owner = owner;
        this.binary = binary;
        this.setCreateTime(new Date(DateUtil.current()));
        this.setUpdateTime(new Date(DateUtil.current()));
    }
}
