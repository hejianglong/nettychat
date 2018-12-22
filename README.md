# nettychat

# init server project
    1. jdk >= 1.8
    2. 安装配置redis
    3. 配置server.properties
    4. mvn clean install -Dmaven.test.skip=true
       mvn assembly:assembly -Dmaven.test.skip=true
    5. nohup java -jar server-1.0-jar-with-dependencies.jar > /data/log/chat.log &

# test
    run ClientTest.java register

# server config
    ip: 114.115.248.101
    port: 8080
    
# 请求指令
    定义: common/Command.java
    
    // request
    Byte LOGIN = 1; // 登录请求
    Byte SEND_MESSAGE = 2; // 发送消息请求
    Byte REGISTER = 4; // 注册请求
    
    // response
    Byte LOGIN_RESPONSE = 3; // 登录成功响应
    Byte REGISTER_RESPONSE = 5; // 注册成功响应

# 协议规范
    首部4个字节 "魔数" 标识 协议认证
    int MAGIC_NUMBER = 0x12345678; // 魔数
    byte VERSION = 1; // 版本
    byte SERIALIZER = 1; // 现目前默认 1: com.alibaba.fastjson.JSON
    byte command = command; // 上述指令中选择
    byte length = length; // 数据长度

# 使用讲解
    请求对象体必须包含字段 command(请求指令), version(版本号)
    安卓 demo
    com.im.nettychat.ClientTest.java

# 接口文档见 doc.md