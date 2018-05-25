package hx.data.mybatis.mapper;

import com.alibaba.fastjson.JSONArray;
import com.sun.org.apache.bcel.internal.util.ClassLoader;
import com.sun.org.apache.xpath.internal.operations.Bool;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.provider.base.BaseSelectProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author mingliang
 * @Date 2017-10-10 11:04
 */
public class Test {

    public static void main(String[] args) {
//        ClassLoader classLoader = new ClassLoader();
//        Class<?> templateClass = null;
//        try {
//            templateClass = classLoader.loadClass("tk.mybatis.mapper.provider.base.BaseSelectProvider");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        Class<?> clazz = templateClass;
//        if(!MapperTemplate.class.isAssignableFrom(clazz)){
//            System.out.println("no from Mapper");
//        }
//        if (booleanTest()){
//            System.out.println("true");
//        }else {
//            System.out.println("false");
//        }

        int i = 1963509270;
        System.out.println(i);
    }

    public static Boolean booleanTest(){
        if (1 == 1){
            throw new IllegalArgumentException("每个属性字段查询条件注解只能存在一个！");
        }
       return true;
    }

    public static void mapLamTest(){
        Map<String,Object> map = new HashMap<>();
        for (int i = 0; i < 100000; i++){
            map.put("key"+i,i);
        }


        StringBuilder sb2 = new StringBuilder();
        long startTime2 = System.currentTimeMillis();
        for (Map.Entry entry: map.entrySet()){
            sb2.append(entry.getValue());
        }
        System.out.println("3 "+sb2);
        System.out.println("3耗时="+(System.currentTimeMillis() - startTime2));

        StringBuilder sb1 = new StringBuilder();
        long startTime1 = System.currentTimeMillis();
        for (Map.Entry entry: map.entrySet()){
            sb1.append(entry.getValue());
        }
        System.out.println("2 "+sb1);
        System.out.println("2耗时="+(System.currentTimeMillis() - startTime1));


        StringBuilder sbs = new StringBuilder();
        long startTime = System.currentTimeMillis();
        map.forEach((k,v) ->{
            sbs.append(v);
        });
        System.out.println("1 "+sbs);
        System.out.println("1耗时="+(System.currentTimeMillis() - startTime));
    }
}
