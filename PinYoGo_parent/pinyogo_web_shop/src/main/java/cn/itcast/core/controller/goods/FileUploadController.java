package cn.itcast.core.controller.goods;

import cn.itcast.core.pojo.page.Result;
import cn.itcast.core.utils.fastDFS.FastDFSClient;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class FileUploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    /**
     *  上传文件
     * @param file
     * @return
     * @throws Exception
     */
    @RequestMapping("/uploadFile.do")
    public Result uploadFile(MultipartFile file) throws Exception {
        try {
            String confPath = "classpath:fastDFS/fdfs_client.conf";
            FastDFSClient fastDFSClient = new FastDFSClient(confPath);
            String originalFilename = file.getOriginalFilename();
            //使用FileNameUtils工具截取扩展名
            String suffix = FilenameUtils.getExtension(originalFilename);
            String path = fastDFSClient.uploadFile(file.getBytes(),suffix,null);
            path = FILE_SERVER_URL + path;
            return new Result(true,path);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"Oops! Filed to upload!");
        }
    }

}
