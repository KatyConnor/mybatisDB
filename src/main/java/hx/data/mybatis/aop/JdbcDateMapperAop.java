package hx.data.mybatis.aop;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import hx.data.mybatis.annotation.*;
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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static hx.data.mybatis.commonenum.MapperSelectConditionStaticConstantEnum.*;

/**
 * @Author mingliang
 * @Date 2017-11-22 16:57
 */
@Aspect
@Component
public class JdbcDateMapperAop {

    private final static Logger LOGGER = LoggerFactory.getLogger(JdbcDateMapperAop.class);
    private final static String UNDER_LINE = "_";
    private final static String PER_CENT = "%";
    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static SimpleDateFormat sf;

    /**
     * 定义拦截规则：拦截com.hc.ala.scheduler.base.mapperdao.BaseMapper.findDynamicConditional 方法
     */
    @Pointcut("execution(* hx.data.mybatis.mapper.BaseSelectConditionMapper.*(..))")
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

        setPage(args[0],signature.getMethod().getName());

        Object obj;
        convert(args[0]);
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
     * @param object 拦截方法参数
     * @return
     */
    private Object convert(Object object){
        Class clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        BeanGenerator beanGenerator = new BeanGenerator();
        Map<String,String> valueMap = new LinkedHashMap<>();
        boolean date = false;
        for (Field field : fields) {
            field.setAccessible(true);
            /* 字段为时间，判断是否添加注解，没添加注解将时间类型修改为String类型，如果添加了注解，注解类型不是下面几种类型时，
               将Date类型修改为String类型，其他注解不变
            */
            if (field.getType().getSimpleName().equals("Date")){
                // 查看条件注解，是否给予了查询时间的格式，没有就是默认格式，有就取设置的格式
                date = true;
                setDateFormat(field);
                beanGenerator.addProperty(field.getName(), String.class);
                try {
                    Object value = field.get(object);
                    valueMap.put(field.getName(), value != null?sf.format(value):null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("get field value failed!");
                }
            }else {
                beanGenerator.addProperty(field.getName(), field.getType());
            }
        }
        if (!date){
            return object;
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

        if ( valueMap.size() <=0 ){
            return obj;
        }

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
                    LOGGER.info("failed to modify field value");
                }
            }
        }
        return obj;
    }

    private static void setDateFormat(Field field){
        Annotation[] annotations = field.getDeclaredAnnotations();
        boolean next = false;
        for (Annotation annotation : annotations){
            String annotationType = annotation.annotationType().getSimpleName();
            switch (getByCode(annotationType)){
                case EQUAL_TO:
                    initDateFormat(((EqualTo) annotation).format());
                    next = true;
                    break;
                case NOT_EQUAL_TO:
                    initDateFormat(((NotEqualTo) annotation).format());
                    next = true;
                    break;
                case BETWEEN_START:
                    initDateFormat(((BetweenStart) annotation).format());
                    next = true;
                    break;
                case BETWEEN_END:
                    initDateFormat(((BetweenEnd) annotation).format());
                    next = true;
                    break;
                case NOT_BETWEEN_START:
                    initDateFormat(((NotBetweenStart) annotation).format());
                    next = true;
                    break;
                case NOT_BETWEEN_END:
                    initDateFormat(((NotBetweenEnd) annotation).format());
                    next = true;
                    break;
                case GREATER_THAN:
                    initDateFormat(((GreaterThan) annotation).format());
                    next = true;
                    break;
                case GREATER_THAN_OR_EQUAL_TO:
                    initDateFormat(((GreaterThanOrEqualTo) annotation).format());
                    next = true;
                    break;
                case LESS_THAN:
                    initDateFormat(((LessThan) annotation).format());
                    next = true;
                    break;
                case LESS_THAN_OR_EQUAL_TO:
                    initDateFormat(((LessThanOrEqualTo) annotation).format());
                    next = true;
                    break;
                default:
                    throw new IllegalArgumentException(String.format("注解类型错误！ @%s",annotationType));
            }
            if (next){
                break;
            }
        }
    }

    private static void initDateFormat(String format){
        sf = new SimpleDateFormat(StringUtils.isNotBlank(format)?format:DATE_FORMAT);
    }

    private void setPage(Object object,String method){
        if ("findDynamicConditionalByPage".equals(method)){
            Class clazz = getSuperclass(object);
            while (!"PageParam".equals(clazz.getSimpleName())){
                clazz = getSuperclass(object);
            }

            try {
                Field pageNum = clazz.getDeclaredField("pageNum");
                Field pageSize = clazz.getDeclaredField("pageSize");
                pageNum.setAccessible(true);
                pageSize.setAccessible(true);
                PageHelper.startPage((int) pageNum.get(object),(int) pageSize.get(object));

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private Class getSuperclass(Object object){
        return object.getClass().getSuperclass();
    }

}
