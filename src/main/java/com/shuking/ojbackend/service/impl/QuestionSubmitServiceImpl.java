package com.shuking.ojbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuking.ojbackend.common.ErrorCode;
import com.shuking.ojbackend.exception.ThrowUtils;
import com.shuking.ojbackend.mapper.QuestionSubmitMapper;
import com.shuking.ojbackend.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.shuking.ojbackend.model.entity.Question;
import com.shuking.ojbackend.model.entity.QuestionSubmit;
import com.shuking.ojbackend.service.QuestionService;
import com.shuking.ojbackend.service.QuestionSubmitService;
import com.shuking.ojbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author HP
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2024-03-25 14:51:38
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    private UserService userService;

    @Resource
    private QuestionService questionService;

    /**
     * 提交做题记录
     */
    @Override
    public Long doSubmit(QuestionSubmitAddRequest submitAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(submitAddRequest == null, ErrorCode.PARAMS_ERROR, "请求对象为空");
        // todo 使用enum校验语言是否合规
        String code = submitAddRequest.getCode();
        Long questionId = submitAddRequest.getQuestionId();
        // 校验代码及题目id合法性
        ThrowUtils.throwIf(StringUtils.isBlank(code), ErrorCode.PARAMS_ERROR, "运行代码不得为空");
        ThrowUtils.throwIf(questionService.count(new LambdaQueryWrapper<Question>()
                .eq(Question::getId, questionId)) == 0, ErrorCode.PARAMS_ERROR, "提交的题目不存在");

        QuestionSubmit newSubmit = new QuestionSubmit();
        BeanUtils.copyProperties(submitAddRequest, newSubmit);

        newSubmit.setJudgeInfo("{}");

        Long loginUserId = userService.getLoginUser(request).getId();
        newSubmit.setUserId(loginUserId);

        boolean saveRes = this.save(newSubmit);
        ThrowUtils.throwIf(!saveRes, ErrorCode.SYSTEM_ERROR, "数据库提交做题记录失败");
        return newSubmit.getId();
    }
}




