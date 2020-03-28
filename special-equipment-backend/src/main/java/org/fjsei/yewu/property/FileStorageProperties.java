package org.fjsei.yewu.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

//配置文件中 的 自定义参数的输入。

@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
    private String uploadDir;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}


/* 对应的配置文件application.yml当中内容如下：　注意：　名字对应和空格长度。
file:
  upload-dir: /Users/callicoder/uploads
  特别注意的是　*.yml工程配置文件的格式严格，upload-dir:和file:的之间的　空格的　长度非常重要的！。
  upload-dir: 经过注入处理后，Bean初始化时刻，会经过setUploadDir(String uploadDir)直接赋值uploadDir的名字对应。
*/


