package hx.data.mybatis.plugin;

import com.intellij.lang.Language;

/**
 * @Author mingliang
 * @Date 2018-03-09 16:57
 */
public class MapperSqlLanguage extends Language {

    private MapperSqlLanguage() {
        super("mapper");
    }

    private static class SigleObject{
        public static final MapperSqlLanguage INSTANCE = new MapperSqlLanguage();
    }

    public static MapperSqlLanguage getInstance(){
        return SigleObject.INSTANCE;
    }
}
