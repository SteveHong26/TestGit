package cn.itcast.core.controller.upload;

import cn.itcast.core.pojo.page.Result;
import cn.itcast.core.utils.fastDFS.FastDFSClient;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    //public String uploadFile(String fileName, String extName, NameValuePair[] metas)

    @RequestMapping("/uploadFile.do")
    public Result uploadFile(MultipartFile file) throws Exception {
        try {
            String confPath = "classpath:fastDFS/fdfs_client.conf";
            FastDFSClient fastDFSClient = new FastDFSClient(confPath);
            String originalFilename = file.getOriginalFilename();
            String extName = FilenameUtils.getExtension(originalFilename);
            String url = fastDFSClient.uploadFile(file.getBytes(), extName, null);
            url = FILE_SERVER_URL + url;
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"😭!!! Something could be wrong");
        }

    }
}
