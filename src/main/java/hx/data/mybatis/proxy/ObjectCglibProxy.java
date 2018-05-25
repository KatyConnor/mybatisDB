package hx.data.mybatis.proxy;


import hx.data.mybatis.annotation.Like;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author mingliang
 * @Date 2017-09-17 21:58
 */
@Component
public class ObjectCglibProxy implements MethodInterceptor {

    private Annotation[] annotation;
    private Map<String,Like> annotationMap = new HashMap<>();

    /**
     * 创建代理类对象
     * @param object 被代理对象
     * @return 代理类对象
     */
    public  <T> T createObject(Class<T> object,Object[] args){

        //利用Enhancer来创建代理类
        Enhancer enhancer=new Enhancer();
        //为目标对象指定父类
        enhancer.setSuperclass(object);
        //设置回调函数
        enhancer.setCallback(this);
        //返回生成的代理类
        if (args !=null && args.length > 0){
            Class<?> [] clazzType = new Class[args.length];
            for (int i =0; i<args.length; i++){
                clazzType[i] = args[i].getClass();
            }
            return (T) enhancer.create(clazzType,args);
        }else {
            return (T) enhancer.create();
        }
    }


    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        //调用目标类的方法
        Object intercept = methodProxy.invokeSuper(o, args);
        return intercept;
    }
}
