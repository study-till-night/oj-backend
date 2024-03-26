package com.shuking.ojbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shuking.ojbackend.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.shuking.ojbackend.model.entity.QuestionSubmit;
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
}
