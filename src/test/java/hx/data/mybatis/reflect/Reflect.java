package hx.data.mybatis.reflect;

import java.util.Collection;

/**
 * @Author mingliang
 * @Date 2017-09-26 11:35
 */
public class Reflect {

    public static void main(String[] args) {
        //设置扫描范围，可以是class文件所在位置例如bin下或者是mysql开头或者mysql结尾的jar,
        //设置为""为全部都扫描,这种比较耗时
        ReflectUtils.createSharedReflections("classes", "bin", "mysql");
        try {
            //调试阶段可以设置每次都全扫描
            //Beans.setDesignTime(true);
//            final Collection<String> subTypes = ReflectUtils.listSubClass(IA.class);//
//            for (final String subType : subTypes) {
//                //这里获取的是所有继承IA的子类
//                System.out.println(subType);
//                final IA impl = ReflectUtils.initClass(subType, IA.class);
//                if (null == impl)
//                    continue;
//                //通过该方式，可以统一做操作，
//                impl.print();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
