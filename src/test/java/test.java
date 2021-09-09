import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @ClassName: test
 * @Description: TODO
 * @Author: Summer
 * @Date: 2021/9/9 10:36
 * @Version: v1.0.0
 * @Description:
 **/
public class test {
    public static void main(String[] args) {
        File file = new File("E:\\myself\\OneDrive - Colegio Público Torre Malmuerta\\Soures\\shiro_attack2.0\\shiro_attack2\\lib\\commons-beanutils-1.9.2.jar");
        URL url = null;
        try {
            url = file.toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLClassLoader classLoader = new URLClassLoader(new URL[]{url});

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
