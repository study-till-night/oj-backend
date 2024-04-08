package com.shuking.ojbackend.judge.codesandbox.impl;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.shuking.ojbackend.common.ErrorCode;
import com.shuking.ojbackend.exception.ThrowUtils;
import com.shuking.ojbackend.judge.codesandbox.CodeSandBox;
import com.shuking.ojbackend.judge.codesandbox.model.ExecuteCodeRequest;
import com.shuking.ojbackend.judge.codesandbox.model.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component("remoteSandBox")
public class RemoteSandBoxImpl implements CodeSandBox {

    private final String POST_URL = "http://localhost:8081/sandbox/execute";

    //  鉴权请求头名称
    private static final String AUTH_HEADER = "sandbox-header";
    //  密钥字符串
    private static final String AUTH_KEY = "shu-king";

    /**
     * @param request 判题服务传输的代码对象
     * @return 执行信息
     */

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        // 调用远程沙箱的post请求
        String response = HttpUtil.createPost(POST_URL)
                .header(AUTH_HEADER,AUTH_KEY).body(JSONUtil.toJsonStr(request)).execute().body();

        ThrowUtils.throwIf(StringUtils.isBlank(response), ErrorCode.SYSTEM_ERROR, "代码沙箱服务调用失败");
        ThrowUtils.throwIf(StringUtils.contains(response,"status: 401"),ErrorCode.NO_AUTH_ERROR,"无权访问代码沙箱");

        return JSONUtil.toBean(response, ExecuteCodeResponse.class);
    }
}