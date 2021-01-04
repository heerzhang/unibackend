package org.fjsei.yewu.controller;

import org.fjsei.yewu.exception.FileStorageException;
import org.fjsei.yewu.payload.UploadFileResponse;
import org.fjsei.yewu.service.FileService;
import org.fjsei.yewu.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FileController {
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    @Autowired
    private FileService fileService;
    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        //客户端给出的文件名。
        String fileName =file.getOriginalFilename();
        String fileDownloadUri = fileStorageService.storeFile(file);
        /* 下载的路径：
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/file/")
                .path(fileName)
                .toUriString();
        */

        //生成文件对象，方便管理上传的文件。
        String  newFileID=fileService.addFile(fileDownloadUri);
        if(newFileID==null) {
            throw new FileStorageException("上传addFile失败:" + fileName, null);
        }
        else
            return new UploadFileResponse(fileName, newFileID,
                    file.getContentType(), file.getSize());
    }

    //下面这个实际上前端不愿意使用，对于多文件的情况，前端会分解成多个文件每个做一次上面的uploadFile上传，分开多个http来做。
    //实际不需要这个的；
    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }

    //SpEL来表示 :.+ 带上.的
    //嵌套的目录文件呢

    ///  @GetMapping("/downloadFile/{fileName:.+}")
    /*
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
  */
    /** 带有 URI 模板

    @GetMapping(value = "/{id}/file-treeTest") // 交易信息编码
    public String getFileTree(@RequestParam(name = "id") String finTradeCode)
    {
        System.out.println("--> " + finTradeCode);
        return "/examples/targets/test2";
    }

    @ResponseBody
    @RequestMapping(method=RequestMethod.GET ,value="/abcTest/**")
    public String dubboMock(HttpServletResponse response, HttpServletRequest request){
        String url = request.getRequestURI();
        System.out.println("--> " + url);
        return "/examples/targets/test2";
    }
    */
 /*
    @RequestMapping(method=RequestMethod.GET ,value="/fileTest/**")
    public ResponseEntity<Resource>  downloadFile(HttpServletResponse response, HttpServletRequest request){
        String filePath;
        try {
            request.setCharacterEncoding("UTF-8");
          //剔除前面 /file/ 后的；
            filePath = request.getRequestURI();

            filePath =new String (filePath.getBytes("iso8859-1"),"utf-8");
        } catch (Exception ex) {
            throw new MyFileNotFoundException("Could not utf-8.", ex);
        }
        filePath = filePath.substring(6);
        //System.out.println("lujing==" + filePath);
        return downloadFile(filePath,request);
    }
 */

    //直接根据文件系统路径名来下载的模式：
    //用于支持中文的名字， 中间子路径也可支持中文的。
   /// @GetMapping("/file/**/{fileName}")
   //  public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request)
     @RequestMapping(method= RequestMethod.GET ,value="/file/**")
     public ResponseEntity<Resource> downloadFile(HttpServletResponse response, HttpServletRequest request){
        String fileNameRelative =request.getServletPath().substring(6);
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileNameRelative);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    //不考虑该文件已经被引用的情况时。
    //立刻删除刚刚上传的文件 ID;
    @PostMapping(value = "files/{id}/delete")
    public UploadFileResponse revertUploadFile(@PathVariable String id, @RequestParam("fileName") String fileName) {
        //String fileName = fileStorageService.storeFile(file);
        //todo:删除 上传的对象？？：｛没地方引用的文件对象可删除文件系统的对应文件｝
        return new UploadFileResponse(fileName, id,
                "成功剔除", 0);
    }

    //public ResponseEntity<Resource>  downloadFile(@RequestParam(name = "id") String id, HttpServletRequest request)
    //使用uniqueFileId模式，保证前后端　安全文件共享；可以在这里插入复杂的安全控制逻辑，避免文件被非授权读取。
    //不是直接使用文件系统的路径文件名去获取文件，而是给出文件对象ID，再根据ID映射出真正文件URL，后端服务器读取文件内容数据后倒手给客户端的。
    @GetMapping(value = "files/{id}/load")
    public ResponseEntity<Resource> downloadFile(@PathVariable String id, HttpServletRequest request)
    {
        String  filePath=fileService.getFileOfID(id);
        //根据ID映射出实际的文件系统文件URL;
        //文件对象检查，权限控制等。
        //String fileNameRelative =request.getServletPath().substring(6);
        //加载相应URL文件内容。 Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(filePath);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        //设置Access-Control-Expose-Headers浏览器需要读取的字段。
        //跨域: 文件下载时可以让客户显示文件原名response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        String contentDisposition=null;
        try {
            contentDisposition= "attachment;filename*=UTF-8''" + URLEncoder.encode(resource.getFilename(), "UTF-8");
        } catch (Exception ex) {
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition" )
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }
    //.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")

}



//@RequestParam @RequestBody @PathVariable 等参数绑定注解详解 https://www.cnblogs.com/guoyinli/p/7056146.html
