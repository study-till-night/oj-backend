package com.shuking.ojbackend.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shuking.ojbackend.model.dto.question.QuestionAddRequest;
import com.shuking.ojbackend.model.dto.question.QuestionEditRequest;
import com.shuking.ojbackend.model.dto.question.QuestionQueryRequest;
import com.shuking.ojbackend.model.entity.Question;
import com.shuking.ojbackend.model.vo.QuestionVO;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author HP
* @description 针对表【question(题目)】的数据库操作Service
* @createDate 2024-03-25 14:51:38
*/
public interface QuestionService extends IService<Question> {

    /**
     * 判断问题合法性
     * @param question 问题
     * @param add      是否为新增操作
     */
    void validQuestion(Question question, boolean add);

    /**
     * 根据请求对象获取QueryWrapper对象
     *
     * @return wrapper
     */
    Wrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    /**
     * 获取问题脱敏对象
     * @param question  问题
     * @param request   http
     * @return  vo
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 添加问题
     * @param questionAddRequest    dto
     * @param request   http
     * @return
     */
    Long addQuestion(QuestionAddRequest questionAddRequest, HttpServletRequest request);

    /**
     * 编辑问题
     * @param questionEditRequest   dto
     * @param request http
     * @return
     */
    boolean updateQuestion(QuestionEditRequest questionEditRequest,HttpServletRequest request);
}
