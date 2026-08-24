package com.spd.web.dto;

import java.io.Serializable;

/**
 * 用户消息提醒权限授权请求体
 */
public class UserMessageReminderGrantBody implements Serializable {

    private static final long serialVersionUID = 1L;

    /** warehouse / department / data：可见菜单 */
    private String[] messageReminderKeys;

    /** warehouse / department / data：登录后自动弹窗（须为 keys 子集） */
    private String[] messageReminderPopupKeys;

    public String[] getMessageReminderKeys() {
        return messageReminderKeys;
    }

    public void setMessageReminderKeys(String[] messageReminderKeys) {
        this.messageReminderKeys = messageReminderKeys;
    }

    public String[] getMessageReminderPopupKeys() {
        return messageReminderPopupKeys;
    }

    public void setMessageReminderPopupKeys(String[] messageReminderPopupKeys) {
        this.messageReminderPopupKeys = messageReminderPopupKeys;
    }
}
