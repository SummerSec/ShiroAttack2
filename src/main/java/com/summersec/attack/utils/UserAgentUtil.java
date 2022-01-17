package com.summersec.attack.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserAgentUtil {
    private static List<String> list = new ArrayList<String>();

    static {
        list.add("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
        list.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.1 Safari/537.36");
        list.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.0 Safari/537.36");
        list.add("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2226.0 Safari/537.36");
        list.add("Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko");
        list.add("Mozilla/5.0 (compatible, MSIE 11, Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko");
        list.add("Mozilla/5.0 (compatible; MSIE 10.6; Windows NT 6.1; Trident/5.0; InfoPath.2; SLCC1; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET CLR 2.0.50727) 3gpp-gba UNTRUSTED/1.0");
        list.add("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 7.0; InfoPath.3; .NET CLR 3.1.40767; Trident/6.0; en-IN)");
        list.add("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)");
        list.add("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)");
        list.add("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/5.0)");
        list.add("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/4.0; InfoPath.2; SV1; .NET CLR 2.0.50727; WOW64)");
        list.add("Mozilla/5.0 (compatible; MSIE 10.0; Macintosh; Intel Mac OS X 10_7_3; Trident/6.0)");
        list.add("Mozilla/4.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/5.0)");
        list.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/532.2 (KHTML, like Gecko) ChromePlus/4.0.222.3 Chrome/4.0.222.3 Safari/532.2");
        list.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/525.28.3 (KHTML, like Gecko) Version/3.2.3 ChromePlus/4.0.222.3 Chrome/4.0.222.3 Safari/525.28.3");
        list.add("Opera/9.80 (X11; Linux i686; Ubuntu/14.10) Presto/2.12.388 Version/12.16");
        list.add("Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14");
        list.add("Mozilla/5.0 (Windows NT 6.0; rv:2.0) Gecko/20100101 Firefox/4.0 Opera 12.14");
        list.add("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.0) Opera 12.14");
        list.add("Opera/12.80 (Windows NT 5.1; U; en) Presto/2.10.289 Version/12.02");
        list.add("Opera/9.80 (Windows NT 6.1; U; es-ES) Presto/2.9.181 Version/12.00");
        list.add("Opera/9.80 (Windows NT 5.1; U; zh-sg) Presto/2.9.181 Version/12.00");
        list.add("Opera/12.0(Windows NT 5.2;U;en)Presto/22.9.168 Version/12.00");
        list.add("Opera/12.0(Windows NT 5.1;U;en)Presto/22.9.168 Version/12.00");
        list.add("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1");
        list.add("Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0");
        list.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10; rv:33.0) Gecko/20100101 Firefox/33.0");
        list.add("Mozilla/5.0 (X11; Linux i586; rv:31.0) Gecko/20100101 Firefox/31.0");
        list.add("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20130401 Firefox/31.0");
        list.add("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0");
    }

    private static int getRandomIndex() {
        int max = 31;
        int min = 0;
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    public static String getRandomUserAgent(){
        return list.get(getRandomIndex());
    }

}