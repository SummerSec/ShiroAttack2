import com.summersec.attack.deser.util.StandardExecutorClassLoader;


public class demo {
    public static void main(String[] args) {
        StandardExecutorClassLoader classLoader1 = new StandardExecutorClassLoader("1.9.2");
        StandardExecutorClassLoader classLoader2 = new StandardExecutorClassLoader("1.8.3");
        try {
            Class o1 = classLoader1.loadClass("org.apache.commons.beanutils.BeanComparator");
            Class o2 = classLoader2.loadClass("org.apache.commons.beanutils.BeanComparator");
            o1.newInstance();
            o2.newInstance();
            System.out.println(o1.getPackage());
            System.out.println(o2.getPackage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
