import org.csource.fastdfs.*;
import org.junit.Test;

import java.io.OutputStream;

public class fastDFS_Demo {

    @Test
    public void FastDFSDemo() throws Exception {

        //指定fastDFS配置文件地址
        String fileName = "E:\\PinYoGo\\FastDFS\\src\\main\\resources\\fdfs_client.conf";
        //第一步 获取Tracker Client
        ClientGlobal.init(fileName);
        //获取TrackerClient
        TrackerClient trackerClient = new TrackerClient();

        //获取TrackerServer
        TrackerServer tkServer = trackerClient.getConnection();
        OutputStream outputStream = tkServer.getOutputStream();

        //获取 存储服务器的客户端
        StorageClient1 storageClient1 = new StorageClient1(tkServer,null);
        //指定图片的路径
        String path = storageClient1.upload_appender_file1("E:\\MyPicture\\2.png","png",null);
        System.out.println(path);
    }
}
