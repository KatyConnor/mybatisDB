package hx.data.mybatis.param;

/**
 * @Author mingliang
 * @Date 2018-03-02 18:02
 */
public abstract class PageParam {

    private int pageNum = 1;
    private int pageSize = 20;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
