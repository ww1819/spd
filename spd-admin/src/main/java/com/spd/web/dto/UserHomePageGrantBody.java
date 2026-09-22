package com.spd.web.dto;

/**
 * 用户首页设置授权请求体：simple=简洁，full=完整；空数组表示未单独授权（前端默认完整）。
 */
public class UserHomePageGrantBody
{
    private String[] homePageKeys;

    public String[] getHomePageKeys()
    {
        return homePageKeys;
    }

    public void setHomePageKeys(String[] homePageKeys)
    {
        this.homePageKeys = homePageKeys;
    }
}
