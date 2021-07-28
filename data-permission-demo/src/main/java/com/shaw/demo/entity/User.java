package com.shaw.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 *
 * @author wangchuang
 * @since 2021-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("xtgl_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 登录名
     */
    private String loginName;

    /**
     * 密码
     */
    private String password;

    /**
     * 所属单位id
     */
    private Integer orgId;

    /**
     * 所属部门id
     */
    private Integer deptId;

    /**
     * 所属角色id
     */
    private Integer roleId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 盐
     */
    private Integer salt;

    /**
     * 查阅范围，1个人，2部门，3法院
     */
    private Integer viewScope;

    /**
     * 所属单位性质，1法院，2纪检组
     */
    private Integer type;

    /**
     * 状态，1正常使用，2被删除，3被停用
     */
    private Integer state;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;


}
