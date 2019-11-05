package ch.rasc.eventbus.demo;


import com.sseevents.util.sseeventsutil.SseEvent;
import com.sseevents.util.sseeventsutil.SseEventBus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;

@Controller
public class SseController {

	private final SseEventBus eventBus;

	public SseController(SseEventBus eventBus) {
		this.eventBus = eventBus;
	}

	@GetMapping("/stream/{id}")
	public SseEmitter register(@PathVariable("id") String id, HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-store");
		return this.eventBus.createSseEmitter(id, 60000L, false, id);
	}

}
