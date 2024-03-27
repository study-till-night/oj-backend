package com.shuking.ojbackend.judge;

import com.shuking.ojbackend.judge.strategy.DefaultJudgeStrategy;
import com.shuking.ojbackend.judge.strategy.JudgeStrategy;
import com.shuking.ojbackend.model.enums.JudgeStrategyEnum;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * 使用工厂模式获取具体策略实现的实例
 */
public class JudgeServiceFactory {

    public static JudgeStrategy getJudgeStrategy(String type) {
        if (StringUtils.isBlank(type))
            return new DefaultJudgeStrategy();

        // 通过反射得到策略实现类的实例
        try {
            return (JudgeStrategy) Class.forName(JudgeStrategyEnum.getByValue(type).getClassName()).getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
