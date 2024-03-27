package com.shuking.ojbackend.judge.codesandbox.impl;

import com.shuking.ojbackend.judge.codesandbox.CodeSandBox;
import com.shuking.ojbackend.judge.codesandbox.model.ExecuteCodeRequest;
import com.shuking.ojbackend.judge.codesandbox.model.ExecuteCodeResponse;
import com.shuking.ojbackend.judge.codesandbox.model.JudgeInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component("remoteSandBox")
public class RemoteSandBoxImpl implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest request) {
        System.out.println("remote");
        return new ExecuteCodeResponse(new ArrayList<>(),"",0,new JudgeInfo());
    }
}
