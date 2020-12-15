package org.jasig.cas.authentication.model;

import java.io.Serializable;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author qinpan
 * @version 1.0
 * @date 2020/10/27 8:38 下午
 */
@Component
@ConfigurationProperties(prefix = "sso.jdbc")
public class DataBaseProperties implements Serializable {

    private String user;

    private String password;

    private String driverClass;

    private String url;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
