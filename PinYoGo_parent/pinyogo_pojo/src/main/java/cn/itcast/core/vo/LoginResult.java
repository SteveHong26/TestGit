package cn.itcast.core.vo;

public class LoginResult {

    private Boolean success;
    private String loginName;
    private Object data;

    public LoginResult(Boolean success, String loginName, Object data) {
        this.success = success;
        this.loginName = loginName;
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LoginResult{" +
                "success=" + success +
                ", loginName='" + loginName + '\'' +
                ", data=" + data +
                '}';
    }
}
