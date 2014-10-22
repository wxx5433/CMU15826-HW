package CMU15826;

import java.util.Stack;

/**
 * This program is for 15-826 HW2
 * It implements edit distance algorithm.  
 * @author Xiaoxiang Wu(xiaoxiaw)
 */
public class StringEditingDistance {

	String word1;
	String word2;
	int[][] dp;
	int[][][] backtrace;
	int insertion_cost = 0;
	int substitution_cost = 0;
	int deletion_cost = 0;

	public StringEditingDistance(String word1, String word2) {
		this.word1 = word1;
		this.word2 = word2;
		dp = new int[this.word1.length() + 1][this.word2.length() + 1];
		backtrace = new int[this.word1.length() + 1][this.word2.length() + 1][2];
	}

	/* Initialize dp and backtrace */
	public void initialize() {
		for (int i = 0; i <= word1.length(); ++i) {
			dp[i][0] = i * deletion_cost;
		}
		for (int j = 0; j <= word2.length(); ++j) {
			dp[0][j] = j * insertion_cost;
		}
		for (int i = 0; i <= word1.length(); ++i) {
			for (int j = 0; j <= word2.length(); ++j) {
				backtrace[i][j][0] = 0;
				backtrace[i][j][1] = 0;
			}
		}
	}

	/**
	 * compute edit distance
	 * @return edit distance between word1 and word2
	 */
	public int computeDistance() {
		for (int i = 0; i < word1.length(); ++i) {
			for (int j = 0; j < word2.length(); ++j) {
				if (word1.charAt(i) == word2.charAt(j)) {
					dp [i + 1][j + 1] = dp[i][j];
					backtrace[i + 1][j + 1][0] = i;
					backtrace[i + 1][j + 1][1] = j;
				} else {
					computeDistanceHelper(i, j);
				}
			}
		}
		return dp[word1.length()][word2.length()];
	}
	
	/**
	 * Update dp, and at the same time update backtrace to save the path info.
	 * @param i row index of the grid to update
	 * @param j column index of the grid to update
	 */
	private void computeDistanceHelper(int i, int j) {
		int d_cost = dp[i + 1][j] + deletion_cost; 
		int i_cost = dp[i][j + 1] + insertion_cost;
		int s_cost = dp[i][j] + substitution_cost;
		/* 0 represents deletion, 1 represents insertion, 2 represents substitution */
		int flag = (d_cost < i_cost)? 0 : 1;
		if (flag == 0 && s_cost < d_cost) {
			flag = 2;			
		} else if (flag == 1 && s_cost < i_cost) {
			flag = 2;
		}
		if (flag == 0) {
			dp[i + 1][j + 1] = d_cost;
			backtrace[i + 1][j + 1][0] = i + 1;
			backtrace[i + 1][j + 1][1] = j;
		} else if (flag == 1) {
			dp[i + 1][j + 1] = i_cost;
			backtrace[i + 1][j + 1][0] = i;
			backtrace[i + 1][j + 1][1] = j + 1;
		} else {
			dp[i + 1][j + 1] = s_cost;
			backtrace[i + 1][j + 1][0] = i;
			backtrace[i + 1][j + 1][1] = j; 
		}
	}

	/**
	 * Output the final result. 
	 * @param distance edit distance between word1 and word2
	 */
	public void outputResult(int distance) {
		System.out.println("Insertion Cost: " + this.insertion_cost);
		System.out.println("Substitution Cost: " + this.substitution_cost);
		System.out.println("Deletion Cost: " + this.deletion_cost);
		System.out.println("String1: " + this.word1 + ", " + "String2: " + this.word2);
		System.out.println("Matrix:");
		for (int i = 0; i < dp.length; ++i) {
			System.out.print(dp[i][0]);
			for (int j = 1; j < dp[0].length; ++j) {
				System.out.print("\t" + dp[i][j]);
			}
			System.out.println();
		}
		System.out.println("String Editing Distance: " + distance);
		outputPath();
	}

	/**
	 * Output the path
	 */
	private void outputPath() {
		System.out.println("String editing path:");
		Stack<String> stack = new Stack<String>();
		int	i = word1.length(), j = word2.length();
		while(true) {
			if (i == 0 && j == 0) {
				break;
			}
			int prev_i = backtrace[i][j][0];
			int prev_j = backtrace[i][j][1];
			if (dp[i][j] == dp[prev_i][prev_j]) {
				stack.push("_");
			} else if (i - prev_i == 1 && j - prev_j == 1) {  // substitution
				stack.push("S(" + word1.charAt(i - 1) + ", " + word2.charAt(j - 1) + ")");
			} else if (i - prev_i == 1 && j - prev_j == 0) {  // deletion
				stack.push("D(" + word1.charAt(i - 1) + ")");
			} else {  // insertion
				stack.push("I(" + word2.charAt(j - 1) + ")");
			}
			i = prev_i;
			j = prev_j;
		} 
		boolean formatFlag = false;
		while (!stack.isEmpty()) {
			if (formatFlag) {
				System.out.print(", ");
			} 
			formatFlag = true;
			System.out.print(stack.pop());
		}
		
		System.out.println();
	}

	public void setInsertion_cost(int insertion_cost) {
		this.insertion_cost = insertion_cost;
	}

	public void setSubstitution_cost(int substitution_cost) {
		this.substitution_cost = substitution_cost;
	}

	public void setDeletion_cost(int deletion_cost) {
		this.deletion_cost = deletion_cost;
	}

	public static void main(String[] args) {
		StringEditingDistance sed = new StringEditingDistance(args[3], args[4]);
		sed.setInsertion_cost(Integer.parseInt(args[0]));
		sed.setSubstitution_cost(Integer.parseInt(args[1]));
		sed.setDeletion_cost(Integer.parseInt(args[2]));
		sed.initialize();
		sed.outputResult(sed.computeDistance());
	}
}
