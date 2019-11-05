package ch.rasc.eventbus.demo;

import com.sseevents.util.sseeventsutil.config.EnableSseEventBus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;



@SpringBootApplication
@EnableScheduling
@EnableSseEventBus
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
