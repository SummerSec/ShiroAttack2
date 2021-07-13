# shiro550反序列化漏洞利用工具

## 工具特点

* javafx
* 处理没有第三方依赖的情况
* CommonsBeanutils1.9.2版本暂时不支持
* 支持内存马
* 采用直接回显执行命令
* 添加了更多的CommonsBeanutils版本gadget
* 支持修改rememberMe关键词
* 支持直接爆破利用gadget和key
* 支持代理

## TODO

* 支持CommonsBeanutils1.9.2版本



## 使用方法

直接使用shiro_attack-3.0-SNAPSHOT-all.jar第三版

![image-20210713150738617](https://gitee.com/samny/images/raw/master/summersec//38u07er38ec/38u07er38ec.png)

在jar的当前目录下创建一个data文件夹，里面创建一个shiro_keys.txt文件，文件内容是shiro_key

![image-20210713151340541](https://gitee.com/samny/images/raw/master/summersec//40u13er40ec/40u13er40ec.png)

