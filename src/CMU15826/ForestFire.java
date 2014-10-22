package CMU15826;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

/**
 * This program is for CMU 15-826 HW2.
 * It simulates the forest fire process to verify the power law
 * @author Xiaoxiang Wu (xiaoxiaw)
 */
public class ForestFire {
	private class Grid {
		int i;
		int j;
		boolean hasTree;

		public Grid(int i, int j, boolean hasTree) {
			this.i = i;
			this.j = j;
			this.hasTree = hasTree;
		}
		@Override
		public int hashCode() {
			return (Integer.toString(i) + "," + Integer.toString(j)).hashCode();
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj instanceof Grid) {
				Grid that = (Grid)obj;
				if (this.i == that.i && this.j == that.j) {
					return true;
				}
			}
			return false;
		}
		public boolean hasTree() {
			return hasTree;
		}
		public void setHasTree(boolean hasTree) {
			this.hasTree = hasTree;
		}
	}

	int sideLength;   /* side length of the forest */
	Grid[][] forest;
	boolean[][] visited;  /* flag to find clusters */
	int burningTime;

	public ForestFire(int sideLength) {
		this.sideLength = sideLength;
		forest = new Grid[sideLength][sideLength];
		visited = new boolean[sideLength][sideLength];
	}

	/**
	 * Initialize the forest 
	 * We randomly choose (sideLength * sideLength * p) grids to 
	 * be occupied by trees. 
	 * @param d forest density
	 */
	public void initializeForest(double d) {
		for (int i = 0; i < sideLength; ++i) {
			for (int j = 0; j < sideLength; ++j) {
				forest[i][j] = new Grid(i, j, false);
			}
		}
		int treesNum = (int)(sideLength * sideLength * d);
		//System.out.println(treesNum);
		/* randomly choose treesNum grid to be occupied by tree */
		Random random = new Random();
		int treeCount = 0;
		while (treeCount < treesNum) {
			int i = random.nextInt(sideLength);
			int j = random.nextInt(sideLength);
			if (hasTree(i, j)) {  /* already occupied by a tree */
				continue;
			}
			forest[i][j].setHasTree(true);
			++treeCount;
		}
	}

	/**
	 * Randomly choose a lightening point and start burning the forest.
	 * The fire will propagate to its four neighbor.
	 * The fire will stop until the fire cannot continue to propagate
	 */
	public void burn(){
		/* Randomly choose a tree to burn */
		int start_i, start_j;
		do {   /* find until the grid has tree */
			Random random = new Random();
			start_i = random.nextInt(sideLength);
			start_j = random.nextInt(sideLength);
		} while (!this.forest[start_i][start_j].hasTree());
		//System.out.println("lightening point: " + Integer.toString(start_i) + ", " + Integer.toString(start_j)) ;
		propagate(start_i, start_j);
	}
	
	private int propagate(int start_i, int start_j) {
		/* Initialize the burning set and time */
		Set<Grid> burningSet = new HashSet<Grid>();
		Set<Grid> propagationSet = null;
		this.forest[start_i][start_j].setHasTree(false);
		this.visited[start_i][start_j] = true;
		burningSet.add(this.forest[start_i][start_j]);
		burningTime = 0;
		int size = 1;

		/* keep propagating until nowhere to propagate */
		do {
			propagationSet = new HashSet<Grid>();
			++burningTime;
			/* start to propagate */
			for (Grid grid: burningSet) {  
				for (Grid neighbor: getNeighbors(grid)) {
					neighbor.setHasTree(false);  // burnt the tree
					propagationSet.add(neighbor);
					++size;
				}
			}
			/* add the propagation set to the burning set */
			burningSet = propagationSet;   
		} while (!propagationSet.isEmpty());
		return size;
	}

	public static void main(String[] args) {
		ForestFire forestFire = new ForestFire(50);
		/* args1: density args2: mode(print tree:0, find cluster: 1) */
		if (args.length == 2) {  /* This is especially for question 3 */
			forestFire.initializeForest(Double.parseDouble(args[0]));
			int mode = Integer.parseInt(args[1]);
			if (mode == 0) {
				forestFire.printForest();
			} else {  // mode 1: find clusters 
				Map<Integer, Integer> map = forestFire.findClusters();
				for (Map.Entry<Integer, Integer> entry: map.entrySet()) {
					System.out.println(entry.getKey() + " " + entry.getValue());
				}
			}
			return;
		}
		double[] densities = {0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5,
				0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9}; 
		int len = densities.length;
		int[] maxBurningTime = new int[len];
		for (int i = 0; i < len; ++i) {
			maxBurningTime[i] = 0;
		}
		for (int index = 0; index < len; ++index) {
			double p = densities[index];
			System.out.println("Density: " + Double.toString(p));
			/* run 5 times for each p */
			for (int r = 0; r < 5; ++r) {
				forestFire.initializeForest(p);
				forestFire.burn();
				if (forestFire.burningTime > maxBurningTime[index]) {
					maxBurningTime[index] = forestFire.burningTime;
				}
				System.out.println("Burning Time: " + forestFire.burningTime);
			}
		}
		forestFire.writeMaxBurningTimeToFile(maxBurningTime);
	}

	/**
	 * check if a grid is occupied by tree
	 * @param i row index
	 * @param j column index
	 * @return true - if occupied by tree, false otherwise
	 */
	private boolean hasTree(int i, int j) {
		return this.forest[i][j].hasTree();
	}

	/**
	 * Print the whole forest.
	 * If a gird is occupied by a tree, print it as 0.
	 * Print 1 otherwise
	 */
	private void printForest(){
		for (int i = 0; i < sideLength; ++i) {
			for (int j = 0; j < sideLength; ++j) {
				if (this.forest[i][j].hasTree()) {
					System.out.print("1 ");
				} else {
					System.out.print("0 ");
				}
			}
			System.out.println();
		}
	}

	/**
	 * Check if the index is valid
	 * @param i row index
	 * @param j column index
	 * @return true if the index is valid, false otherwise
	 */
	private boolean checkIndex(int i, int j) {
		if (i < 0 || i >= sideLength || j < 0 || j >= sideLength) {
			return false;
		}
		return true;
	}

	/**
	 * Find grids that the fire on this grid will propagate to.
	 * @param grid burning grid 
	 * @return grids that the fire on this grid will propagate to
	 */
	private Iterable<Grid> getNeighbors(Grid grid) {
		List<Grid> neighbors = new ArrayList<Grid>();
		int[] i_direction = {-1, 1, 0, 0};   // up, down, left, right
		int[] j_direction = {0, 0, -1, 1};
		for (int k = 0; k < 4; ++k) {
			int neighbor_i = grid.i + i_direction[k];
			int neighbor_j = grid.j + j_direction[k];
			if (checkIndex(neighbor_i, neighbor_j)  
					&& this.forest[neighbor_i][neighbor_j].hasTree()) {
				neighbors.add(this.forest[neighbor_i][neighbor_j]);
				this.visited[neighbor_i][neighbor_j] = true;
			}
		}
		return neighbors;
	}

	/**
	 * Write the maximum burning time for each p to a file
	 * so that we can plot it later.
	 * @param maxBurningTime max burning time for each p
	 */
	private void writeMaxBurningTimeToFile(int[] maxBurningTime) {
		double[] densities = {0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5,
				0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9}; 
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("Q5_2.dat", "US-ASCII");
			for (int i = 1; i < densities.length; ++i) {
				writer.write(Double.toString(densities[i]) + "\t" 
						+ Integer.toString(maxBurningTime[i]) + "\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		writer.close();
	}
	
	private Map<Integer, Integer> findClusters() {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0; i < this.sideLength; ++i) {
			for (int j = 0; j < this.sideLength; ++j) {
				if (visited[i][j] || !this.forest[i][j].hasTree()) {
					visited[i][j] = true;
					continue;
				}
				visited[i][j] = true;
				int size = propagate(i, j);
				if (map.containsKey(size)) {
					map.put(size, map.get(size) + 1);
				} else {
					map.put(size, 1);
				}
			}
		}
		return new TreeMap<Integer, Integer>(map);
	}

}
