package hx.data.mybatis.utils;

import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 文件读取
 * @Author mingliang
 * @Date 2018-03-12 10:19
 */
public class FileReadStream {

    /**
     * 读取 .mapper 文件 先读取用户配置指定路径，如果用户没有配置，那么就读取resource下的.mapper 结尾的文件
     * @return
     * @throws FileNotFoundException
     */
    public static InputStream readMapper(File file) throws FileNotFoundException {
        if (null == file){
            new RuntimeException(String.format("文件不存在!, url = %s",file));
        }
        return  new FileInputStream(file);
    }

    public static File getResources(String path){
        if (null == path){
            new RuntimeException(String.format("没有指定文件路径！, path = %s",path));
        }
        return new File(path);
    }

    /**
     * 语法验证
     * @return
     */
    public static boolean syntaxVerification(){
        return false;
    }



    public static void main(String[] args) {

    }
}
