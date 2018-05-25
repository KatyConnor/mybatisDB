package hx.data.mybatis.plugin;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @Author mingliang
 * @Date 2018-03-12 17:38
 */
public class MapperBufElementType extends IElementType {

    public MapperBufElementType(@NotNull String debugName) {
        super(debugName, MapperSqlLanguage.getInstance());
    }
}
