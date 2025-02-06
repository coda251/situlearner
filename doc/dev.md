## 软件开发

### 架构

参照[now in android](https://github.com/android/nowinandroid)的mvi架构。


## 业务逻辑

### 语言识别

app使用[lingua](https://github.com/pemistahl/lingua)，在打包过程中删除了未使用的其它语言模型（只保留了汉语、英语和日语三种语言）。

### 字幕-分词：分词与词形还原

app中使用kuromoji作为日语分词工具，opennlp作为英语分词工具。
其中opennlp中修改了opennlp.tools.util.XmlUtil中部分代码并重新打包，使其兼容android平台。

#### 单语言

kuromoji分词。

#### 多语言

- corenlp
  - 缺点：
    - 包体积过大（.jar约500 MB）
    - 支持语言有限

- opennlp
  - 缺点：
    - 在opennlp.tools.util.XmlUtil中使用了android不兼容的xml解析特性XMLConstants.FEATURE_SECURE_PROCESSING，需要[修改源码](https://stackoverflow.com/questions/47243013)
    - 支持语言有限

- spacy + chaquopy
  - 优点：支持多种语言，统一的分词和词性还原的接口
  - 缺点：
    - 包体积大（.apk约100 MB，安装后约200MB）
    - [chaquopy目前只支持到2.2.3版本的spacy](https://github.com/chaquo/chaquopy/issues/639)，该版本的spacy不包含日语的模型（2.3.0以后才支持日语）。
    - 解析速度较慢，解析单个字幕文件需耗费5到10秒，耗时大约是opennlp的5倍。

### 词典

使用jsoup解析youdao词典返回的html文件。