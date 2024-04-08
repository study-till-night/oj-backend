package com.shuking.ojbackend;

import com.shuking.ojbackend.judge.codesandbox.CodeSandBox;
import com.shuking.ojbackend.judge.codesandbox.CodeSandBoxProxy;
import com.shuking.ojbackend.judge.codesandbox.model.ExecuteCodeRequest;
import com.shuking.ojbackend.judge.codesandbox.model.ExecuteCodeResponse;
import com.shuking.ojbackend.model.enums.QuestionSubmitLanguageEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;

@SpringBootTest(classes = com.shuking.ojbackend.OjBackendApplication.class)
class OjBackendApplicationTests {

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

    @Test
    void testSandboxApi() {
        String code = """
                import java.util.Scanner;

                public class Main {

                    public static void main(String[] args) {
                        // int a = Integer.parseInt(args[0]), b = Integer.parseInt(args[1]);
                        Scanner scanner = new Scanner(System.in);
                        int a=scanner.nextInt();
                        int b=scanner.nextInt();
                        System.out.println("result:" + (a + b));
                    }
                }
                """;

        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputs(Arrays.asList("1 2","100 200"));
        executeCodeRequest.setLanguage("java");
        executeCodeRequest.setCode(code);

        ExecuteCodeResponse executeCodeResponse = codeSandBoxProxy.executeCode(executeCodeRequest);
    }
}