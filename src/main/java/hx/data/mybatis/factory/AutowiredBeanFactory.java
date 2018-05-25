package hx.data.mybatis.factory;

import hx.data.mybatis.proxy.ObjectCglibProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author mingliang
 * @Date 2017-09-20 9:28
 */
@Component
public class AutowiredBeanFactory {

    private final static Logger LOGGER = LoggerFactory.getLogger(AutowiredBeanFactory.class);

    @Autowired
    private ObjectCglibProxy objectCglibProxy;

    public <T> T create (Class<T> classzz){
        if (classzz == null){
            throw new RuntimeException("class must be not null");
        }
        Object obj = objectCglibProxy.createObject(classzz,null);
        if (null == obj){
            throw  new RuntimeException("Failed to initialize class object！");
        }
        return (T) obj;
    }
}
