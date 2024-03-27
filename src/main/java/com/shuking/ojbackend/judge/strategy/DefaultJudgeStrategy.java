package com.shuking.ojbackend.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.shuking.ojbackend.judge.codesandbox.model.JudgeInfo;
import com.shuking.ojbackend.model.dto.question.JudgeCase;
import com.shuking.ojbackend.model.dto.question.JudgeConfig;
import com.shuking.ojbackend.model.entity.Question;
import com.shuking.ojbackend.model.enums.JudgeInfoMessageEnum;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultJudgeStrategy implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext context) {
        JudgeInfo judgeInfo = context.getJudgeInfo();
        List<String> inputList = context.getInputList();
        List<String> outputList = context.getOutputList();
        List<JudgeCase> caseList = context.getJudgeCaseList();
        Question question = context.getQuestion();

        JudgeInfoMessageEnum resType;
        // 输出长度不一致则判为WA
        if (outputList.size() != inputList.size()) {
            resType = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfo.setMessage(resType.getText());
            return judgeInfo;
        }
        // 依次判断执行输出与测试用例是否一致
        for (int i = 0; i < outputList.size(); i++) {
            if (!outputList.get(i).equals(caseList.get(i).getOutput())) {
                resType = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfo.setMessage(resType.getText());
                return judgeInfo;
            }
        }

        // 程序执行的相关信息
        String execMessage = judgeInfo.getMessage();
        Long memoryUsed = judgeInfo.getMemory();
        Long timeUsed = judgeInfo.getTime();

        // 得到题目规定的要求
        JudgeConfig judgeConfig = JSONUtil.toBean(question.getJudgeConfig(), JudgeConfig.class);
        Long timeLimit = judgeConfig.getTimeLimit();
        Long memoryLimit = judgeConfig.getMemoryLimit();

        // 判断时空间是否满足
        if (timeUsed > timeLimit){
            resType = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfo.setMessage(resType.getText());
            return judgeInfo;
        }
        if (memoryUsed > memoryLimit){
            resType = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfo.setMessage(resType.getText());
            return judgeInfo;
        }
        resType = JudgeInfoMessageEnum.ACCEPTED;
        judgeInfo.setMessage(resType.getText());
        return judgeInfo;
    }
}
