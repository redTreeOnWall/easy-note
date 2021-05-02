# easy-note

## 简介
一个方便,安全,支持多笔记形式的云笔记工具.
### 方便
支持web端, app端, 其中,app端可以离线保存笔记,以便在无网络的条件下记录.
### 安全
客户端加密和解密, 即, 需要在客户端输入密码. 才能解码. 保存在服务端的数据为加密数据, 即使数据泄露, 也很难还原原始信息. 这也意味着,没有密码找回功能,密码丢失后,资料将丢失.
### 多形式
支持文本, 录音, 图片等形式的笔记的记录.

## server 

### 前置条件
* 安装jdk (jdk版本 >= 11)
* 安装maven

### 运行:
``` shell
mvn -DskipTest compile exec:java -Dexec.args="address password"
```

### 打包
``` shell
mvn package
```
