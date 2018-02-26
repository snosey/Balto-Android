package com.example.snosey.balto.login;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Snosey on 2/6/2018.
 */

public class NewAccountObject implements Serializable {
    public String firstName = "";
    public String lastName = "";
    public String email = "";
    public String password = "";
    public String gender = "";
    public String lang = "";
    public String governrate = "";
    public String kindDoctor = "";
    public String specialization = "";
    public String id_provider = "";
    public String provider_kind = "";
    public String phone = "";
    public String logo = "";
    public String type = "";
    public List<String> city = new ArrayList<>();
    public List<String> id_sub = new ArrayList<>();
}