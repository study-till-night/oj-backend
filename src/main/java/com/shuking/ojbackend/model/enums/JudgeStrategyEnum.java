package com.shuking.ojbackend.model.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 判题策略枚举类
 */
public enum JudgeStrategyEnum {

    Default("default", "com.shuking.ojbackend.judge.strategy.DefaultJudgeStrategy"),
    JAVA("java", "com.shuking.ojbackend.judge.strategy.JavaLanguageJudgeStrategy");

    /**
     * 策略名称
     */
    private String strategyName;
    /**
     * 类名称
     */
    private String className;


    JudgeStrategyEnum(String strategyName, String className) {
        this.strategyName = strategyName;
        this.className = className;
    }

    public static JudgeStrategyEnum getByValue(String strategyName) {
        if (StringUtils.isBlank(strategyName))
            return Default;
        for (JudgeStrategyEnum strategyEnum : JudgeStrategyEnum.values()) {
            if (strategyEnum.getStrategyName().equals(strategyName))
                return strategyEnum;
        }
        return Default;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}