package com.shuking.ojbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.shuking.ojbackend.common.ErrorCode;
import com.shuking.ojbackend.constant.CommonConstant;
import com.shuking.ojbackend.exception.BusinessException;
import com.shuking.ojbackend.exception.ThrowUtils;
import com.shuking.ojbackend.mapper.QuestionMapper;
import com.shuking.ojbackend.model.dto.question.*;
import com.shuking.ojbackend.model.entity.Question;
import com.shuking.ojbackend.model.entity.User;
import com.shuking.ojbackend.model.vo.QuestionVO;
import com.shuking.ojbackend.service.QuestionService;
import com.shuking.ojbackend.service.UserService;
import com.shuking.ojbackend.utils.SqlUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author HP
 * @description 针对表【question(题目)】的数据库操作Service实现
 * @createDate 2024-03-25 14:51:38
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {

    private final static Gson GSON = new Gson();

    @Resource
    private UserService userService;

    /**
     * 判断问题合法性
     *
     * @param question 问题
     * @param add      是否为新增操作
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        ThrowUtils.throwIf(question == null, ErrorCode.PARAMS_ERROR);

        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();

        // 若为添加操作 则追加判断
        ThrowUtils.throwIf(add && StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR, "创建时标题、内容、标签不得为空");

        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && answer.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && judgeCase.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }
    }

    /**
     * 根据请求对象获取QueryWrapper对象
     *
     * @return wrapper
     */
    @Override
    public Wrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        ThrowUtils.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);

        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        Long userId = questionQueryRequest.getUserId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();

        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(userId != null, "userId", userId);
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        // 对标签字段进行模糊匹配
        if (!CollectionUtils.isEmpty(tags)) {
            for (String tag : tags) {
                // 对json字符串进行转移化匹配
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 设置排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        return null;
    }

    /**
     * 获取问题脱敏对象
     *
     * @param question 问题
     * @param request  http
     * @return vo
     */
    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        ThrowUtils.throwIf(question == null, ErrorCode.PARAMS_ERROR);

        QuestionVO questionVO = QuestionVO.objToVo(question);
        User loginUser = userService.getLoginUser(request);
        // 设置UserVo成员
        if (loginUser != null)
            questionVO.setUserVO(userService.getUserVO(loginUser));
        return questionVO;
    }

    /**
     * 添加问题
     *
     * @param questionAddRequest dto
     * @param request            http
     * @return
     */
    @Override
    public Long addQuestion(QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);

        List<String> tags = questionAddRequest.getTags();
        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        List<JudgeCase> judgeCase = questionAddRequest.getJudgeCase();

        objFieldToJson(question, tags, judgeConfig, judgeCase);

        // 校验问题参数合法性
        this.validQuestion(question, true);
        Long currentUserId = userService.getLoginUser(request).getId();
        question.setUserId(currentUserId);

        ThrowUtils.throwIf(!this.save(question), ErrorCode.SYSTEM_ERROR, "保存问题至数据库失败");

        return question.getId();
    }

    /**
     * 编辑问题
     *
     * @param questionEditRequest dto
     * @param request             http
     * @return
     */
    @Override
    public boolean updateQuestion(QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);

        List<String> tags = questionEditRequest.getTags();
        List<JudgeCase> judgeCase = questionEditRequest.getJudgeCase();
        JudgeConfig judgeConfig = questionEditRequest.getJudgeConfig();

        objFieldToJson(question, tags, judgeConfig, judgeCase);

        // 参数校验
        this.validQuestion(question, false);
        User loginUser = userService.getLoginUser(request);
        long id = questionEditRequest.getId();
        // 判断是否存在
        Question oldQuestion = this.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return true;
    }

    /**
     * 将对象字段转为json字符串
     *
     * @param question
     * @param tags
     * @param judgeConfig
     * @param judgeCase
     */
    private void objFieldToJson(Question question, List<String> tags, JudgeConfig judgeConfig, List<JudgeCase> judgeCase) {
        // 判断标签
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        } else throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签不得为空!");
        // 判断测试用例
        if (!CollectionUtils.isEmpty(judgeCase))
            question.setJudgeCase(GSON.toJson(judgeCase));
        else throw new BusinessException(ErrorCode.SYSTEM_ERROR, "测试用例不得为空!");
        // 判断时空间要求
        if (judgeConfig != null && judgeConfig.getMemoryLimit() != null
                && judgeConfig.getTimeLimit() != null && judgeConfig.getStackLimit() != null)
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        else throw new BusinessException(ErrorCode.SYSTEM_ERROR, "时空间要求不得为空!");
    }
}