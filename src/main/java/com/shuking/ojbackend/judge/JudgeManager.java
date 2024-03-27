package com.shuking.ojbackend.judge;


import com.shuking.ojbackend.judge.codesandbox.model.JudgeInfo;
import com.shuking.ojbackend.judge.strategy.JudgeContext;
import com.shuking.ojbackend.judge.strategy.JudgeStrategy;
import com.shuking.ojbackend.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）   根据上下文选择不同判题策略
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext 判题上下文
     * @return 判题结果
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        // 利用工厂反射得到实例
        JudgeStrategy judgeStrategy = JudgeServiceFactory.getJudgeStrategy(language);

        return judgeStrategy.doJudge(judgeContext);
    }
}