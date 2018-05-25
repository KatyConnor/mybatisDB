package hx.data.mybatis.aop;

import com.alibaba.fastjson.JSONArray;
import hx.data.mybatis.annotation.Like;
import hx.data.mybatis.commonenum.MapperSelectConditionStaticConstantEnum;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.beans.BeanGenerator;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @Author mingliang
 * @Date 2017-09-15 23:07
 */
//@Aspect
//@Component
public class MapperSelectParamAop {

    private final static Logger LOGGER = LoggerFactory.getLogger(MapperSelectParamAop.class);
    private final static String UNDER_LINE = "_";
    private final static String PER_CENT = "%";
    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 定义拦截规则：拦截com.hc.ala.scheduler.base.mapperdao.BaseMapper.findDynamicConditional 方法
     */
    @Pointcut("execution(* hx.data.mybatis.mapper.BaseSelectConditionMapper.findDynamicConditional(..))")
    public void paramSetPointcut(){}

    /**
     * 拦截器具体实现,
     * @param pjp
     * @return JsonResult（被拦截方法的执行结果，或需要登录的错误提示。） ProceedingJoinPoint
     */
    @Around("paramSetPointcut()")
    public Object doBefore(ProceedingJoinPoint pjp){
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Object[] args = pjp.getArgs(); // 获取拦截方法的参数

        LOGGER.info("调用Mapper接口 ：{}.{}, 入参：{}",signature.getDeclaringType(), signature.getMethod().getName(),
                JSONArray.toJSONStringWithDateFormat(args,DATE_FORMAT));

        args[0] = convert(args);
        Object obj;
        try {
            obj = pjp.proceed(args);
        } catch (Throwable throwable) {
            LOGGER.info("修改{}.{} 入参失败！",signature.getDeclaringType(),signature.getMethod().getName());
            throw new RuntimeException("参数修改失败",throwable);
        }
        return obj;
    }

    /**
     * 根据参数生成代理参数类
     * @param args 拦截方法参数
     * @return
     */
    private Object convert(Object[] args){
        Object object =  args[0];
        Class clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        BeanGenerator beanGenerator = new BeanGenerator();
        Map<String,String> valueMap = new LinkedHashMap<>();
        for (Field field : fields) {
            field.setAccessible(true);
            // 检查属性字段是否添加注解
            Annotation[] annotations = field.getDeclaredAnnotations();
            Like like = null;
            boolean likeFlag = false;
            if (null != annotations && annotations.length > 0) {
                for (int j = 0; j < annotations.length; j++) {
                    if (MapperSelectConditionStaticConstantEnum.LIKE.getCode().equals(annotations[j].annotationType().getSimpleName())) {
                        likeFlag = true;
                        like = (Like) annotations[j];
                        break;
                    }
                }
            }
            // 设置模糊查询的值，如果源对象的值long,Integer,int,long的需要把代理对象的该字段的值设置为String
            if (likeFlag) {
                if (field.getType().toString().endsWith("String") || field.getType().toString().endsWith("Integer") ||
                        field.getType().toString().endsWith("Long") || field.getType().toString().endsWith("long") ||
                        field.getType().toString().endsWith("int")) {
                    beanGenerator.addProperty(field.getName(), String.class);
                    String value = "";
                    try {
                        if ((StringUtils.isNotBlank(like.before()) && StringUtils.isNotBlank(like.after())) || StringUtils.
                                isNotBlank(like.around())) {
                            value = PER_CENT + field.get(object) + PER_CENT;
                        }

                        if (StringUtils.isNotBlank(like.before())) {
                            value = PER_CENT + field.get(object);
                        }

                        if (StringUtils.isNotBlank(like.after())) {
                            value = field.get(object) +PER_CENT;
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("get field value filed!");
                    }
                    valueMap.put(field.getName(), value);
                } else {
                    throw new RuntimeException("该类型不自持模糊查询！" + field.getType());
                }
            } else {
                beanGenerator.addProperty(field.getName(), field.getType());
            }
        }
        return createBena(beanGenerator,object,valueMap,clazz);
    }

    /**
     *  根据设置的属性和目标对象class生成代理对象
     * @param beanGenerator
     * @param object
     * @param valueMap
     * @param clazz
     * @return
     */
    private Object createBena(BeanGenerator beanGenerator,Object object,Map<String,String> valueMap,Class clazz){
        Object obj =  beanGenerator.create();
        BeanCopier copier = BeanCopier.create(clazz,obj.getClass(),false);
        copier.copy(object,obj,null);

        Field[] cglibFields = obj.getClass().getDeclaredFields();
        for (Field field : cglibFields){
            field.setAccessible(true);
            String[] keys = field.getName().split(UNDER_LINE);
            String key = keys[keys.length-1];
            if (valueMap.containsKey(key)){
                String str = valueMap.get(key);
                try {
                    field.set(obj,str);
                } catch (IllegalAccessException e) {
                    LOGGER.info("filed to modify field value");
                }
            }
        }
        return obj;
    }
}
