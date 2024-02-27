package com.jingu.biz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author jingu
 */
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.jingu"})
public class BizApplication {

    public static void main(String[] args) {
        SpringApplication.run(BizApplication.class, args);
    }

}
