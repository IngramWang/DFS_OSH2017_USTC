# DFS_OSH2017_USTC

## INTRO

本项目是中国科学技术大学《操作系统原理与设计(H)》课程的大作业，旨在实现一个基于Erasure Code技术的,可以使用网页快速访问的小型分布式文件系统。

本项目申请大创计划已获通过，执行时间预期将持续到2018年初。

This project is a course work of Operating System(H) course in University of Science and Technology of China. In the project,
we will design a Distribute File System which can easily access with web page using principle of P2P and erasure code.

This project will be completed in July 2018.

### Features

项目预期具有以下特点：

通过安装客户端,任何主机均可以共享其部分存储空间供分布式文件系统使用,从而降低了分布式文件系统对专用数据服务器的依赖；

利用Erasure code（而非完全复制的副本）进行副本管理以维护文件的安全性与可用性；

整合客户端与网盘服务以提高可用性；

支持客户端、网页访问，同时提供API接口供软件开发者使用；

主要使用Java编程。

### FILE ORGANIZATION

src 目录下是本项目完整的源代码

report 目录下是本项目的各个报告

部署范例 目录下是本项目部署的一个例子，包括了客户端与服务器的可执行Jar包

分工 目录下是按分工组织的代码，即在每个人对应的目录下的代码均是由该人负责的

## CONTRIBUTOR

Kai Xing(Professor)

Jun Wang		[cnwj@mail.ustc.edu.cn]

Haojun Xia

Sijie Teng

Zhi Zheng

## TIMELINE

Mar 5, 2017	确定题目与分组成员

Mar 19, 2017	完成前期调研

Apr 4, 2017	完成可行性报告

Apr 9, 2017	重新修改了可行性报告

Apr 16, 2017	完成了中期报告

Jun 19, 2017 	完成了演示系统的编程

Jun 24, 2017 	完成了结题答辩

Jun 27, 2017 	完成了结题报告
