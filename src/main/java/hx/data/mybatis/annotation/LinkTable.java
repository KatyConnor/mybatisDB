package hx.data.mybatis.annotation;

import hx.data.mybatis.commonenum.ContinuousTableTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author mingliang
 * @Date 2017-11-23 15:45
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LinkTable {

    Class<?>[] linkTable();

    String type() default "WHERE";
}
