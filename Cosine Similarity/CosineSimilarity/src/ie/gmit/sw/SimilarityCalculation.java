package ie.gmit.sw;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class takes the two maps and on the bases of that map's keys and values
 * the <a>Cosine Similarity</a> is calculated.
 * 
 * @author Nouman Zafar
 * @version 1.0
 *
 */
public class SimilarityCalculation implements Callable<Double> {
	private ConcurrentHashMap<String, Integer> queryMap;
	private ConcurrentHashMap<String, Integer> subjectMap;

	/**
	 * 
	 * @param queryMap   Query ConcurrentHashMap
	 * @param subjectMap Subject ConcurrentHashMap
	 */
	public SimilarityCalculation(ConcurrentHashMap<String, Integer> queryMap,
			ConcurrentHashMap<String, Integer> subjectMap) {
		super();
		this.queryMap = queryMap;
		this.subjectMap = subjectMap;
	}

	public Double call() throws Exception {
		Thread.sleep(10);
		/**
		 * Unique shingles are stored in the Set from both the Maps provided in the
		 * constructor.
		 */
		Set<String> unique = new HashSet<>(queryMap.keySet());
		unique.retainAll(subjectMap.keySet());

		long dotProduct = 0;
		double queryMagnitude = 0, subjectMagnitude = 0;

		/**
		 * Loop over set of unique shingles and calculate the Dot Product dotProduct =
		 * (Sum of shingle frequency in query map) X (Sum of shingle frequency in subject map)
		 */
		for (String word : unique) {
			dotProduct += queryMap.get(word) * subjectMap.get(word);
		}

		/**
		 * Loop over all the Values from Query map and calculate the Magnitude of Query
		 * map. magnitude = Sum of number of keys with the power of 2
		 */
		for (String i : queryMap.keySet()) {
			queryMagnitude += Math.pow(queryMap.get(i), 2);
		}

		/**
		 * Loop over all the Values from Subject map and calculate the Magnitude of
		 * Subject map. magnitude = Sum of number of keys with the power of 2
		 */
		for (String i : subjectMap.keySet()) {
			subjectMagnitude += Math.pow(subjectMap.get(i), 2);
		}

		/**
		 * Calculate the Cosine Similarity using the formula. similarity = dotProduct /
		 * (Square root of query magnitude) X (Square root of subject magnitude)
		 * 
		 */
		return (dotProduct / (Math.sqrt(queryMagnitude) * Math.sqrt(subjectMagnitude))) * 100;
	}
}
