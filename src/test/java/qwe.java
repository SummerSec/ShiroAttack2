import org.apache.shiro.codec.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName: qwe
 * @Description: TODO
 * @Author: Summer
 * @Date: 2021/10/19 13:51
 * @Version: v1.0.0
 * @Description:
 **/
public class qwe {
    public static void main(String[] args) {
//        KeyGenerator keygen = null;
//        try {
//            keygen = KeyGenerator.getInstance("AES");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        SecretKey deskey = keygen.generateKey();
////        System.out.println(Base64.encodeToString(deskey.getEncoded()));
//        System.out.println(Base64.encodeToString(new byte[]{101, -88, 60, -121, -55, 13, -27, -8, -27, -32, 18, -11, 106, 7, 15, -11}));

        String s = "Basic YWxhZGRpbjpvcGVuc2VzYW1l";
        System.out.println(s.replaceAll("Basic", ""));
    }

}
