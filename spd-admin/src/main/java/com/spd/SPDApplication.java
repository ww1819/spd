package com.spd;

import java.io.File;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 *
 * @author spd
 */
@SpringBootApplication(scanBasePackages = {"com.spd", "com.sb"}, exclude = { DataSourceAutoConfiguration.class })
public class SPDApplication
{
    public static void main(String[] args)
    {
//         System.setProperty("spring.devtools.restart.enabled", "false");
        System.setProperty("spring.devtools.restart.enabled", "false");
        locateLocalSpringConfig();
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

    /**
     * Cursor/VS Code 增量编译经常只输出 .class、不拷 resources。
     * 从源码目录补一份配置，避免落到 default profile 后缺少 token.header 等项。
     */
    private static void locateLocalSpringConfig()
    {
        if (System.getProperty("spring.config.additional-location") != null)
        {
            return;
        }
        String userDir = System.getProperty("user.dir", "");
        String[] candidates = {
            userDir + File.separator + "spd-admin" + File.separator + "src" + File.separator + "main" + File.separator + "resources",
            userDir + File.separator + "src" + File.separator + "main" + File.separator + "resources"
        };
        for (String path : candidates)
        {
            File dir = new File(path);
            if (new File(dir, "application.yml").isFile())
            {
                System.setProperty("spring.config.additional-location",
                    "optional:file:" + dir.getAbsolutePath().replace('\\', '/') + "/");
                return;
            }
        }
    }
}
