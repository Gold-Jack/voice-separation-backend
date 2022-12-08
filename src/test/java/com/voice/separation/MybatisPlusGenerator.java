package com.voice.separation;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MybatisPlusGenerator {

    private static final DataSourceConfig DATA_SOURCE_CONFIG = new DataSourceConfig		// 配置数据源
            .Builder("jdbc:p6spy:mysql://localhost:3306/voice?serverTimezone=UTC&useUnicode=true&characterEncoding=GBK",
            "root", "admin")
            .schema("voice")
            .build();

    private static final String PROJECT_PATH = System.getProperty("user.dir");	// 获取项目路径
    private static final GlobalConfig GLOBAL_CONFIG = new GlobalConfig.Builder()		// 全局配置
            .outputDir(PROJECT_PATH + "/src/main/java")		// 代码生成路径
            .author("Gold_Jack")
            .disableOpenDir()	// 生成后不打开explorer窗口
            .dateType(DateType.ONLY_DATE)
            .enableSwagger()
            .build();

    private static final PackageConfig PACKAGE_CONFIG = new PackageConfig.Builder()		// 配置包文件
//            .moduleName("")			// 包名称
            .parent("com.voice.separation")			// 父目录名称
            .entity("pojo")					// 实体类文件夹名称...
            .mapper("mapper")
            .service("service")
            .controller("controller")
            .build();
    // 在pom中需配置springfox-swagger2和springfox-swagger-ui

    private static final StrategyConfig STRATEGY_CONFIG = new StrategyConfig.Builder()	// 策略配置
            // 数据库表名称, 可以多个同时生成
            .addInclude("user", "file")
            .entityBuilder()	// 通过entityBuilder()，可以开启TableFill，Lombok，NamingStrategy等
            .enableLombok()
            .enableTableFieldAnnotation()
//            .enableColumnConstant()
            .columnNaming(NamingStrategy.underline_to_camel)
            .naming(NamingStrategy.underline_to_camel).build();

    // !!! 最终项目路径
    // 代码生成路径/父目录名称/包名称
    //  - pojo
    // 		- Entity.java
    //	- service
    //  - controller
    // 	- mapper
    // 		- EntityMapper.java (extends BaseMapper<>)

    @Test
    public void mybatisGenerate() {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.global(GLOBAL_CONFIG)
                .strategy(STRATEGY_CONFIG)
                .packageInfo(PACKAGE_CONFIG);		// 自动生成器配置，依次传入四个配置文件
        generator.execute();
    }
}
