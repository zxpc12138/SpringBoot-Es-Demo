package com.example.springbootesdemo.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:钟湘
 * @data: 11:00
 */

public class Page {
    private Integer pageNo;//当前页
    private Integer pageSize;//每页显示的数据
    private Integer pageCount;//总页数
    private Integer pageSizeSum;//总数据
    private List<FileEntiry> files=new ArrayList<>();

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getPageSizeSum() {
        return pageSizeSum;
    }

    public void setPageSizeSum(Integer pageSizeSum) {
        this.pageSizeSum = pageSizeSum;
    }

    public List<FileEntiry> getFiles() {
        return files;
    }

    public void setFiles(List<FileEntiry> files) {
        this.files = files;
    }
}
