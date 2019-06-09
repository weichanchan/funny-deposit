package com.funny.admin.agent.entity;

import java.io.Serializable;
import java.util.Date;


/**
 * 商品信息表
 *
 * @author weicc
 * @email
 * @date 2019-02-27 09:05:27
 */
public class WareFuluInfoEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 卡密类型
     */
    public static final int TYPE_IS_CARD = 1;
    /**
     * 非卡密类型
     */
    public static final int TYPE_NOT_CARD = 2;
    /**
     * 旧福禄充值平台类型
     */
    public static final int TYPE_OLD_RECHARGE_CHANNEL = 1;
    /**
     * 新福禄充值平台类型
     */
    public static final int TYPE_NEW_RECHARGE_CHANNEL = 2;
    /**
     * A平台充值类型
     */
    public static final int TYPE_A_CHANNEL = 3;

    /**
     * 设置：主键id
     */
    private Long id;
    /**
     * 设置：商品编号（对应有赞outer_sku_id）
     */
    private String outerSkuId;
    /**
     * 设置：商品名
     */
    private String wareName;
    /**
     * 设置：数量
     */
    private Integer num;
    /**
     * 设置：福禄商品编号
     */
    private String productId;
    /**
     * 设置：福禄商品批量编号
     */
    private String productHugeId;
    /**
     * 设置：充值账号提取标识
     */
    private String mark;
    /**
     * 设置：是否区分批量渠道，1：不区分，2：区分
     */
    private Integer type;
    /**
     * 充值渠道，1：旧福禄平台，2：新福禄平台，3：A平台
     */
    private Integer rechargeChannel;
    /**
     * 设置：创建时间
     */
    private Date createTime;

    /**
     * 设置：主键id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取：主键id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置：商品编号（对应有赞outer_sku_id）
     */
    public void setOuterSkuId(String outerSkuId) {
        this.outerSkuId = outerSkuId;
    }

    /**
     * 获取：商品编号（对应有赞outer_sku_id）
     */
    public String getOuterSkuId() {
        return outerSkuId;
    }

    /**
     * 设置：商品名
     */
    public void setWareName(String wareName) {
        this.wareName = wareName;
    }

    /**
     * 获取：商品名
     */
    public String getWareName() {
        return wareName;
    }

    /**
     * 设置：数量
     */
    public void setNum(Integer num) {
        this.num = num;
    }

    /**
     * 获取：数量
     */
    public Integer getNum() {
        return num;
    }

    /**
     * 设置：福禄商品编号
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * 获取：福禄商品编号
     */
    public String getProductId() {
        return productId;
    }

    /**
     * 设置：充值账号提取标识
     */
    public void setMark(String mark) {
        this.mark = mark;
    }

    /**
     * 获取：充值账号提取标识
     */
    public String getMark() {
        return mark;
    }

    /**
     * 设置：创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取：创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    public String getProductHugeId() {
        return productHugeId;
    }

    public void setProductHugeId(String productHugeId) {
        this.productHugeId = productHugeId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getRechargeChannel() {
        return rechargeChannel;
    }

    public void setRechargeChannel(Integer rechargeChannel) {
        this.rechargeChannel = rechargeChannel;
    }
}
