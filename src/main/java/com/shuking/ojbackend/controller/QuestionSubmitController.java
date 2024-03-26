package com.shuking.ojbackend.controller;

import com.shuking.ojbackend.common.BaseResponse;
import com.shuking.ojbackend.common.ErrorCode;
import com.shuking.ojbackend.common.ResultUtils;
import com.shuking.ojbackend.exception.ThrowUtils;
import com.shuking.ojbackend.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.shuking.ojbackend.service.QuestionService;
import com.shuking.ojbackend.service.QuestionSubmitService;
import com.shuking.ojbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/submit")
@Slf4j
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService submitService;

    /**
     * 执行提交操作
     * @param submitAddRequest  dto
     * @param request   http
     */
    @PostMapping("/do")
    public BaseResponse<Long> doSubmit(@RequestBody QuestionSubmitAddRequest submitAddRequest, HttpServletRequest request){
        ThrowUtils.throwIf(submitAddRequest==null, ErrorCode.PARAMS_ERROR,"请求对象为空");

        return ResultUtils.success(submitService.doSubmit(submitAddRequest, request));
    }
}
