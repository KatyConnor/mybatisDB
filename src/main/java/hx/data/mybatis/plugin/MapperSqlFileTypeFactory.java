package hx.data.mybatis.plugin;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import com.intellij.util.PairConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * @Author mingliang
 * @Date 2018-03-09 18:14
 */
public class MapperSqlFileTypeFactory extends FileTypeFactory {

    @Override
    public void createFileTypes(@NotNull PairConsumer<FileType, String> pairConsumer) {
        pairConsumer.consume(MapperSqlFileType.getInstance(),MapperSqlFileType.getInstance().getDefaultExtension());
    }
}
