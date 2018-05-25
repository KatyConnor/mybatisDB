package hx.data.mybatis.cglib;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @Author mingliang
 * @Date 2017-11-17 15:25
 */
public class ObjectAdvance {
    private static Enhancer e;

    static {
        e = new Enhancer();
        e.setSuperclass(ObjectPerson.class);
        e.setCallbacks(new Callback[] { new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method m, Object[] args, MethodProxy mp) throws Throwable {
                if (m.getName().indexOf("set") != -1){
                    System.out.println(args[0]);
                    args[0] = "a" + args[0];
                    System.out.println(args[0]);
                    return mp.invokeSuper(o, args) + "c";
                }
                return mp.invokeSuper(o, null);
            }
        }});
    }

    public synchronized static ObjectPerson getInstance() {
        return (ObjectPerson) e.create();
    }
}
