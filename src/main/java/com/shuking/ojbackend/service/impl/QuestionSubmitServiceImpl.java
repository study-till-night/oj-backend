package com.shuking.ojbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuking.ojbackend.common.ErrorCode;
import com.shuking.ojbackend.constant.CommonConstant;
import com.shuking.ojbackend.exception.BusinessException;
import com.shuking.ojbackend.exception.ThrowUtils;
import com.shuking.ojbackend.judge.JudgeServiceImpl;
import com.shuking.ojbackend.mapper.QuestionSubmitMapper;
import com.shuking.ojbackend.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.shuking.ojbackend.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.shuking.ojbackend.model.entity.Question;
import com.shuking.ojbackend.model.entity.QuestionSubmit;
import com.shuking.ojbackend.model.entity.User;
import com.shuking.ojbackend.model.enums.QuestionSubmitLanguageEnum;
import com.shuking.ojbackend.model.enums.QuestionSubmitStatusEnum;
import com.shuking.ojbackend.model.vo.QuestionSubmitVO;
import com.shuking.ojbackend.service.QuestionService;
import com.shuking.ojbackend.service.QuestionSubmitService;
import com.shuking.ojbackend.service.UserService;
import com.shuking.ojbackend.utils.SqlUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author HP
 * &#064;description  针对表【question_submit(题目提交)】的数据库操作Service实现
 * &#064;createDate  2024-03-25 14:51:38
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    private UserService userService;

    @Resource
    private QuestionService questionService;

    @Resource
    @Lazy   //  应对循环Bean依赖
    private JudgeServiceImpl judgeService;

    /**
     * 提交做题记录
     */
    @Override
    public Long doSubmit(QuestionSubmitAddRequest submitAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(submitAddRequest == null, ErrorCode.PARAMS_ERROR, "请求对象为空");

        String language = submitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
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

        // 异步执行判题操作
        CompletableFuture.runAsync(()->{
            judgeService.doJudge(newSubmit.getId());
        });
        return newSubmit.getId();
    }

    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到 mybatis 框架支持的查询 QueryWrapper 类）
     *
     * @param questionSubmitQueryRequest    查询对象
     * @return  wrapper
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    // 直接以User对象作为方法形参 减少数据库IO耗时
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏：仅本人和管理员能看见自己（提交 userId 和登录用户 id 不同）提交的代码
        long userId = loginUser.getId();
        // 处理脱敏
        if (userId != questionSubmit.getUserId() && !userService.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    // 直接以User对象作为方法形参 减少数据库IO耗时
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }
}