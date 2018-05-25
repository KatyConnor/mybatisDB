package hx.data.mybatis.cglib;

import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.beans.BeanMap;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaProperty;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author mingliang
 * @Date 2017-09-26 10:01
 */
public class BeanProxy {

    /**
     *  cglib
     * @return
     * @throws IllegalAccessException
     */
    public static Object create() throws IllegalAccessException {
        BeanGenerator beanGenerator = new BeanGenerator();
        beanGenerator.addProperty("id",Long.class);
        beanGenerator.addProperty("ip",String.class);

        Object object = beanGenerator.create();
        BeanMap beanMap = BeanMap.create(object);

        BeanCopier copier = BeanCopier.create(ObjectPerson.class,object.getClass(),false);
        ObjectPerson objectPerson = new ObjectPerson();
        objectPerson.setAddress("dsffds");
        objectPerson.setCreateTime(new Date());
        objectPerson.setName("张三是");
        objectPerson.setSize(12);
        objectPerson.setUpdateTime(new Date());
        objectPerson.setVersion(1);

        copier.copy(objectPerson,object,null);

        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields){
            field.setAccessible(true);
            System.out.println(field.getName()+"------"+field.get(object));
        }

        return null;
    }

    /**
     *  org.apache.commons.beanutils
     * @return
     * @throws IllegalAccessException
     */
    public static Object beanCreate() throws IllegalAccessException {
        DynaProperty property = new DynaProperty("id", Long.class);
        DynaProperty property1 = new DynaProperty("ip", String.class);
        DynaProperty property2 = new DynaProperty("address", String.class);
        DynaProperty property3 = new DynaProperty("name", String.class);
        BasicDynaClass basicDynaClass = new BasicDynaClass("objectPerson", null, new DynaProperty[]{property, property1,property2,property3});
        BasicDynaBean basicDynaBean = new BasicDynaBean(basicDynaClass);

        ObjectPerson objectPerson = new ObjectPerson();
        objectPerson.setAddress("dsffds");
        objectPerson.setCreateTime(new Date());
        objectPerson.setName("张三是");
        objectPerson.setSize(12);
        objectPerson.setUpdateTime(new Date());
        objectPerson.setVersion(1);

        ObjectPerson person = new ObjectPerson();

        try {
            BeanUtils.copyProperties(basicDynaBean,objectPerson);
            BeanUtils.copyProperties(person,objectPerson);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        Map<String, Object> map = basicDynaBean.getMap();
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            System.out.println(key + ":" + map.get(key));
        }

        System.out.println(person.toString());

        return null;
    }

    public static void main(String[] args) {
//        try {
////            create();
//            beanCreate();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }

        ObjectPerson objectPerson = ObjectAdvance.getInstance();
        objectPerson.setAddress("fsdfsdfsd");
        String address = objectPerson.getAddress();
        System.out.println(address);
    }
}
