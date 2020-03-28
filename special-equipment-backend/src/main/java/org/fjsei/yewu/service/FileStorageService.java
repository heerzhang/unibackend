package org.fjsei.yewu.service;

import org.fjsei.yewu.exception.FileStorageException;
import org.fjsei.yewu.exception.MyFileNotFoundException;
import org.fjsei.yewu.property.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        //这里只会执行一次的；
        try {
            Files.createDirectories(this.fileStorageLocation);
            //实际创建 目录=  D:/Users/callicoder/uploads
            //目录 /Users/callicoder/uploads 实际是当前的工程运行目录的当前磁盘根目录底下的；
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // Normalize file name
        //文件存储实际的路径：　../用户id/file.终端可附带路径的文件名　{前端和用户决定的子路径}　
        String fileName =file.getOriginalFilename();
        //自动转化转移../　./"表示的：　String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        int subdirCnt=fileName.lastIndexOf('/');
        if(subdirCnt>0) {
            if (fileName.lastIndexOf("./", subdirCnt) >= 0) {
                //防止非法的用法： 相对路径../../表示方法
                throw new FileStorageException("不能使用非法字符　" + fileName + "　.等", null);
            }
        }
        //字路径的：　用户id/终端可附带路径
        String subDir;
        String pureFileName;
        if(subdirCnt>=0) {
            subDir=fileName.substring(0,subdirCnt);
            pureFileName=fileName.substring(subdirCnt+1);
        }else{
            subDir="";       //不能用"/"的
            pureFileName=fileName;
        }

        Path myPath= Paths.get(subDir).normalize();
        Path  myRealPath=this.fileStorageLocation.resolve(myPath);
        try {
            //最终的正式字路径。
            Files.createDirectories(myRealPath);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
        //准备完了目录，再看最终文件
        try {
            // Check if the file's name contains invalid characters
            if(pureFileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + pureFileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            //客户端取消上传导致 情况1，java.io.IOException: 您的主机中的软件中止了一个已建立的连接。
            //情况2，更意外的，服务端正常生成文件存储对象后，客户端因为取消上传，用户已经不知道服务端做完的正事；
            //用户可能并不能真正取消刚刚的上传结果，文件对象实际已经保存了，客户还认为取消了就一定就没有文件的。

            Path targetLocation = myRealPath.resolve(pureFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return targetLocation.toString();
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + pureFileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }
}


//Java7取代原来的基于java.io.File的文件IO操作Java NIO之拥抱Path和Files;  https://blog.csdn.net/qq_34337272/article/details/80349017
//NIO2创建临时Files; isRegularFile :   https://www.cnblogs.com/niaomingjian/p/8551706.html
