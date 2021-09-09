package com.haha.im.controller;

import com.haha.im.model.VO.NextMsgIdParam;
import com.haha.im.model.VO.NextMsgIdVo;
import com.haha.im.service.UserMsgIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/msg_id")
public class IdController {

    @Autowired
    private UserMsgIdService userMsgIdService;

    // todo 使用token或jwt方式获取用户信息
    @PostMapping("/next_id")
    public NextMsgIdVo getNextId(@RequestBody NextMsgIdParam param) {
        NextMsgIdVo nextMsgIdVo = new NextMsgIdVo();
        nextMsgIdVo.setMsgId(userMsgIdService.getNextMsgId(param.getUserId()));
        nextMsgIdVo.setUserId(param.getUserId());
        return nextMsgIdVo;
    }
}
