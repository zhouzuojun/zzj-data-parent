package com.zzj.data.config;

import org.mybatis.generator.api.ShellRunner;

public class MybatisPlusConfig {

    public static void main(String[] args) {
        String a=System.getProperty("use.dir");
        args=new String[]{"-configfile","E:\\study-space\\zzj-data-parent\\zzj-data-common\\src\\main\\resources\\mybatis-generator.xml","-overwrite"};
        ShellRunner.main(args);
    }
}
