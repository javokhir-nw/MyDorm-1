package javier.com.mydorm1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "javier.com.mydorm1",
        "javier.com.mydorm1.auth",
        "javier.com.mydorm1.telegram",
})
public class MyDorm1Application {

    public static void main(String[] args) {
        SpringApplication.run(MyDorm1Application.class, args);
    }

}
