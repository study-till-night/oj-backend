package com.shuking.ojbackend.judge.codesandbox.impl;

import com.shuking.ojbackend.judge.codesandbox.CodeSandBox;
import com.shuking.ojbackend.judge.codesandbox.model.ExecuteCodeRequest;
import com.shuking.ojbackend.judge.codesandbox.model.ExecuteCodeResponse;
import com.shuking.ojbackend.judge.codesandbox.model.JudgeInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * 调用第三方代码沙箱
 */
@Component("thirdPartySandBox")
public class ThirdPartySandBoxImpl implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest request) {
        System.out.println("third");
        return new ExecuteCodeResponse(new ArrayList<String>(),"",0,new JudgeInfo());
    }
}
