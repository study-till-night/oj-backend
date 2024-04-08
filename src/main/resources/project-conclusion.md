## 代码提交执行流程

1、请求QuestionController的doSubmit接口

2、执行QuestionSubmitServiceImpl的doSubmit方法，保存记录并异步调用judgeService的doJudge方法，传入提交ID

---

**代理模式**

沙箱顶层接口CodeSandBox   包含唯一方法executeCode 接收ExecuteCodeRequest对象

```java
public class ExecuteCodeRequest {

    private List<String> inputs;

    private String language;

    private String code;
}
```

代理类CodeSandBoxProxy实现CodeSandBox 并在实现类的基础上在方法执行前后添加增强内容，比如日志打印

```java
public ExecuteCodeResponse executeCode(ExecuteCodeRequest request) {
        log.info("代码沙箱请求信息--{}", request.toString());
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(request);
        log.info("代码沙箱响应信息--{}", executeCodeResponse.toString());
        return executeCodeResponse;
    }
```

---

3、在doJudge方法中执行sandBoxProxy的executeCode方法

4、根据application.yml文件中配置的代码沙箱模式，调用相应的代码沙箱服务

```yml
#自定义代码沙箱类型
codesandbox:
  #  共三种 ExampleSandBox  remoteSandBox ThirdPartySandBoxImpl
  type: remoteSandBox 
```

5、以远程型为例，通过Rpc远程调用请求代码沙箱的开放API得到ExecuteCodeResponse

```java
public class ExecuteCodeResponse {

    private List<String> outputList;

    /**
     * 接口信息
     */
    private String message;

    /**
     * 执行状态
     */
    private Integer status;

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;
}
```


```java
public ExecuteCodeResponse executeCode(ExecuteCodeRequest request) {
    ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);

    // 调用远程沙箱的post请求
    String response = HttpUtil.createPost(POST_URL)
            .header(AUTH_HEADER,AUTH_KEY).body(JSONUtil.toJsonStr(request)).execute().body();
    ThrowUtils.throwIf(StringUtils.isBlank(response), ErrorCode.SYSTEM_ERROR, "代码沙箱服务调用失败");

    return JSONUtil.toBean(response, ExecuteCodeResponse.class);
}
```

---

**动态工厂模式**

通过java反射机制根据传入的字符串查询枚举类，动态地生成实现类

```java
public class JudgeServiceFactory {

    public static JudgeStrategy getJudgeStrategy(String type) {
        if (StringUtils.isBlank(type))
            return new DefaultJudgeStrategy();

        // 通过反射得到策略实现类的实例
        try {
            return (JudgeStrategy) Class.forName(JudgeStrategyEnum.getByValue(type).getClassName()).getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
```
---
**策略模式**

根据不同情景 判题服务可有多种策略   设置一个JudgeManager 根据传入的context上下文
```java
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;
}
```
调用动态工厂生成相应策略实现类

```java
JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        // 利用工厂反射得到实例
        JudgeStrategy judgeStrategy = JudgeServiceFactory.getJudgeStrategy(language);

        return judgeStrategy.doJudge(judgeContext);
}
```
---

6、根据得到的ExecuteCodeResponse，执行JudgeManager的doJudge方法，判断执行结果是否与预期一致 并判断时空间是否满足要求 根据此置提交结果为AC TLE或MLE等

7、前端查询新结果进行展示

## 代码沙箱实现细节