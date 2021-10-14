# haha_im

- 最近在看netty想找个练手的项目
- 看到了这个博客 https://juejin.cn/post/6844903935040307214
- 自己实现一下


### todo

- [ ] 加一个网关模块, 用于根据用户id做流量分发
- [ ] 加一个注册模块, 用户connector及transfer的注册和发现
- [ ] global_id多机部署
- [ ] user_status做成服务
- [ ] 用户下线没有在connector服务中清理掉连接及userStatus中修改用户上线状态
- [ ] MemoryConnectManager不是线程安全的，因为内部有两个concurrentHashMap
- [ ] 离线消息放入队列并持久化的可靠性优化


### 问题





### 细节

1. haha_im使用客户端到客户端的ack机制保证消息不丢，但是当消息接收者不在线时，
chat消息传入队列中以后，transfer需要给消息发送人一个ack响应(模拟消息已经发送成功)，
如果这个ack响应在传输中丢失，消费者会超时重传同一个消息，导致同一个chat消息重复入队，
所以需要在队列消费者处做消息过滤


