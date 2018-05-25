package hx.data.mybatis.utils;

import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @Author mingliang
 * @Date 2017-08-28 10:10
 *  implements Serializable
 *  extends ResponseEntity<T>
 *extends PageInfo<T>
 */

public class PageResultInfo<T> extends PageInfo<T>{

    private String errorCode;
    private String message;
    private Boolean success;
    private List<T> dataMain;
    private int status;
    private String memo;

    public PageResultInfo() {
    }

    public PageResultInfo(List<T> list) {
        super(list);
        this.errorCode = "";
        this.message = "";
        this.success = true;
        this.status = HttpStatus.OK.value();
    }

    public PageResultInfo(List<T> list, int navigatePages) {
        super(list, navigatePages);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<T> getDataMain() {
        return dataMain;
    }

    public void setDataMain(List<T> dataMain) {
        this.dataMain = dataMain;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
