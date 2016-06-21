package RoutesMining;

import java.util.List;

import Model.StayPoint;

/**
 * This class implements the Dynamic Time Warping algorithm
 * given two sequences
 * <pre>
 *   X = x1, x2,..., xi,..., xn
 *   Y = y1, y2,..., yj,..., ym
 *  </pre>
 * 
 * @author              Cheol-Woo Jung (cjung@gatech.edu)
 * @version     1.0     
 */
public class DTW {
	//protected float[] seq1;
	//protected float[] seq2;
	protected List<StayPoint> sq1;
	protected List<StayPoint> sq2;
	protected int[][] warpingPath;

	protected int n;
	protected int m;
	protected int K;

	protected double warpingDistance;

	/**
	 * Constructor
	 *
	 * @param query         
	 * @param templete     
	 */
	public DTW(List<StayPoint> sample, List<StayPoint> templete) {
		sq1 = sample;
		sq2 = templete;

		n = sq1.size();       
		m = sq2.size();
		K = 1;

		warpingPath = new int[n + m][2];        // max(n, m) <= K < n + m
		warpingDistance = 0.0;

		this.compute();
	}

	public void compute() {
		double accumulatedDistance = 0.0;

		double[][] d = new double[n][m];        // local distances
		double[][] D = new double[n][m];        // global distances

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				d[i][j] = distanceBetween(sq1.get(i), sq2.get(j));
			}
		}
		D[0][0] = d[0][0];
		for (int i = 1; i < n; i++) {
			D[i][0] = d[i][0] + D[i - 1][0];
		}
		for (int j = 1; j < m; j++) {
			D[0][j] = d[0][j] + D[0][j - 1];
		}

		for (int i = 1; i < n; i++) {
			for (int j = 1; j < m; j++) {
				accumulatedDistance = Math.min(Math.min(D[i-1][j], D[i-1][j-1]), D[i][j-1]);
				accumulatedDistance += d[i][j];
				D[i][j] = accumulatedDistance;
			}
		}
		accumulatedDistance = D[n - 1][m - 1];
		int i = n - 1;
		int j = m - 1;
		int minIndex = 1;

		warpingPath[K - 1][0] = i;
		warpingPath[K - 1][1] = j;

		while ((i + j) != 0) {
			if (i == 0) {
				j -= 1;
			} else if (j == 0) {
				i -= 1;
			} else {        // i != 0 && j != 0
				double[] array = { D[i - 1][j], D[i][j - 1], D[i - 1][j - 1] };
				minIndex = this.getIndexOfMinimum(array);
				if (minIndex == 0) {
					i -= 1;
				} else if (minIndex == 1) {
					j -= 1;
				} else if (minIndex == 2) {
					i -= 1;
					j -= 1;
				}
			} // end else
			K++;
			warpingPath[K - 1][0] = i;
			warpingPath[K - 1][1] = j;
		} // end while
		warpingDistance = accumulatedDistance / K;
 
		this.reversePath(warpingPath);
	}

	/**
	 * Changes the order of the warping path (increasing order)
	 *
	 * @param path  the warping path in reverse order
	 */
	protected void reversePath(int[][] path) {
		int[][] newPath = new int[K][2];
		for (int i = 0; i < K; i++) {
			for (int j = 0; j < 2; j++) {
				newPath[i][j] = path[K - i - 1][j];
			}
		}
		warpingPath = newPath;
	}
	/**
	 * Returns the warping distance
	 *
	 * @return
	 */
	public double getDistance() {
		return warpingDistance;
	}

	/**
	 * Computes a distance between two points
	 *
	 * @param p1    the point 1
	 * @param p2    the point 2
	 * @return              the distance between two points
	 */
	protected double distanceBetween(StayPoint p1, StayPoint p2) {
		double lon1 = p1.getLon();
		double lat1 = p1.getLat();
		double lon2 = p2.getLon();
		double lat2 = p2.getLat();
		return distanceInGlobal(lon1,lat1,lon2,lat2);
	}
	/**
	 * 计算两位置之间的距离，根据球面坐标长度公式计算(单位：米)
	 * 注意，这个计算很耗时间,另外,这个计算把经纬度的100万倍还原了!
	 */
	public static double distanceInGlobal(double lon1, double lat1, double lon2, double lat2){
		double x1 = lon1;
		double y1 = lat1;
		double x2 = lon2;
		double y2 = lat2;

		double L = (3.1415926*6370/180)*Math.sqrt((Math.abs((x1)-(x2)))*(Math.abs((x1)-(x2)))*(Math.sin((90-(y1))*(3.1415926/180)))*(Math.sin((90-(y1))*(3.1415926/180)))+(Math.abs((y1)-(y2)))*(Math.abs((y1)-(y2))));
		return L * 1000;
	}
	/**
	 * Finds the index of the minimum element from the given array
	 *
	 * @param array         the array containing numeric values
	 * @return                              the min value among elements
	 */
	protected int getIndexOfMinimum(double[] array) {
		int index = 0;
		double val = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] < val) {
				val = array[i];
				index = i;
			}
		}
		return index;
	}
	/**
	 *      Returns a string that displays the warping distance and path
	 */
	public String toString() {
		String retVal = "Warping Distance: " + warpingDistance + "\n";
		retVal += "Warping Path: {";
		for (int i = 0; i < K; i++) {
			retVal += "(" + warpingPath[i][0] + ", " +warpingPath[i][1] + ")";
			retVal += (i == K - 1) ? "}" : ", ";
		}
		return retVal;
	}

	/**
	 * Tests this class
	 *
	 * @param args  ignored
	 */
	public static void main(String[] args) {
		float[] n2 = {1.5f, 3.9f, 4.1f, 3.3f};
		float[] n1 = {2.1f, 2.45f, 3.673f, 4.32f, 2.05f, 1.93f, 5.67f, 6.01f};
		DTW dtw = new DTW(null, null);
		System.out.println(dtw);
	}
}