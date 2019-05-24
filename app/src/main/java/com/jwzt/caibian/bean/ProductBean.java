package com.jwzt.caibian.bean;

import java.io.Serializable;

/**
 * 作者：我的孩子叫好帅 on 2018/8/2 13:35
 * Q Q：779594494
 * 邮箱：17600949389@163.com
 */
public class ProductBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private int url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUrl() {
        return url;
    }

    public void setUrl(int url) {
        this.url = url;
    }

    public ProductBean(String name, int url) {
        super();
        this.name = name;
        this.url = url;
    }
}
