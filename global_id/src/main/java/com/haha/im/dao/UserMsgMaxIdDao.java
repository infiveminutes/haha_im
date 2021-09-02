package com.haha.im.dao;

import com.haha.im.model.DO.UserMsgMaxIdDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMsgMaxIdDao {
    UserMsgMaxIdDo getByUserId(@Param("userId") String userId);
    int updateMaxId(UserMsgMaxIdDo userMsgMaxIdDo);
    int insert(UserMsgMaxIdDo userMsgMaxIdDo);
}
