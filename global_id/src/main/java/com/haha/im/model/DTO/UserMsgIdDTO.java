package com.haha.im.model.DTO;

import com.haha.im.model.DO.UserMsgMaxIdDo;

public class UserMsgIdDTO extends UserMsgMaxIdDo {
    private Long curId;

    public Long getCurId() {
        return curId;
    }

    public void setCurId(Long curId) {
        this.curId = curId;
    }
}
