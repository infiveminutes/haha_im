package com.haha.im.service.impl;

import com.haha.im.dao.UserMsgMaxIdDao;
import com.haha.im.model.DO.UserMsgMaxIdDo;
import com.haha.im.service.UserMsgIdService;
import com.haha.im.service.UserMsgMaxIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserMsgMaxIdServiceImpl implements UserMsgMaxIdService {

    @Autowired
    private UserMsgMaxIdDao userMsgMaxIdDao;


    @Override
    public Long getUserMsgMaxIdFromDb(String userId) {
        if(userId == null) {
            return null;
        }
        UserMsgMaxIdDo userMsgMaxIdDo = userMsgMaxIdDao.getByUserId(userId);
        if(userMsgMaxIdDo == null) {
            userMsgMaxIdDo = new UserMsgMaxIdDo();
            userMsgMaxIdDo.setUserId(userId);
            userMsgMaxIdDo.setMaxId(0L);
            userMsgMaxIdDao.insert(userMsgMaxIdDo);
            return 0L;
        }
        return userMsgMaxIdDo.getMaxId();
    }

    @Override
    public boolean setMaxId(String userId, long maxId) {

        return false;
    }
}
