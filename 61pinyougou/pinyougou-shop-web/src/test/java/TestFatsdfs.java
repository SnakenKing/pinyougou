import com.pinyougou.common.util.FastDFSClient;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package PACKAGE_NAME *
 * @since 1.0
 */
public class TestFatsdfs {

    //上传图片
    @Test
    public void upload() throws Exception{
        //1.创建配置文件 配置服务器的IP和端口
        //2.加载配置文件
        ClientGlobal.init("C:\\Users\\Administrator\\ideaChanggou\\61pinyougou\\pinyougou-shop-web\\src\\main\\resources\\config\\fastdfs_client.conf");
        //3.创建trackerclient
        TrackerClient trackerClient = new TrackerClient();
        //4.获取到trackerserver
        TrackerServer trackerServer = trackerClient.getConnection();
        //5.创建storageclient
        StorageClient storageClient = new StorageClient(trackerServer,null);
        //6.使用storageclient上传图片
        /**
         * 参数1 表示本地文件路径
         * 参数2 表示文件的扩展名  不要点
         * 参数3 表示文件的元数据()
         */
        String[] jpgs = storageClient.upload_file("C:\\Users\\Administrator\\Pictures\\5b13cd6cN8e12d4aa.jpg", "jpg", null);

        for (String jpg : jpgs) {
            System.out.println(jpg);//图片的路径(file_id)
        }
    }

    @Test
    public void uploadClient() throws Exception{
        FastDFSClient fastDFSClient = new FastDFSClient("C:\\Users\\Administrator\\ideaChanggou\\61pinyougou\\pinyougou-shop-web\\src\\main\\resources\\config\\fastdfs_client.conf");
        String path = fastDFSClient.uploadFile("C:\\Users\\Administrator\\Pictures\\timg.jpg", "jpg");
        System.out.println(path);
    }
}
