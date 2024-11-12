package com.spd;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 *
 * @author spd
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class SPDApplication
{
    public static void main(String[] args)
    {
//         System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication app = new SpringApplication(SPDApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
//        SpringApplication.run(SPDApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  SPD启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "                    .___ \n" +
                "   ____________   __| _/ \n" +
                "  /  ___/\\____ \\ / __ | \n" +
                "  \\___ \\ |  |_> > /_/ | \n" +
                " /____  >|   __/\\____ |  \n" +
                "      \\/ |__|        \\/  ");
    }
}
