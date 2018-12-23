# nettychat
    基于netty4 IM

# 项目启动前配置
    1. jdk >= 1.8
    2. 安装配置redis, 无需设置账号密码
    3. 配置 server.properties
    4. mvn clean install -Dmaven.test.skip=true
       mvn assembly:assembly -Dmaven.test.skip=true // 将依赖一起打包
       // 部署环境需要指定 -Xms -Xmx 等配置信息, 默认10M或者较小会产生多次full gc以及eden会内存溢出
       // nohup java -Xms300M -Xmx300M  -jar server-1.0-jar-with-dependencies.jar > /data/log/chat.log &
    5. nohup java -jar server-1.0-jar-with-dependencies.jar > /data/log/chat.log &

# 客户端测试用列
    安卓还没有写好, 用端口访问测试
        见MainTest.java
    初始化配置账号2个
    账号, 密码
    666666,777777
    888888,999999

# 服务端配置
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
    com.im.nettychat.ClientTest.java // 有详细测试用列操作展示如下
    为了写测试用列方便request, response都复用了server的, 客户端可以自己定义取消不要的字段
    ---输入指令 -> a: 登录, b: 发送消息, c: 创建群组, d: 获取群组信息, e: 加入群组, f: 发送群组消息
    ---输入指令 -> q: 注册, g: 添加好友, h: 获取好友信息列表
    q
    请输入名称, 用户名, 密码用,隔开
    系统用户-1,666666,777777
    ---输入指令 -> a: 登录, b: 发送消息, c: 创建群组, d: 获取群组信息, e: 加入群组, f: 发送群组消息
    ---输入指令 -> q: 注册, g: 添加好友, h: 获取好友信息列表
    成功: [ RegisterResponse(userId=1, name=系统用户-1, icon=null, desc=null) ]

    ---输入指令 -> a: 登录, b: 发送消息, c: 创建群组, d: 获取群组信息, e: 加入群组, f: 发送群组消息
    ---输入指令 -> q: 注册, g: 添加好友, h: 获取好友信息列表
    b
    请输入对方的id和发送的消息用,隔开: 
    2,nihaoma
    ---输入指令 -> a: 登录, b: 发送消息, c: 创建群组, d: 获取群组信息, e: 加入群组, f: 发送群组消息
    ---输入指令 -> q: 注册, g: 添加好友, h: 获取好友信息列表
    成功: [ MessageResponse(fromUserId=2, message=wohenhao) ]

    ---输入指令 -> a: 登录, b: 发送消息, c: 创建群组, d: 获取群组信息, e: 加入群组, f: 发送群组消息
    ---输入指令 -> q: 注册, g: 添加好友, h: 获取好友信息列表
    e
    请输入要加入的群组的id: 
    1
    ---输入指令 -> a: 登录, b: 发送消息, c: 创建群组, d: 获取群组信息, e: 加入群组, f: 发送群组消息
    ---输入指令 -> q: 注册, g: 添加好友, h: 获取好友信息列表
    失败: [ 没有找到对应的群组]
    c
    请输入群组名称
    系统群组-1
    请输入拉取的用户id用,隔开
    2
    ---输入指令 -> a: 登录, b: 发送消息, c: 创建群组, d: 获取群组信息, e: 加入群组, f: 发送群组消息
    ---输入指令 -> q: 注册, g: 添加好友, h: 获取好友信息列表
    成功: [ CreateGroupResponse(groupName=null, groupId=1, userIds=null, icon=null) ]
    d
    请输入群组id
    1
    ---输入指令 -> a: 登录, b: 发送消息, c: 创建群组, d: 获取群组信息, e: 加入群组, f: 发送群组消息
    ---输入指令 -> q: 注册, g: 添加好友, h: 获取好友信息列表
    成功: [ GetUserGroupResponse(groupName=系统群组-1, groupId=1, userIds=[2, 1], icon=null, owner=1, desc=null) ]

    ---输入指令 -> a: 登录, b: 发送消息, c: 创建群组, d: 获取群组信息, e: 加入群组, f: 发送群组消息
    ---输入指令 -> q: 注册, g: 添加好友, h: 获取好友信息列表
    f
    请输入群组的id和发送消息用,隔开
    1,dajiahao
    ---输入指令 -> a: 登录, b: 发送消息, c: 创建群组, d: 获取群组信息, e: 加入群组, f: 发送群组消息
    ---输入指令 -> q: 注册, g: 添加好友, h: 获取好友信息列表
    成功: [ SendGroupMessageResponse(sendUserId=2, message=nihaoa) ]

    ---输入指令 -> a: 登录, b: 发送消息, c: 创建群组, d: 获取群组信息, e: 加入群组, f: 发送群组消息
    ---输入指令 -> q: 注册, g: 添加好友, h: 获取好友信息列表
    g
    请输入对方的id: 
    2
    ---输入指令 -> a: 登录, b: 发送消息, c: 创建群组, d: 获取群组信息, e: 加入群组, f: 发送群组消息
    ---输入指令 -> q: 注册, g: 添加好友, h: 获取好友信息列表
    成功: [ AddFriendResponse() ]

    ---输入指令 -> a: 登录, b: 发送消息, c: 创建群组, d: 获取群组信息, e: 加入群组, f: 发送群组消息
    ---输入指令 -> q: 注册, g: 添加好友, h: 获取好友信息列表
    h
    ---输入指令 -> a: 登录, b: 发送消息, c: 创建群组, d: 获取群组信息, e: 加入群组, f: 发送群组消息
    ---输入指令 -> q: 注册, g: 添加好友, h: 获取好友信息列表
# 接口文档见 doc.md
