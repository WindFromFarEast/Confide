package net.confide.common;


public class Common {

    /**
     * 一些用于配置的不变参数
     */
    public interface Constance {
        //手机号码的正则,11位手机号
        String REGEX_MOBILE = "[1][3,4,5,7,8][0-9]{9}$";
        //基础网络请求地址
        String API_URL = "http://192.168.43.142:8080/api/";
//        String API_URL = "http://10.70.58.237:8080/api/";
    }
}
