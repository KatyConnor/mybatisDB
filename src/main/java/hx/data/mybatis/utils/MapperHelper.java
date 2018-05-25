package hx.data.mybatis.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.util.Map;

/**
 *
 * mapper文件读取和sql解析
 * @Author mingliang
 * @Date 2018-03-12 10:18
 */
public class MapperHelper {

    private static String classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();

    private String mapperUrl;

    public String getMapperUrl() {
        return mapperUrl;
    }

    public void setMapperUrl(String mapperUrl) {
        this.mapperUrl = mapperUrl;
    }

    public void readMapper(){
        if (null == mapperUrl){
            new RuntimeException(String.format("*.mapper文件不存在!, url = %s",mapperUrl));
        }
        if (mapperUrl.indexOf("*") != -1){
            File file = FileReadStream.getResources(classPath+mapperUrl.split("\\*")[0]);
            File[] fileSources = file.listFiles();
            for (int i = 0; i < fileSources.length; i++) {
                if (fileSources[i].isFile()) {
                    //读取某个文件夹下的所有文件
                    try {
                        InputStream inputStream = FileReadStream.readMapper(fileSources[i]);
                        byte[] stream = new byte[1024];
                        inputStream.read(stream);
                        System.out.println(new String(stream));
                        // 解析读取的字符串
                        analysisStringReader(new String(stream));
                    } catch (FileNotFoundException e) {
//                        LOGGER.error("",e);
                    } catch (IOException e) {
//                        LOGGER.error("",e);
                    }
                }
            }
        }else {

        }
    }

    private void analysisStringReader(String reader){
        String[] readers = reader.split(";");
        // 解析文件头，文件头必须是import引入相关变量，若果没有import就解析dao接口，然后再解析接口的调用方法，然后在解析对应的sql
        for (String str : readers){
            // 解析import部分
            if (str.indexOf("import") != -1){
                String[] importStrs = str.split(" ");

            }
        }

    }

    public static void main(String[] args) {
        MapperHelper mapperHelper = new MapperHelper();
        mapperHelper.mapperUrl = "/mapper/*.mapper";
        mapperHelper.readMapper();

    }
}
