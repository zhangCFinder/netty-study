基于Netty的仿微信聊天小程序，用于自己对netty api的学习。

1. 使用Json作为序列化器，利用MessageToMessageCodec，合并编解码

2. 用户登陆后记录使用session类记录登陆状态，后续与服务端的交互直接在session类中验证用户状态

3. 服务端实现了空闲检测，客户端定时发送心跳包

4. 实现了一对一单聊

5. 后续准备加入群聊