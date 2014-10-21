package CMU15826;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * This program is for 15-826 HW2 Q1
 * Use naive algorithm to compute correlation integral
 * @author Xiaoxiang Wu (xiaoxiaw)
 */
public class NaiveCorrelationIntegral {

	private class Point {
		double x;
		double y;

		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}

	final static int RADIUS_NUM = 21;
	double[] radius_list;
	int[] l1_count;
	int[] l2_count;
	String inFileName;
	List<Point> point_lists;

	public NaiveCorrelationIntegral(String inFileName) {
		radius_list = new double[RADIUS_NUM];
		l1_count = new int[RADIUS_NUM];
		l2_count = new int[RADIUS_NUM];
		this.inFileName = inFileName;
		this.point_lists = new ArrayList<Point>();
	}

	private void initialize() {
		for (int index = 0; index < RADIUS_NUM; ++index) {
			int exponent = index - 10;
			radius_list[index] = Math.pow(10, exponent);
			l1_count[index] = 0;
			l2_count[index] = 0;
		}
		String inFilePath = "../data/" + inFileName;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(inFilePath));
			String line = br.readLine();
			while (line != null) {
				String[] coordinate = line.trim().split(" ");
				Point p = new Point(Double.parseDouble(coordinate[0]), Double.parseDouble(coordinate[1]));
				point_lists.add(p);
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void computeAllPariDistance() {
		int point_num = this.point_lists.size();
		for (int index1 = 0; index1 < point_num; ++index1) {
			Point p1 = this.point_lists.get(index1);
			int index2 = index1 + 1;
			Point p2 = null;
			double l1_distance, l2_distance;
			while (index2 < point_num) {
				p2 = this.point_lists.get(index2);
				l1_distance = computeL1Distance(p1, p2);
				l2_distance = computeL2Distance(p1, p2);
				/* update count */
				for (int i = NaiveCorrelationIntegral.RADIUS_NUM - 1; i >= 0; --i) {
					if (l1_distance > this.radius_list[i]) {
						break;
					}
					++this.l1_count[i];
				}
				for (int i = NaiveCorrelationIntegral.RADIUS_NUM - 1; i >= 0; --i) {
					if (l2_distance > this.radius_list[i]) {
						break;
					}
					++this.l2_count[i];
				}
				++index2;
			}
		}
	}

	private void writeToFile() {
	
		int point_num = this.point_lists.size();
		PrintWriter pw1 = null;
		PrintWriter pw2 = null;
		try {
			pw1 = new PrintWriter("../output/" + inFileName + "_l1.result", "US-ASCII");
			pw2 = new PrintWriter("../output/" + inFileName + "_l2.result", "US-ASCII");
			for (int i = 0; i < NaiveCorrelationIntegral.RADIUS_NUM; ++i) {
				/* add back the self-pairs and mirror-pairs */
				pw1.write(Double.toString(this.radius_list[i]) + " " 
						+ Integer.toString(this.l1_count[i] * 2 + point_num) + "\n");
				pw2.write(Double.toString(this.radius_list[i]) + " " 
						+ Integer.toString(this.l2_count[i] * 2 + point_num) + "\n");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			pw1.close();
			pw2.close();
		}
	}

	private double computeL1Distance(Point p1, Point p2) {
		return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
	}

	private double computeL2Distance(Point p1, Point p2) {
		return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
	}

	public static void main(String[] args) {
		String inFileName = args[0].trim();
		NaiveCorrelationIntegral ncl = new NaiveCorrelationIntegral(inFileName);
		ncl.initialize();
		//		for (int i = 0; i < RADIUS_NUM; ++i) {
		//			System.out.println(ncl.radius_list[i]);
		//		}
		ncl.computeAllPariDistance();
		ncl.writeToFile();
	}

}
