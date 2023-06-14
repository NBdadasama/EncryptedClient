package com.middleware.demo.translation;


import com.middleware.demo.translation.baidu.TransApi;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translate {




    public Translate() {}



    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20230406001630213";
    private static final String SECURITY_KEY = "ZhgiMToP0PmM2uiuBfuh";
    String temp = null;
    String InPut;

    TransApi api = new TransApi(APP_ID, SECURITY_KEY);

    //输出翻译后的字符
    public void putOut()
    {
        System.out.println(temp);

    }

    //获取需要翻译的文本
    public String inPut(String input,String language )
    {   InPut = input;
        return TranslateStart(language);
    }

    public  String ascii2native(String ascii) {//解码方法

        List<String> ascii_s = new ArrayList<String>();

        String zhengz= "\\\\u[0-9,a-f,A-F]{4}";

        Pattern p = Pattern.compile(zhengz);

        Matcher m=p.matcher(ascii);

        while (m.find()){

            ascii_s.add(m.group());
        }

        for (int i = 0, j = 2; i < ascii_s.size(); i++) {

            String code = ascii_s.get(i).substring(j, j + 4);

            char ch = (char) Integer.parseInt(code, 16);

            ascii = ascii.replace(ascii_s.get(i),String.valueOf(ch));
        }
        return ascii;
    }

    //开始翻译
    public String TranslateStart(String languages)
    {


        String query = InPut;
        //对百度翻译获得的文档进行拆分;

        StringTokenizer token = new StringTokenizer(api.getTransResult(query, "auto", languages),"\"}]}");
        while(token.hasMoreTokens()){//这个翻译方法
            temp = token.nextToken();
        }

        temp = ascii2native(temp);//译文转化
        //获取需要的文本
        return temp;
    }






}
