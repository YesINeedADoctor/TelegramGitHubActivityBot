package io.project.SpringTelegramGHActivityBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringTelegramGhActivityBotApplication {

    public static void main(String[] args) {
		try{
			SpringApplication.run(SpringTelegramGhActivityBotApplication.class, args);
		}
		catch(Throwable e){
			e.printStackTrace();
		}
    }
}