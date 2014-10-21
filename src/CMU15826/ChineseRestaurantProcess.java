package CMU15826;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * This program is for CMU 15-826 HW2
 * It aims to verify the probability distribution of the size of tables
 * exhibits a power law tail.
 * @author Xiaoxiang Wu (xiaoxiaw)
 *
 */
public class ChineseRestaurantProcess {
	int t;
	double p;
	List<Integer> tables;
	int peopleNum;
	
	public ChineseRestaurantProcess(int t, double p) {
		this.t = t;
		this.p = p;
	}
	
    /**
     *  Initially there is one table and one customer 
     */
	private void initializeTable() {
		tables = new ArrayList<Integer>();
		tables.add(1);  
		peopleNum = 1;
	}
	
	/**
	 * Simulate the Chinese restaurant process
	 * 1. we have probability p to add a new table, 
	 *    so we generate a random number between 0 and 1.
	 *    The probability to generate a result small than p is exactly p. 
	 *    So if the result < p, we add a new table.
	 * 2. We do not need to actually care about the sum of adding to 
	 *    existing table is (1-p).
	 *    The only thing we need to care about is the probability to 
	 *    join a table is proportional to k (number of people on this table).
	 *    So we generate a random integer between [0, current peopleNum) called probability. 
	 *    Then we start adding each tables' peopleCount until it exceeds probability.
	 */
	private void simulate() {
		Random random = new Random();
		for (int time = 1; time <= t; ++time) {
			if (Math.random() < p) {  /* add a new table */
				addTable();
				continue;
			}
			/* join an existing table */
			int probability = random.nextInt(this.peopleNum);
			joinExistingTable(probability);
		}
	}
	
	/**
	 * add a new table
	 */
	private void addTable() {
		this.tables.add(1);
		++this.peopleNum;
	}
	
	/**
	 * Join an existing table
	 * @param probability 
	 */
	private void joinExistingTable(int probability) {
		int sum = 0;
		int table_index = 0;
		int table_num = this.tables.size();
		/* find the table to join */
		while (table_index < table_num) {
			sum += this.tables.get(table_index);
			if (sum > probability) {
				break;
			}
			++table_index;
		}
		tables.set(table_index, tables.get(table_index) + 1);	
		++this.peopleNum;
	}
	
	private Map<Integer, Integer> generateCountSizeMap() {
		Map<Integer, Integer> map = new HashMap<Integer,Integer>();
		for (Integer peopleCount: tables) {
			if (map.containsKey(peopleCount)) {
				map.put(peopleCount, map.get(peopleCount) + 1);
			} else {
				map.put(peopleCount, 1);
			}
		}
		// use tree map to keep order
		return new TreeMap<Integer, Integer>(map);
	}
	
	public static void main(String[] args) {
		ChineseRestaurantProcess crp = new ChineseRestaurantProcess(Integer.parseInt(args[0]), 
				Double.parseDouble(args[1]));
		crp.initializeTable();
		crp.simulate();
		Map<Integer, Integer> map = crp.generateCountSizeMap();
		for (Map.Entry<Integer, Integer> entry: map.entrySet()) {
			System.out.println(Integer.toString(entry.getKey()) + "\t" 
					+ Integer.toString(entry.getValue()));
		}
	}

}
