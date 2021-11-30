# shiro550反序列化漏洞利用工具

<h1 align="center" >ShiroAttack2</h1>
<h3 align="center" >一款针对Shiro550漏洞进行快速漏洞利用</h3>
 <p align="center">
    <a href="https://github.com/SummerSec/ShiroAttack2"></a>
    <a href="https://github.com/SummerSec/ShiroAttack2"><img alt="SpringBootExploit" src="https://img.shields.io/badge/Spring-Boot-Exploit-green"></a>
    <a href="https://github.com/SummerSec/ShiroAttack2"><img alt="Forks" src="https://img.shields.io/github/forks/SummerSec/ShiroAttack2"></a>
     <a href="https://github.com/SummerSec/ShiroAttack2"><img alt="Release" src="https://img.shields.io/github/release/SummerSec/ShiroAttack2.svg"></a>
  <a href="https://github.com/SummerSec/ShiroAttack2"><img alt="Stars" src="https://img.shields.io/github/stars/SummerSec/ShiroAttack2.svg?style=social&label=Stars"></a>
     <a href="https://github.com/SummerSec"><img alt="Follower" src="https://img.shields.io/github/followers/SummerSec.svg?style=social&label=Follow"></a>
	<a href="https://twitter.com/SecSummers"><img alt="SecSummers" src="https://img.shields.io/twitter/follow/SecSummers.svg"></a>
	<a xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="https://visitor-badge.laobi.icu"><rect fill="rgba(0,0,0,0)" height="20" width="49.6"/></a>
	<a xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="https://visitor-badge.laobi.icu"><rect fill="rgba(0,0,0,0)" height="20" width="17.0" x="49.6"/></a>
	</p>



## 前言

关于该工具更新内容介绍后续会更新到博客下面**https://summersec.github.io/BlogParpers**

## 工具特点

* javafx
* 处理没有第三方依赖的情况
* 支持多版本CommonsBeanutils的gadget
* 支持内存马
* 采用直接回显执行命令
* 添加了更多的CommonsBeanutils版本gadget
* 支持修改rememberMe关键词
* 支持直接爆破利用gadget和key
* 支持代理
* 添加修改shirokey功能（使用内存马的方式）
* 支持内存马小马



## 使用方法

直接使用shiro_attack-{version}-SNAPSHOT-all.jar第三版

![image-20211130114603322](https://gitee.com/samny/images/raw/master/summersec//3u46er3ec/3u46er3ec.png)

在jar的当前目录下创建一个data文件夹，里面创建一个shiro_keys.txt文件，文件内容是shiro_key。lib目前是CommonsBeanutils依赖的版本。

![image-20211130113559530](https://gitee.com/samny/images/raw/master/summersec//59u35er59ec/59u35er59ec.png)



---

## :b:免责声明

该工具仅用于安全自查检测

由于传播、利用此工具所提供的信息而造成的任何直接或者间接的后果及损失，均由使用者本人负责，作者不为此承担任何责任。

本人拥有对此工具的修改和解释权。未经网络安全部门及相关部门允许，不得善自使用本工具进行任何攻击活动，不得以任何方式将其用于商业目的。



----

![as](https://starchart.cc/SummerSec/ShiroAttack2.svg)

