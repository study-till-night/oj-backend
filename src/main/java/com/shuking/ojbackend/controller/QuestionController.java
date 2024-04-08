package com.shuking.ojbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shuking.ojbackend.common.BaseResponse;
import com.shuking.ojbackend.common.ErrorCode;
import com.shuking.ojbackend.common.ResultUtils;
import com.shuking.ojbackend.exception.BusinessException;
import com.shuking.ojbackend.exception.ThrowUtils;
import com.shuking.ojbackend.model.dto.question.QuestionAddRequest;
import com.shuking.ojbackend.model.dto.question.QuestionEditRequest;
import com.shuking.ojbackend.model.dto.question.QuestionQueryRequest;
import com.shuking.ojbackend.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.shuking.ojbackend.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.shuking.ojbackend.model.entity.Question;
import com.shuking.ojbackend.model.entity.QuestionSubmit;
import com.shuking.ojbackend.model.entity.User;
import com.shuking.ojbackend.model.vo.QuestionSubmitVO;
import com.shuking.ojbackend.model.vo.QuestionVO;
import com.shuking.ojbackend.service.QuestionService;
import com.shuking.ojbackend.service.QuestionSubmitService;
import com.shuking.ojbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Resource
    private QuestionSubmitService submitService;

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param questionAddRequest dto
     * @param request            http
     * @return 新问题id
     */
    @PostMapping("/add")
    @Operation(summary = "新增问题")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long newQuestionId = questionService.addQuestion(questionAddRequest, request);

        return ResultUtils.success(newQuestionId);
    }

    /**
     * 根据 id 获取
     *
     * @param id 问题id
     * @return 脱敏后问题对象
     */
    @GetMapping("/get/vo")
    @Operation(summary = "查询single问题")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        // 过滤非法id
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param questionQueryRequest dto
     * @param request              http
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                               HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param questionQueryRequest dto
     * @param request              http
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 编辑（用户）
     *
     * @param questionEditRequest dto
     * @param request             http
     */
    @PostMapping("/edit")
    @Operation(summary = "编辑问题")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求为空或id不合法");
        }

        boolean result = questionService.updateQuestion(questionEditRequest, request);
        return ResultUtils.success(result);
    }
    // endregion


    /**
     * 执行提交操作
     *
     * @param submitAddRequest dto
     * @param request          http
     */
    @PostMapping("/question_submit/do")
    public BaseResponse<Long> doSubmit(@RequestBody QuestionSubmitAddRequest submitAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(submitAddRequest == null, ErrorCode.PARAMS_ERROR, "请求对象为空");

        return ResultUtils.success(submitService.doSubmit(submitAddRequest, request));
    }

    /**
     * 分页查询
     *
     * @param questionSubmitQueryRequest dto
     * @param request                    http
     */
    @PostMapping("/question_submit/list/page")
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