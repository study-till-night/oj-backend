package com.shuking.ojbackend;

import com.shuking.ojbackend.judge.codesandbox.CodeSandBox;
import com.shuking.ojbackend.judge.codesandbox.CodeSandBoxProxy;
import com.shuking.ojbackend.judge.codesandbox.model.ExecuteCodeRequest;
import com.shuking.ojbackend.model.enums.QuestionSubmitLanguageEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest(classes = com.shuking.ojbackend.OjBackendApplication.class)
class OjBackendApplicationTests {

    @Value("${codesandbox.type:exampleSandBox}")
    private final String boxType="";

    @SuppressWarnings({"all"})
    @Resource(name = "${codesandbox.type:exampleSandBox}")
    private CodeSandBox codeSandBox;

    @Resource
    private CodeSandBoxProxy codeSandBoxProxy;

    @Test
    void testCodeBox() {
        CodeSandBox codeSandBox1 = (request) -> {
            System.out.println("test");
            return null;
        };

        codeSandBox1.executeCode(ExecuteCodeRequest.builder().code("int main")
                .inputs(new ArrayList<>())
                .language(QuestionSubmitLanguageEnum.CPLUSPLUS.getText()).build()
        );
    }

}
