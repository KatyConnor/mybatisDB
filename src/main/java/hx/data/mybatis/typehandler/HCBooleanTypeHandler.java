package hx.data.mybatis.typehandler;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author mingliang
 * @Date 2017-08-26 23:30
 */
//@MappedJdbcTypes(JdbcType.CHAR)
//@MappedTypes(Boolean.class)
public class HCBooleanTypeHandler extends BaseTypeHandler<Boolean> {

    public HCBooleanTypeHandler() {
        super();
    }

    @Override
    public void setConfiguration(Configuration c) {
        super.setConfiguration(c);
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType) throws SQLException {
        String value = parameter == true?"Y":"N";
        ps.setString(i,value);
    }

    @Override
    public Boolean getResult(ResultSet rs, String columnName) throws SQLException {
        String resultStr = rs.getString(columnName);
        Boolean result = Boolean.FALSE;
        if (resultStr.equalsIgnoreCase("Y")){
            result = Boolean.TRUE;
        }
        return result;
    }

    @Override
    public Boolean getResult(ResultSet rs, int columnIndex) throws SQLException {
        return super.getResult(rs, columnIndex);
    }

    @Override
    public Boolean getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return super.getResult(cs, columnIndex);
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Boolean aBoolean, JdbcType jdbcType) throws SQLException {

    }

    @Override
    public Boolean getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String resultStr = resultSet.getString(s);
        Boolean result = Boolean.FALSE;
        if (resultStr.equalsIgnoreCase("Y")){
            result = Boolean.TRUE;
        }
        return result;
    }

    @Override
    public Boolean getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return null;
    }

    @Override
    public Boolean getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return callableStatement.getBoolean(i);
    }
}
