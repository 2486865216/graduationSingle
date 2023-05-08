package com.example.graduationprojectsingle;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
@MapperScan(basePackages = {"com.example.graduationprojectsingle.mapper"})
public class GraduationProjectSingleApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraduationProjectSingleApplication.class, args);
        log.info("""

				  ___ _   _  ___ ___ ___  ___ ___\s
				 / __| | | |/ __/ __/ _ \\/ __/ __|
				 \\__ \\ |_| | (_| (_|  __/\\__ \\__ \\
				 |___/\\__,_|\\___\\___\\___||___/___/\
				""");
    }

}
