package hx.data.mybatis.plugin;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @Author mingliang
 * @Date 2018-03-09 17:46
 */
public class MapperSqlFileType extends LanguageFileType {

    private MapperSqlFileType(){
        super(MapperSqlLanguage.getInstance());
    }

    public static MapperSqlFileType getInstance(){
        return SigleObject.INSTANCE;
    }

    private static class SigleObject{
        public static final MapperSqlFileType INSTANCE = new MapperSqlFileType();
    }

    @NotNull
    @Override
    public String getName() {
        return "Mapper sql file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Mapper sql language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "mapper";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return MapperSqlIcons.ICON_FILE;
    }


}
