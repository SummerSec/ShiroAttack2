import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.util.ByteSource;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

public class ShiroEncryTest {

    public static void main(String[] args) {
        AesCipherService aesCipherService=new AesCipherService();
        Key key=aesCipherService.generateNewKey(128);
        String keystr= Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("key: "+keystr);
        String text="dadasxwfwqfwf";
        byte[] encrytext=shiroencry(text.getBytes(),Base64.getDecoder().decode(keystr));
        byte[] decrytext=shirodecry(encrytext,Base64.getDecoder().decode(keystr));
        System.out.println(new String(decrytext));

    }

    public static byte[] shiroencry(byte[] data,byte[] key){

        AesCipherService aesCipherService=new AesCipherService();
        ByteSource byteSource= aesCipherService.encrypt(data,key);
        return byteSource.getBytes();
    }

    public static byte[] shirodecry(byte[] data,byte[] key){

        AesCipherService aesCipherService=new AesCipherService();
        ByteSource byteSource= aesCipherService.decrypt(data,key);
        return byteSource.getBytes();

    }

}
