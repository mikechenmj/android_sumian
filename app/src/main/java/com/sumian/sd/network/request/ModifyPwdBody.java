package com.sumian.sd.network.request;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:
 */

public class ModifyPwdBody {

    private String old_password;
    private String password;
    private String password_confirmation;

    public String getOld_password() {
        return old_password;
    }

    public ModifyPwdBody setOld_password(String old_password) {
        this.old_password = old_password;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public ModifyPwdBody setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getPassword_confirmation() {
        return password_confirmation;
    }

    public ModifyPwdBody setPassword_confirmation(String password_confirmation) {
        this.password_confirmation = password_confirmation;
        return this;
    }

    @Override
    public String toString() {
        return "ModifyPwdBody{" +
            "old_password='" + old_password + '\'' +
            ", password='" + password + '\'' +
            ", password_confirmation='" + password_confirmation + '\'' +
            '}';
    }
}
