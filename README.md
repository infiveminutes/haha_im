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

