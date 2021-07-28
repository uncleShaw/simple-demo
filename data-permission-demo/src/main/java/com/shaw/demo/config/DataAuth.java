package com.shaw.demo.config;

import java.lang.annotation.*;

/**
 * @author yichen
 * @Description: 需要注解的方法
 * @date 2021/7/21 15:32
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DataAuth {

}
