/*
 * #{copyright}#
 */
package com.ssm.captcha.service;

import java.io.IOException;
import java.io.OutputStream;

import com.ssm.message.profile.SystemConfigListener;

/**
 * 验证码接口.
 * <p>
 * 生成验证码图片以及captchaKey等.
 * @author meixl
 */
public interface ICaptchaManager{

    /**
     * 获取CaptchaKeyName.
     * 
     * @return keyName
     */
    String getCaptchaKeyName();

    /**
     * 获取验证码code.
     * 
     * @return captchaCode
     */
    String generateCaptchaCode();

    /**
     * 生成CaptchaKey.
     * 
     * @return captchaKey
     */
    String generateCaptchaKey();

    /**
     * 根据 code 生成图片数据,写入指定的输出流. <br>
     * 同时验证信息放入 redis
     * 
     * @param captchaKey
     *            放入 redis 需要用的 key(部分)
     * @param captchaCode
     *            生成的 code
     * @throws IOException
     *             IOException
     * @param os
     *            OutputStream输出流
     */
    void generateCaptcha(String captchaKey, String captchaCode, OutputStream os) throws IOException;

    /**
     * 校验验证码的值.
     * 
     * @param captchaKey
     *            cookie 中的 captchaKey
     * @param captchaCode
     *            用户输入的验证码
     * @return 验证是否成功
     */
    boolean checkCaptcha(String captchaKey, String captchaCode);
    
    /**
     * 移除验证码.
     * @param captchaKey
     */
    void removeCaptcha(String captchaKey);
}
