package com.manager.nacelle_rent.utils;

public class PageQueryUtil {
    private Integer pageSize = 5;
    private Integer pageIndex = 1;
    private Integer offset = 0;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        if (this.pageIndex != null) {
            this.offset = this.pageSize * (this.pageIndex - 1);
        }
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
        if (this.pageSize != null) {
            this.offset = this.pageSize * (this.pageIndex - 1);
        }
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
