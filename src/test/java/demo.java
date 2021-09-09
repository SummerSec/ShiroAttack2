import com.summersec.attack.deser.util.StandardExecutorClassLoader;

/**
 * @ClassName: demo
 * @Description: TODO
 * @Author: Summer
 * @Date: 2021/9/9 10:52
 * @Version: v1.0.0
 * @Description:
 **/
public class demo {
    public static void main(String[] args) {
        StandardExecutorClassLoader classLoader = new StandardExecutorClassLoader("1.9.2");
        try {
            Class o = classLoader.loadClass("org.apache.commons.beanutils.BeanComparator");
            o.newInstance();
            System.out.println(o.getPackage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
