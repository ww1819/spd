package com.spd.system.dto;

import java.io.Serializable;

/**
 * 租户批量修改密码请求
 */
public class BatchPasswordRequest implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String password;

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
