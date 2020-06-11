package com.uncleshaw;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description Excel工具类
 * @Author shaw
 * @Date 2020/6/11 18:54
 */
public class ExcelUtils {
    /**
     * @Description: 对比两个文件名（包括后缀）是否相同
     * @param: [source, target]
     * @return: boolean
     * @auther: shaw
     * @date: 2020/6/11 19:12
     */
    public static boolean compareFileName(File source, File target) {
        return source.getName().equals(target.getName());
    }
    
    /**
     * @Description: 判断多个文件的文件名是否和目标文件名一致
     * @param: [source, target]
     * @return: java.lang.String[] 不一致文件名的集合
     * @auther: shaw
     * @date: 2020/6/11 20:31
     *
     */
    public static String[] compareFileName(File[] source, File[] target) {
        Objects.requireNonNull(source, "原文件夹中没有文件");
        Objects.requireNonNull(target, "目标文件夹中没有文件");
        List<String> sourceList = new ArrayList<>();
        List<String> targetList = new ArrayList<>();
        for (File file : source) {
            sourceList.add(file.getName());
        }
        
        for (File file : target) {
            targetList.add(file.getName());
        }
        sourceList.removeAll(targetList);
        return sourceList.toArray(new String[sourceList.size()]);
    }
}
