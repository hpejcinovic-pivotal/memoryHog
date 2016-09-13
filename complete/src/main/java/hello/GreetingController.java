package hello;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();
	private static ArrayList<Long[]> al = new ArrayList<Long[]>();

	@RequestMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}

	@RequestMapping("/memoryHog")
	public String memoryHog() {
		String after = "";
		try {
			System.out.println("before:" + humanReadableByteCount());
			final Long[] ar = new Long[Integer.MAX_VALUE/100];
			al.add(ar);
			
			after = humanReadableByteCount();
			System.out.println("after:" + after);
		} catch (java.lang.OutOfMemoryError e) {
			after ="We have run out of memory, party over. Free memory:" + after;
			System.out.println(e);
			throw new OutOfMemoryError();
		}
		return after;
	}

	public static String humanReadableByteCount() {

		long bytes = Runtime.getRuntime().freeMemory();
		int unit = 1000;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = "" + ("KMGTPE").charAt(exp - 1);
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre) + " bytes free!";
	}

}
