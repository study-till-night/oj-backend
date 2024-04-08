package com.shuking.ojbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shuking.ojbackend.common.BaseResponse;
import com.shuking.ojbackend.common.ErrorCode;
import com.shuking.ojbackend.common.ResultUtils;
import com.shuking.ojbackend.exception.ThrowUtils;
import com.shuking.ojbackend.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.shuking.ojbackend.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.shuking.ojbackend.model.entity.QuestionSubmit;
import com.shuking.ojbackend.model.entity.User;
import com.shuking.ojbackend.model.vo.QuestionSubmitVO;
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
@Deprecated
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService submitService;

    @Resource
    private UserService userService;

    /**
     * 执行提交操作
     *
     * @param submitAddRequest dto
     * @param request          http
     */
    @PostMapping("/do")
    public BaseResponse<Long> doSubmit(@RequestBody QuestionSubmitAddRequest submitAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(submitAddRequest == null, ErrorCode.PARAMS_ERROR, "请求对象为空");

        return ResultUtils.success(submitService.doSubmit(submitAddRequest, request));
    }

    /**
     * 分页查询
     * @param questionSubmitQueryRequest    dto
     * @param request   http
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                         HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 从数据库中查询原始的题目提交分页信息
        Page<QuestionSubmit> questionSubmitPage = submitService.page(new Page<>(current, size),
                submitService.getQueryWrapper(questionSubmitQueryRequest));
        final User loginUser = userService.getLoginUser(request);
        // 返回脱敏信息
        return ResultUtils.success(submitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));
    }
}
