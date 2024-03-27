package com.shuking.ojbackend.judge.codesandbox;

import com.shuking.ojbackend.judge.codesandbox.model.ExecuteCodeRequest;
import com.shuking.ojbackend.judge.codesandbox.model.ExecuteCodeResponse;

@FunctionalInterface
public interface CodeSandBox {
    // 使用接口定义执行方法提高通用性
    ExecuteCodeResponse executeCode(ExecuteCodeRequest request);
}
