package com.pinyougou.upload.controller;

import com.pinyougou.common.util.FastDFSClient;
import entity.Result;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.upload *
 * @since 1.0
 */
@RestController
@RequestMapping("/upload")
public class UploadController {

    /**
     *
     * @param file
     */
    @RequestMapping("/uploadFile")
    @CrossOrigin(origins = {"http://localhost:9102","http://localhost:9101"},allowCredentials = "true")
    public Result upload(MultipartFile file){
        try {
            //1.获取字节数组
            byte[] bytes = file.getBytes();
            //2.获取文件的扩展名
            String originalFilename = file.getOriginalFilename();//  aaaa.jpg
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
            //3.调用fastdfsclient的代码上传图片
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fastdfs_client.conf");
            String path = fastDFSClient.uploadFile(bytes, extName);// group1/M00/00/06/wKgZhV0oB4GAVJehAAClQrJOYvs386.jpg
            String realPath = "http://192.168.25.133/"+path;

            return new Result(true,realPath);

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"错了");
        }
    }
}
