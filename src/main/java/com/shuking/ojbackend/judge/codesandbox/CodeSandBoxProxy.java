package com.shuking.ojbackend.judge.codesandbox;

import com.shuking.ojbackend.judge.codesandbox.model.ExecuteCodeRequest;
import com.shuking.ojbackend.judge.codesandbox.model.ExecuteCodeResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 代理类 实现对基础代码沙箱实现类的增强
 * 代理模式的简单应用
 */
@Slf4j
@Component
public class CodeSandBoxProxy implements CodeSandBox {
    /**
     * 从配置文件中读取参数实现Bean的动态注入
     */
    @SuppressWarnings({"all"})
    @Resource(name = "${codesandbox.type:exampleSandBox}")
    private CodeSandBox codeSandBox;

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest request) {
        log.info("代码沙箱请求信息--{}", request.toString());
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(request);
        log.info("代码沙箱响应信息--{}", executeCodeResponse.toString());
        return executeCodeResponse;
    }
}
