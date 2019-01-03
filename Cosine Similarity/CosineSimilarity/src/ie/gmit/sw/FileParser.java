package ie.gmit.sw;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

/**
 * This class implements the Callable interface and call the call() method which
 * returns null value. This class reads the file in chunks (Number of
 * characters) and add them to the BlockingQueue
 * 
 * @author Nouman Zafar
 * @version 1.0
 * @since 1.8
 * @see Callable
 */
public class FileParser implements Callable<Object> {
	private String fileName;
	private BlockingQueue<Shingle> queue;
	private int k_mer_size;

	/**
	 * Constructor takes the BlockingQueue,File name and k-mer size.
	 * 
	 * @param queue      - BlockingQueue to add the k-mer
	 * @param fileName   - Name (Path) of the file to read
	 * @param k_mer_size - Number of the characters needs to read from file at a
	 *                   time. This may effect the results and the time of execution
	 *                   of the program.
	 */
	public FileParser(BlockingQueue<Shingle> queue, String fileName, int k_mer_size) {
		super();
		this.queue = queue;
		this.fileName = fileName;
		this.k_mer_size = k_mer_size;
	}

	public Object call() throws Exception {
		try {
			Reader reader = new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8);
			char[] chars = new char[k_mer_size];
			int charRead = -1;

			/**
			 * Read till the last character of the file.
			 */
			while ((charRead = readCharacters(reader, chars)) != -1) {
				/**
				 * If last buffer size is bigger than the characters left in the file then add
				 * empty spaces to fill the buffer.
				 */
				if (charRead != k_mer_size) {
					int add = charRead % k_mer_size;
					int run = k_mer_size - add;
					for (int i = 0; i < run; i++) {
						chars[add + i] = ' ';
					}
				}
				/**
				 * Convert char[] to string and remove any extra characters.
				 */
				String shingle = new String(chars).replaceAll("[^A-Za-z0-9 ]", "");
				/**
				 * Add shingles (New instance of the Shingle class which takes the file name and
				 * the shingle generated above.) to the BlockingQueue.
				 */
				queue.put(new Shingle(this.fileName, shingle));
			}
			/**
			 * Poison the BlockingQueue after reading the whole file. This will help in
			 * finding out the end of File or Stream.
			 */
			queue.put(new Poison(fileName, "END-OF-FILE"));
			/**
			 * Close the Reader.
			 */
			reader.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Reads the number of characters and return the buffer (Number of characters
	 * being read) size.
	 * 
	 * @param reader - Used to read the file it gives the advantage of using the
	 *               Encoding characters e.g UTF-8
	 * @param chars  - Characters array
	 * @return - Number of characters being read
	 * @throws IOException - Input/Output Exception.
	 */
	public int readCharacters(Reader reader, char[] chars) throws IOException {
		return reader.read(chars);
	}
}