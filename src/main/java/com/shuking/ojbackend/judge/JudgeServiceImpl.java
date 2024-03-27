package com.shuking.ojbackend.judge;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.shuking.ojbackend.common.ErrorCode;
import com.shuking.ojbackend.exception.ThrowUtils;
import com.shuking.ojbackend.judge.codesandbox.CodeSandBoxProxy;
import com.shuking.ojbackend.judge.codesandbox.model.ExecuteCodeRequest;
import com.shuking.ojbackend.judge.codesandbox.model.ExecuteCodeResponse;
import com.shuking.ojbackend.judge.codesandbox.model.JudgeInfo;
import com.shuking.ojbackend.judge.strategy.JudgeContext;
import com.shuking.ojbackend.model.dto.question.JudgeCase;
import com.shuking.ojbackend.model.entity.Question;
import com.shuking.ojbackend.model.entity.QuestionSubmit;
import com.shuking.ojbackend.model.enums.QuestionSubmitStatusEnum;
import com.shuking.ojbackend.model.vo.QuestionSubmitVO;
import com.shuking.ojbackend.service.QuestionService;
import com.shuking.ojbackend.service.QuestionSubmitService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private CodeSandBoxProxy sandBoxProxy;

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService submitService;

    /**
     * 执行判题逻辑
     *
     * @param submitId 执行判题的提交记录id
     * @return 提交记录VO
     */
    @Override
    @Transactional
    public QuestionSubmitVO doJudge(Long submitId) {
        QuestionSubmit submit = submitService.getById(submitId);

        String language = submit.getLanguage();
        String code = submit.getCode();
        Integer submitStatus = submit.getStatus();
        Long questionId = submit.getQuestionId();

        ThrowUtils.throwIf(questionId == null, ErrorCode.NOT_FOUND_ERROR, "提交记录不存在");
        ThrowUtils.throwIf(!submitStatus.equals(QuestionSubmitStatusEnum.WAITING.getValue()), ErrorCode.OPERATION_ERROR, "不得重复提交");

        // 将记录状态更改为判题中
        boolean updateStatusRes = submitService.update(new LambdaUpdateWrapper<QuestionSubmit>().eq(QuestionSubmit::getId, submitId)
                .set(QuestionSubmit::getStatus, QuestionSubmitStatusEnum.RUNNING.getValue()));
        ThrowUtils.throwIf(!updateStatusRes, ErrorCode.SYSTEM_ERROR, "更新提交记录状态失败");

        // 得到题目测试用例list
        Question question = questionService.getById(questionId);
        List<JudgeCase> caseList = JSONUtil.toList(question.getJudgeCase(), JudgeCase.class);

        // 执行沙箱得到结果
        List<String> inputList = caseList.stream().map(JudgeCase::getInput).toList();
        ExecuteCodeResponse executeCodeResponse = sandBoxProxy.executeCode(ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputs(inputList).build());

        List<String> outputList = executeCodeResponse.getOutputList();
        JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo();

        // 封装判题逻辑上下文
        JudgeContext judgeContext = JudgeContext.builder()
                .judgeInfo(judgeInfo).judgeCaseList(caseList)
                .inputList(inputList).outputList(outputList)
                .questionSubmit(submit).question(question).build();
        // 得到执行结果 是AC还是WA还是TLE...
        JudgeManager judgeManager = new JudgeManager();
        JudgeInfo judgeRes = judgeManager.doJudge(judgeContext);

        // 将记录状态更改为成功   并更新答题结果
        submit.setJudgeInfo(JSONUtil.toJsonStr(judgeRes));
        boolean updateStatusRes2 = submitService.update(new LambdaUpdateWrapper<QuestionSubmit>().eq(QuestionSubmit::getId, submitId)
                .set(QuestionSubmit::getStatus, QuestionSubmitStatusEnum.SUCCEED.getValue())
                .set(QuestionSubmit::getJudgeInfo, submit.getJudgeInfo()));
        ThrowUtils.throwIf(!updateStatusRes2, ErrorCode.SYSTEM_ERROR, "更新提交记录状态失败");

        return QuestionSubmitVO.objToVo(submit);
    }
}