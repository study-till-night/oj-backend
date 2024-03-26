package com.shuking.ojbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shuking.ojbackend.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.shuking.ojbackend.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.shuking.ojbackend.model.entity.QuestionSubmit;
import com.shuking.ojbackend.model.entity.User;
import com.shuking.ojbackend.model.vo.QuestionSubmitVO;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author HP
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2024-03-25 14:51:38
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 提交做题记录
     */
    Long doSubmit(QuestionSubmitAddRequest submitAddRequest, HttpServletRequest request);

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest    查询对象
     * @return  wrapper
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取题目封装
     *
     * @param questionSubmit    提交记录对象
     * @param loginUser 用户对象
     * @return  脱敏对象
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitPage    提交记录对象
     * @param loginUser 用户对象
     * @return  脱敏对象
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);
}
