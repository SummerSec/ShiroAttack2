import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

import java.util.List;

/**
 * @ClassName: http
 * @Description: TODO
 * @Author: Summer
 * @Date: 2021/9/25 13:37
 * @Version: v1.0.0
 * @Description:
 **/
public class http {
    public static void main(String[] args) {
        String tar = "http://cms.changdu.gov.cn:8002/uas-center/a/cas";
        HttpResponse re = HttpRequest.get(tar)
                .header("Cookie","rememberMe=1")
                .execute();

        List<String> resp = re.headerList("Set-Cookie");

        System.out.println(resp);
        System.out.println(resp.toString());

    }
}
