# 工作内容介绍
last update 2017/5/12

本文介绍到目前为止客户端方面的代码的主要工作。

## 一、进展

目前已经完成了控制连接的代码并经过了初步的测试.

与服务器的长期连接的可靠性仍需测试.

数据连接的代码已完成但未测试.

## 二、代码结构

### client包中是客户端的主体部分,目前包括

Client.java 客户端的主类,读取配置文件,启动客户端服务

SynItem.java 同步类,用于管理客户端各线程间的同步,使主线程可以检测各线程的工作状态.

### com.backblaze.erasure包是Erasure Code的开源实现,用于文件的分块

### connect包是客户端的链接部分,其中

ServerConnector.java 服务器控制连接类,继承了Thread接口,可在新线程中执行.

FragmentManager.java 碎片管理与服务器数据连接类,继承了Thread接口,但目前不支持在新线程中执行(需使用submit方法在调用者的线程中执行).

FileTransporter.java 是滕思洁编写的文件传输类.

