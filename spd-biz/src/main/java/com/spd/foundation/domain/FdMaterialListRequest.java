package com.spd.foundation.domain;

/**
 * 产品档案列表查询请求（POST body，避免 URL 过长导致 400）
 *
 * @author spd
 */
public class FdMaterialListRequest {

    /** 页码 */
    private Integer pageNum = 1;
    /** 每页条数 */
    private Integer pageSize = 10;
    /** 查询条件 */
    private FdMaterial query;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public FdMaterial getQuery() {
        return query;
    }

    public void setQuery(FdMaterial query) {
        this.query = query;
    }
}
