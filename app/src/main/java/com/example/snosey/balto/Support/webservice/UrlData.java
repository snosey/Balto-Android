package com.example.snosey.balto.Support.webservice;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by ahmed on 4/24/2017.
 */

//customize url data which need to send in post
public class UrlData implements Serializable {
    String string = "";

    public void add(String s, String d) {
        try {
            if (string.equals(""))
                string += s + "=" + URLEncoder.encode(d, "UTF-8");
            else
                string += "&" + s + "=" + URLEncoder.encode(d, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String get() {
        return string;
    }
}
