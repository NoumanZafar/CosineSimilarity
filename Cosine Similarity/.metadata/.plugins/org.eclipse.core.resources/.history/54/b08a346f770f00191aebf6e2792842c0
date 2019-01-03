package ie.gmit.sw;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class FileParser implements Callable<Object> {
	private String fileName;
	private BlockingQueue<Shingle> queue;
	private int charLength;

	public FileParser(BlockingQueue<Shingle> queue, String fileName, int charLength) {
		super();
		this.queue = queue;
		this.fileName = fileName;
		this.charLength = charLength;
	}

	public Object call() throws Exception {
		try {
			Reader reader = new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8);
			char[] chars = new char[charLength];
			int charRead = -1;
			while ((charRead = readChars(reader, chars)) != -1) {
				if (charRead != charLength) {
					int add = charRead % charLength;
					int run = charLength - add;
					for (int i = 0; i < run; i++) {
						chars[add + i] = ' ';
					}
				}
				String shingle = new String(chars).replaceAll("[^A-Za-z0-9 ]", "");
				queue.put(new Shingle(this.fileName, shingle));
			}
			queue.put(new Poison(fileName, "END-OF-FILE"));
			reader.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int readChars(Reader reader, char[] chars) throws IOException {
		return reader.read(chars);
	}
}