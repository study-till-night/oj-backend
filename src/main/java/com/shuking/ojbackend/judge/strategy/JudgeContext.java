package com.shuking.ojbackend.judge.strategy;

import com.shuking.ojbackend.judge.codesandbox.model.JudgeInfo;
import com.shuking.ojbackend.model.dto.question.JudgeCase;
import com.shuking.ojbackend.model.entity.Question;
import com.shuking.ojbackend.model.entity.QuestionSubmit;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
@Builder
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;

}