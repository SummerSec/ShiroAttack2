# shiro550反序列化漏洞利用工具

## 前言



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



----

![as](https://starchart.cc/SummerSec/shiro_attack2.svg)

