package com.example.springbootesdemo.entity;

import org.springframework.data.elasticsearch.annotations.*;

/**
 * @author:钟湘
 * @data: 8:46
 */
@Document(indexName = "file")
@Setting(shards = 5,replicas = 3)//设置分片
//@Mapping(mappingPath="mapper/myDocument.json")
public class FileEntiry {
    @Field(type = FieldType.Long,store = true)
    private Long fileSize;//文件长度
    @Field(type = FieldType.Text,store = true,analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String fileName;//文件名字
    @Field(type = FieldType.Text,store = true)
    private String fileUrl;//文件路径
    @Field(type = FieldType.Text,store = true,analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String fileContent;//文件内容

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    @Override
    public String toString() {
        return "FileEntiry{" +
                "fileSize=" + fileSize +
                ", fileName='" + fileName + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", fileContent='" + fileContent + '\'' +
                '}';
    }
}
