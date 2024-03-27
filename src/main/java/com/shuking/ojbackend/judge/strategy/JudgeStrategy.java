package com.shuking.ojbackend.judge.strategy;

import com.shuking.ojbackend.judge.codesandbox.model.JudgeInfo;

public interface JudgeStrategy {
    /**
     * 执行判题策略接口
     * @param context   判断参数上下文
     * @return  判题结果
     */
    JudgeInfo doJudge(JudgeContext context);
}
