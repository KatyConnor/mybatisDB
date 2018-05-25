package hx.data.mybatis.proxy;

import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;

import java.lang.reflect.Method;

/**
 *
 *  jdk 动态代理
 * @Author mingliang
 * @Date 2017-09-17 20:18
 */
public class ObjectProxy implements InvocationHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(ObjectProxy.class);

    private Object target;

    /**
     *  绑定委托对象并返回一个【代理占位】,该方法的主要目的是生成一个代理对象，参数就是类加载器，和正式委托对象
     *
     * @param target   真实对象
     * @param interfaces 代理对象【占位】
     * @return
     */
    public Object bind(Object target, Class[] interfaces) {
        this.target = target;
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),target.getClass().getInterfaces(),this);
    }


    /**
     * 同过代理对象调用方法首先进入这个方法.
     * @param o        代理对象
     * @param method   方法,被调用方法.
     * @param args     方法的参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        LOGGER.info("--------------------cglib动态代理-----------------------------,{}", JSONArray.toJSONString(args));
        Object result = null;
        // 反射方法前调用
        LOGGER.info("-----------方法调用之前进行处理-----------");
        // 反射执行代理对象的方法
        result = method.invoke(target,args);
        // 反射方法之后进行调用
        LOGGER.info("------------目标类型的方法已经处理完毕---------------");
        // 返回结果
        return result;
    }

}
