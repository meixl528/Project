package Reflect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
/**
 * @name        TestReflect
 * @description 在泛型为Integer的List中存放一个String类型的对象
 * @author      meixl
 * @date        2017年8月29日上午11:40:08
 * @version
 */
public class TestReflect {
	
    public static void main(String[] args) throws Exception {
        List<Integer> list = new ArrayList<>();
        
        /* 方式一:反射 */
        Method method = list.getClass().getMethod("add", Object.class);
        method.invoke(list, "Java反射机制实例。");
        System.out.println(list.get(0));
        
        /* 方式而:强转 */
        ((List<Object>)(List<?>)list).add("字符串");
        ((List<Object>)(List<?>)list).add(true);
        System.out.println(list);
    }
    
}
