# 证书生成，加解密，签名 Demo

# 数字证书产品字段说明

**公用名称** (Common Name, 简称：CN) 字段，对于 SSL 证书，一般为网站域名；而对于代码签名证书则为申请单位名称；而对于客户端证书则为证书申请者的姓名；

**单位名称** (Organization Name) ：简称：O 字段，对于 SSL 证书，一般为网站域名；而对于代码签名证书则为申请单位名称；而对于客户端单位证书则为证书申请者所在单位名称；

**证书申请单位所在地：**

**所在城市** (Locality) 简称：L 字段

**所在省份** (State/Provice) 简称：S 字段

**所在国家** (Country) 简称：C 字段，只能是国家字母缩写，如中国：CN

其他一些字段：

**电子邮件** (Email) 简称：E 字段

**多个姓名字段** 简称：G 字段

介绍：Description 字段

**电话号码：** Phone 字段，格式要求 + 国家区号 城市区号 电话号码，如： +86 732 88888888

**地址：** STREET 字段

**邮政编码：** PostalCode 字段

显示其他内容 简称：OU 字段

**简单说明：**
> CN=(名字与姓氏), OU=(组织单位名称), O=(组织名称), L=(城市或区域名称),ST=(州或省份名称), C=(单位的两字母国家代码)

# 关于证书的几个概念
**公共钥匙：** 是一个详细的实体的数字关联,并有意让所有想同这个实体发生信任关系的其他实体知道.公共钥匙用来检验签名;

**数字签名 ：** 是实体信息用实体的私有钥匙签名（加密）后的数据.这条数据可以用这个实体的公共钥匙来检验签名（解密）出实体信息以鉴别实体的身份;用户用私钥对某些信息加以处理，由于密钥仅为本人所有，这样就产生了别人无法生成的文件，也就形成了数字签名。

**签名：** 用实体私有钥匙加密某些消息，从而得到加密数据;

**私有钥匙：** 是一些数字,私有和公共钥匙存在所有用公共钥匙加密的系统的钥匙对中.公共钥匙用来加密数据，私有钥匙用来计算签名.公钥加密的消息只能用私钥解密，私钥签名的消息只能用公钥检验签名。

**实体：** 一个实体可以是一个人,一个组织,一个程序,一台计算机,一个商业,一个银行,或其他你想信任的东西.

**公钥和私钥的关系 ：**

如果我用私钥加密一段数据（当然只有我可以用私钥加密，因为只有我知道2是我的私钥），结果所有的人都看到我的内容了，因为他们都知 
道我的公钥是1，那么这种加密有什么用处呢？但是我的好朋友x说有人冒充我给他发信。怎么办呢？我把我要发的信，内容是c，用我的私钥2，加密，加密后的内容是d，发给x，再告诉他解密看是不是c。他用我的公钥1解密，发现果然是c。这个时候，他会想到，能够用我的公钥解密的数据，必然是用我的私钥加的密。只有我知道我得私钥，因此他就可以确认确实是我发的东西。

这样我们就能确认发送方身份了。这个过程叫做数字签名。当然具体的过程要稍微复杂一些。用私钥来加密数据，用途就是 **数字签名** 。

**总结：** 公钥和私钥是成对的，它们互相解密。

公钥加密，私钥解密。

私钥数字签名，公钥验证。


# JKS 说明
JKS文件是一个java中的密钥管理库。
JKS文件就好像一个仓库，里面可以放很多的东西，这里只存放一类东西就是密钥，仓库当然会有一把锁，防范别人随便乱拿，这个就是JKS文件的密码。里面存放的密钥也各有不同，每个密钥都有一个名字（在下面叫别名），一类就密钥对，一类叫公钥，一类叫私钥，密钥对就是包含公钥和私钥的。这里的公钥只要你能进入仓库你就可以随便查看拿走，私钥则是有密码的，只允许有权限的人查看拿走。
Keytool 是一个JAVA环境下的安全钥匙与证书的管理工具，Keytool将密钥（key）和证书（certificates）存在一个称为keystore 的文件(受密码保护)中。
在keystore里，包含两种数据： 
**密钥实体**（Key entity）——密钥（secretkey）又或者是私钥和配对公钥（采用非对称加密）
**可信任的证书实体**（trustedcertificate entries）——只包含公钥


# 参考链接
[关于证书认证技术的相关概念理解](https://www.cnblogs.com/sdjnzqr/p/4277092.html)

[Java Keytool（KeyStore） 以及公钥私钥的一些概念](http://ju.outofmemory.cn/entry/224421)
