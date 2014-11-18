package CMU15826;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class PageRankPreprocess {
	
	// The list is the all the nodes that this node points to.
	// The first value of the list is the src node's importance value
	Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
	
	public void preProcess(String fileName) {
		try {
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#")) {  // ignore comments
					continue;
				}
				// should have two tokens per line
				StringTokenizer tokenizer = new StringTokenizer(line);
				String srcID = tokenizer.nextToken();
				String dstID = tokenizer.nextToken();
				updateMap(Integer.getInteger(srcID), Integer.getInteger(dstID));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private void updateMap(int srcID, int dstID) {
		List<Integer> dstLists = null;
		if (map.containsKey(srcID)) {
			dstLists = map.get(srcID);
		} else {
			dstLists = new ArrayList<Integer>();
			dstLists.add(1);  // initialize the node's importance value to 1
		}
		// add the dstID to the srcID's destination node lists
		dstLists.add(dstID);
		map.put(srcID, dstLists);
		// initialize the dstID into the map, in case it has no out-degree
		// in that case, we cannot get initial importance value for that node
		if (!map.containsKey(dstID)) {
			List<Integer> newList = new ArrayList<Integer>();
			newList.add(1);
			map.put(dstID, newList);
		}
	}
	
	public void outputResult() {
		for (Map.Entry<Integer, List<Integer>> entry: map.entrySet()) {
			System.out.print(entry.getKey());
			for (Integer value: entry.getValue()) {
				System.out.print(" " + value);
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		PageRankPreprocess pageRankPreprocess = new PageRankPreprocess();
		pageRankPreprocess.preProcess(args[0]);
		pageRankPreprocess.outputResult();
	}

}
