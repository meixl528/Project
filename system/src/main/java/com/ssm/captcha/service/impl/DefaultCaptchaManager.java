package com.ssm.captcha.service.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.ssm.captcha.service.ICaptchaManager;

/**
 * 验证码生成工具类.
 * @author meixl
 */
@Service
public class DefaultCaptchaManager implements ICaptchaManager {

    private static final int CAPTCHA_WIDTH = 85;
    private static final int CAPTCHA_HEIGHT = 30;
    private static final int CAPTCHA_CODE_COUNT = 4;
    private static final int CAPTCHA_CODE_X = 19;// 15
    private static final int CAPTCHA_CODE_Y = 26;
    private static final int CAPTCHA_FONT_HEIGHT = 25;
    private static final int CAPTCHA_EXPIRE = 60 * 5;

    private static final int LINE_COUNT = 20;// 40
    private static final int LINE_DY = 12;
    private static final int MAX_RGB = 255;

    private static final char[] CAPTCHA_CODES = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private String category = "hap:cache:captcha";

    private String captchaKeyName = "captcha_key";

    /**
     * 过期时间,默认5分钟.
     */
    private Integer expire = CAPTCHA_EXPIRE;

    public String getCaptchaKeyName() {
        return captchaKeyName;
    }

    public void setCaptchaKeyName(String captchaKeyName) {
        this.captchaKeyName = captchaKeyName;
    }

    /**
     * @return 过期时间,单位秒
     */
    public Integer getExpire() {
        return expire;
    }

    /**
     * captchaCode 有效期.单位 秒.
     *
     * @param expire
     *            过期时间
     */
    public void setExpire(Integer expire) {
        this.expire = expire;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * 生成captchaCode.
     *
     * @return 随机生成的验证码
     */
    public String generateCaptchaCode() {
        // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
        StringBuilder randomCode = new StringBuilder();
        // 创建一个随机数生成器类
        Random random = new Random();
        // 随机产生codeCount数字的验证码。
        for (int i = 0; i < CAPTCHA_CODE_COUNT; i++) {
            // 得到随机产生的验证码数字。
            String code = String.valueOf(CAPTCHA_CODES[random.nextInt(CAPTCHA_CODES.length)]);
            // 将产生的四个随机数组合在一起。
            randomCode.append(code);
        }
        return randomCode.toString();
    }

    /**
     * 生成captchaKey.
     *
     * @return 返回 UUID 形式的 captchaKey
     */
    public String generateCaptchaKey() {
        return UUID.randomUUID().toString();
    }

    /**
     * 根据 code 生成图片数据,写入指定的输出流. <br>
     * 同时验证信息放入 redis
     *
     * @see ICaptchaManager#generateCaptcha(java.lang.String, java.lang.String,
     *      java.io.OutputStream)
     * @throws IOException
     *             IOException
     */
    public void generateCaptcha(String captchaKey, String captchaCode, OutputStream os) throws IOException {

        // 定义图像buffer
        BufferedImage buffImg = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_INT_RGB);
        // Graphics2D gd = buffImg.createGraphics();
        // Graphics2D gd = (Graphics2D) buffImg.getGraphics();
        Graphics gd = buffImg.getGraphics();
        // 创建一个随机数生成器类
        Random random = new Random();
        // 将图像填充为白色
        gd.setColor(Color.WHITE);
        gd.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);

        // 创建字体，字体的大小应该根据图片的高度来定。
        Font font = new Font("Fixedsys", Font.ITALIC, CAPTCHA_FONT_HEIGHT);
        // 设置字体。
        gd.setFont(font);

        // 画边框。
        gd.setColor(Color.BLACK);
        gd.drawRect(0, 0, CAPTCHA_WIDTH - 1, CAPTCHA_HEIGHT - 1);

        // 随机产生40条干扰线，使图象中的认证码不易被其它程序探测到。
        gd.setColor(Color.BLACK);
        for (int i = 0; i < LINE_COUNT; i++) {
            int x = random.nextInt(CAPTCHA_WIDTH);
            int y = random.nextInt(CAPTCHA_HEIGHT);
            int xl = random.nextInt(LINE_DY);
            int yl = random.nextInt(LINE_DY);
            gd.drawLine(x, y, x + xl, y + yl);
        }

        // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
        // StringBuffer randomCode = new StringBuffer();
        int red = 0, green = 0, blue = 0;

        // 随机产生codeCount数字的验证码。
        int sw = Math.floorDiv(CAPTCHA_WIDTH, captchaCode.length());
        for (int i = 0; i < captchaCode.length(); i++) {
            // 得到随机产生的验证码数字。
            String code = String.valueOf(captchaCode.charAt(i));
            // 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
            red = random.nextInt(MAX_RGB);
            green = random.nextInt(MAX_RGB);
            blue = random.nextInt(MAX_RGB);

            // 用随机产生的颜色将验证码绘制到图像中。
            gd.setColor(new Color(red, green, blue));
            gd.drawString(code, i * sw, CAPTCHA_CODE_Y);
        }
        // 将四位数字的验证码保存到Session中。
        redisTemplate.opsForValue().set(getCategory() + ":" + captchaKey, captchaCode, getExpire(), TimeUnit.SECONDS);
        // 将图像输出到输出流中。
        try (OutputStream ignored = os) {
            ImageIO.write(buffImg, "jpeg", os);
        }
    }

    /**
     * 验证码校验.
     * <p>
     * 将用户输入的 code 与 redis 中暂存的code进行比较.<br>
     */
    public boolean checkCaptcha(String captchaKey, String captchaCode) {
        if (captchaCode == null) {
            return false;
        }
        final String key = getCategory() + ":" + captchaKey;
        String captchaCodeInRedis = redisTemplate.opsForValue().get(key);
        removeCaptcha(key);
        return captchaCode.equalsIgnoreCase(captchaCodeInRedis);
    }

    @Override
    public void removeCaptcha(String key) {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.del(key.getBytes());
            return null;
        });

    }

}