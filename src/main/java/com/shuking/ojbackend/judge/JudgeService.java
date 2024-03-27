package com.shuking.ojbackend.judge;

import com.shuking.ojbackend.judge.codesandbox.model.ExecuteCodeResponse;
import com.shuking.ojbackend.model.vo.QuestionSubmitVO;

public interface JudgeService {

    QuestionSubmitVO doJudge(Long submitId);
}
