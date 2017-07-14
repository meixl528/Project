package com.ssm.mail.dto;

import java.net.URI;

public class MessageAddress {

    // {
    // MessageAddress messageAddress = new MessageAddress();
    // messageAddress.setType("ROLE");
    // messageAddress.setAddress("3");
    //
    //
    // messageAddress.setType(USER);
    // messageAddress.setAddress("1");
    //
    // new MessageAddressService().toAddressString();
    // }

    public static final String USER = "user";
    public static final String MEMEBER = "memeber";
    public static final String ROLE = "role";
    public static final String PHONE = "phone";
    public static final String MAIL = "mail";

    private String type;
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // role://1
    static public MessageAddress toAddressObject(String s) {

        MessageAddress address = new MessageAddress();
        if(s.contains("://")){
        URI uri = URI.create(s);
        address.setAddress(uri.getAuthority());
        // .getHost());
        address.setType(uri.getScheme());
        } else {
            address.setType("email");
            address.setAddress(s);
        }

        return address;

    }

    public static void main(String[] args) {
        String s1 = "m123://adsb";
        String s2 = "Inull://adsb_sss";
        String s3 = "adsb";

        URI uri = URI.create(s2);

        System.out.println(uri.getHost());
        System.out.println(uri.getAuthority());
        System.out.println(uri.getRawAuthority());
        System.out.println(uri.getScheme());
        System.out.println(uri.getPath());

        //
        // String substring = s1.substring(1, s1.length());
        // System.out.println(substring);
        // MessageAddress addressObject = toAddressObject(s1);
        // System.out.println(addressObject.getType());
        // System.out.println(addressObject.getAddress());
        // addressObject = toAddressObject(s2);
        // System.out.println(addressObject.getType());
        // System.out.println(addressObject.getAddress());
        // addressObject = toAddressObject(s3);
        // System.out.println(addressObject.getType());
        // System.out.println(addressObject.getAddress());
    }

}
