# Struts2とAWS S3でファイルダウンロードを行う

このリポジトリはAWS S3からファイルダウンロードを行うプログラムのサンプルです。  

## 動作環境

下記の環境で動作確認ができています。

* Java8
  * Struts2 (2.5.22)
* Tomcat8.5

## 内容

### 概要

AWS S3からファイルを下記2通りでダウンロードする

* メインのjsp ⇒ `WebContent/index.jsp`
* S3からオブジェクトを直接指定でダウンロード ⇒ `src/sample/DownloadAction.java`
* S3のオブジェクトの署名付きURLを生成してダウンロード  ⇒ `src/sample/DownloadFromPresignedUrlAction.java`

### 全体
```
FileDownloadSample
|--README.md
|--resources
|  |--log4j2.xml
|  |--struts.properties
|  |--struts.xml
|--src
|  |--sample
|  |  |--DownloadAction.java
|  |  |--DownloadFromPresignedUrlAction.java
|--WebContent
|  |--error.jsp
|  |--index.jsp
|  |--META-INF
|  |  |--MANIFEST.MF
|  |--WEB-INF
|  |  |--lib
|  |  |  |--aws-java-sdk-1.11.842.jar
|  |  |  |--commons-fileupload-1.4.jar
|  |  |  |--commons-io-2.6.jar
|  |  |  |--commons-lang-2.4.jar
|  |  |  |--commons-lang3-3.8.1.jar
|  |  |  |--commons-logging-1.2.jar
|  |  |  |--freemarker-2.3.28.jar
|  |  |  |--httpclient-4.5.9.jar
|  |  |  |--httpcore-4.4.11.jar
|  |  |  |--jackson-annotations-2.6.0.jar
|  |  |  |--jackson-core-2.6.7.jar
|  |  |  |--jackson-databind-2.6.7.3.jar
|  |  |  |--javassist-3.20.0-GA.jar
|  |  |  |--joda-time-2.8.1.jar
|  |  |  |--log4j-api-2.12.1.jar
|  |  |  |--log4j-core-2.12.1.jar
|  |  |  |--lombok.jar
|  |  |  |--ognl-3.1.26.jar
|  |  |  |--struts2-core-2.5.22.jar
|  |  |  |--xmlpull-1.1.3.1.jar
|  |  |  |--xpp3_min-1.1.4c.jar
|  |  |  |--xstream-1.4.11.1.jar
|  |  |--web.xml
```

### 備考

* AWSの認証情報が書かれたpropertiesファイルはgit管理していません。

    `resources`配下に`awsS3.properties`というファイルを下記のように作成してください。
    ```
    accessKeyId=アクセスキーID
    secretAccessKey=シークレットアクセスキー
    backetName=バケット名
    ```

* 取得するファイル名などはサンプルプログラムのため、Actionクラス内にべた書きです。
* jarについて
    * AWS関連
         * aws-java-sdk-1.11.842.jar
         * 下記はS3操作用に必要(aws sdkをダウンロード時に`third-party`として付属しているjar)
            * jackson-databind-2.6.7.3.jar
            * jackson-annotations-2.6.0.jar
            * jackson-core-2.6.7.jar
            * httpcore-4.4.11.jar
            * httpclient-4.5.9.jar
            * joda-time-2.8.1.jar]
    * Struts2関連
         * 上記以外 