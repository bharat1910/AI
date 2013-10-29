import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import sun.org.mozilla.javascript.ast.Assignment;

public class MapColoring
{
	public static int N_VAL;
	public static int N_ITER = 3;
	public static int LOWER = 1, UPPER = 50;
	List<List<POINT>> distances; 
	public double assignmentMadeNormal;
	public double assignmentMadeOptimized;
	public long timeNormal, timeMRV;
	
	public List<POINT> points;
	public List<SEGMENT> segments;
	List<List<POINT>> pointConnections;
	
	public class POINT implements Comparable {
		public Double x, y;

		POINT(double xval,double yval) {
			x = xval;
			y = yval;
		}
		
		POINT(POINT p) {
			x = p.x;
			y = p.y;
		}

		@Override	
		public boolean equals(Object obj)
		{
			POINT p = (POINT) obj;
			return p.x == x && p.y == y;
		}
		
		@Override
		public int hashCode()
		{
			return Objects.hash(x, y);
		}
		
		@Override
		public int compareTo(Object obj)
		{
			POINT p = (POINT) obj;
			if (x == p.x) {
				if (y < p.x) {
					return -1;
				}
			}
			else if (x < p.x) {
				return -1;
			}

			return 1;
		}
		
		@Override
		public POINT clone()
		{
			POINT p = new POINT(x, y);
			return p;
		}
	}
	
	public class SEGMENT {
		public POINT p1, p2;

		SEGMENT(POINT a, POINT b) {
			p1 = new POINT(a);
			p2 = new POINT(b);
		}
		
		public boolean shareAnEndPoint(SEGMENT s) {
			if (s.p1.equals(p1) || 
				s.p1.equals(p2) ||
				s.p2.equals(p1) ||
				s.p2.equals(p2)) {
					return true;
				}
			return false;
		}
				
		@Override
		public boolean equals(Object obj)
		{
			SEGMENT s = (SEGMENT) obj;
			return (p1.equals(s.p1) && p2.equals(s.p2)) ||
					(p1.equals(s.p2) && p2.equals(s.p1));
		}
		
		@Override
		public int hashCode()
		{
			return Objects.hash(p1, p2);
		}
	}
	
	public void generateLines()
	{
		points = new ArrayList<>();
		Random randomGenerator = new Random();
		double x, y;
		
		for (int i=0; i<N_VAL; i++) {
			x = randomGenerator.nextDouble();
			y = randomGenerator.nextDouble();
			points.add(new POINT(x, y));
		}
		
		distances = new ArrayList<>();
		List<POINT> distanceForPoint;
		for (int i=0; i<N_VAL; i++) {
			
			distanceForPoint = new ArrayList<>();
			for (int j=0; j<N_VAL; j++) {
				distanceForPoint.add(points.get(j));
			}
			
			final int i_final = i;
			Collections.sort(distanceForPoint, new Comparator<POINT>() {
				@Override
				public int compare(POINT o1, POINT o2)
				{
					return (int) ((getDistance(o1, points.get(i_final)) - getDistance(o2, points.get(i_final))) * 1000.00);
				}
			});
			
			distances.add(distanceForPoint);
		}
	}

	
	public double getDistance(POINT a, POINT b)
	{
		double x1 = a.x - b.x;
		double y1 = a.y - b.y;
		
		return Math.sqrt(x1 * x1 + y1 * y1);
	}
	
	public double getDistance(int i, int j)
	{
		double x1 = points.get(i).x - points.get(j).x;
		double y1 = points.get(i).y - points.get(j).y;
		
		return Math.sqrt(x1 * x1 + y1 * y1);
	}
	
	public boolean checkValidSegment(POINT p, POINT q)
	{
		SEGMENT sdash = new SEGMENT(p, q);
		
		for (SEGMENT s : segments) {
			if (!s.equals(sdash) && s.shareAnEndPoint(sdash)) {
				continue;
			}
			else if (Line2D.linesIntersect(s.p1.x, s.p1.y,
									  s.p2.x, s.p2.y,
									  sdash.p1.x, sdash.p1.y,
									  sdash.p2.x, sdash.p2.y))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public POINT getValidPoint(int random)
	{	
		for (int i=1; i<N_VAL; i++) {
			if (checkValidSegment(distances.get(random).get(i), points.get(random))) {
				return distances.get(random).get(i);
			}
		}

		return null;
	}
	
	public void generateMap()
	{
		Random randomGenerator = new Random();
		int random;
		POINT p;
		segments = new ArrayList<>();
		pointConnections = new ArrayList<>();
		
		for (int i=0; i<points.size(); i++) {
			pointConnections.add(new ArrayList<POINT>());
		}
		
		while (true) {
			random = randomGenerator.nextInt(N_VAL);
			
			p = getValidPoint(random);
			if (p == null) {
				return;
			}
			else {
				pointConnections.get(random).add(p);
				pointConnections.get(points.indexOf(p)).add(points.get(random));
				segments.add(new SEGMENT(points.get(random), p));
			}
		}
	}

	public List<String>	 deepCopyValues(List<String> values)
	{
		List<String> valuesCopy = new ArrayList<>();
		for (int i=0; i<values.size(); i++) {
			valuesCopy.add(new String(values.get(i)));
		}
		return valuesCopy;
	}
	
	public List<Character> deepCopyAssignment(List<Character> assignment)
	{
		List<Character> assignmentCopy = new ArrayList<>();
		for (int i=0; i<assignment.size(); i++) {
			assignmentCopy.add(assignment.get(i));
		}
		return assignmentCopy;
	}
	
	public boolean backtracking(List<String> values, int index, List<Character> assignment)
	{ 
		if (index == N_VAL) {
			for (int i=0; i<points.size(); i++) {
				//System.out.println(points.get(i).x + " " + points.get(i).y + " " + assignment.get(i));
			}
			return true;
		}
		
		String str;
		for (int i=0; i<pointConnections.get(index).size(); i++) {
			int j = points.indexOf(pointConnections.get(index).get(i));
			if (!assignment.get(j).equals(' ')) {
				str = values.get(index).replace(assignment.get(j) + "", "");
				values.set(index, str);
			}
		}
		
		List<String> valuesCopy = deepCopyValues(values);
		List<Character> assignmentCopy = deepCopyAssignment(assignment);
		for (int i=0; i<values.get(index).length(); i++) {
			assignmentCopy.set(index, values.get(index).charAt(i));
//			for (int j=0; j<values.size(); j++) {
//				System.out.print(values.get(j).size() + " ");
//			}
//			System.out.println();
			assignmentMadeNormal++;
			if(backtracking(valuesCopy, index + 1, assignmentCopy)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void forwardChecking(List<String> values, List<Character> assignment)
	{
		int index;
		String str;
		for (int i=0; i<assignment.size(); i++) {
			if (!assignment.get(i).equals(' ')) {
				for (int j=0; j<pointConnections.get(i).size(); j++) {
					index = points.indexOf(pointConnections.get(i).get(j));
					str = values.get(index).replace(assignment.get(i) + "", "");
					values.set(index, str);
				}				
			}
		}
	}
	
	public boolean mrv(List<String> values, int count, List<Character> assignment)
	{ 
		if (count == N_VAL) {
			for (int i=0; i<points.size(); i++) {
				//System.out.println(points.get(i).x + " " + points.get(i).y + " " + assignment.get(i));
			}
			return true;
		}
		
		int index = 0, min_val_index = Integer.MAX_VALUE;
		for (int i=0; i<values.size(); i++) {
			//System.out.println(values.get(i).length());
			if (values.get(i).length() == 0) {
				return false;
			}
			else if (min_val_index > values.get(i).length() && assignment.get(i) == ' ') {
				min_val_index = values.get(i).length();
			}
		}
		
		int conflicts, minimum = Integer.MIN_VALUE;
		for (int i=0; i<values.size(); i++) {
			if (min_val_index == values.get(i).length() && assignment.get(i) == ' ') {
				conflicts = 0;
				for (POINT p : pointConnections.get(i)) {
					if (assignment.get(points.indexOf(p)) == ' ') {
						conflicts++;
					}
				}

				if (conflicts > minimum) {
					minimum = conflicts;
					index = i;
				}
			}
		}
		
		List<Integer> sortedValues = new ArrayList<>();
		List<Character> listValues = new ArrayList<>();
		for (int i=0; i<values.get(index).length(); i++) {
			conflicts = 0;
			for (POINT p : pointConnections.get(index)) {
				if (values.get(points.indexOf(p)).contains(values.get(index).charAt(i) + "")) {
					conflicts++;
				}
			}
			sortedValues.add(conflicts);
			listValues.add(values.get(index).charAt(i));
		}
		
		leastConstrainingValue(sortedValues, listValues);
		
		//System.out.println("v--------------------------v");
		//System.out.println(points.get(index).x + " " + points.get(index).y);
		//System.out.println(values.get(index));
		String str = "";
		for (int i=0; i<listValues.size(); i++) {
			str += listValues.get(i);
		}
		values.set(index, str);
		//System.out.println(values.get(index));
		//System.out.println("v--------------------------v");

//		System.out.println("---------------------------------------");
//		System.out.println(index);
//		System.out.println("---------------------------------------");
//		System.out.println("---------------------------------------");
		
		List<Character> assignmentCopy = deepCopyAssignment(assignment);
		for (int i=0; i<values.get(index).length(); i++) {
			assignmentCopy.set(index, values.get(index).charAt(i));
			assignmentMadeOptimized++;
			
			List<String> valuesCopy = deepCopyValues(values);
			forwardChecking(valuesCopy, assignmentCopy);
						
			if(mrv(valuesCopy, count + 1, assignmentCopy)) {
				return true;
			}
		}
		
		return false;
	}
	
	private void leastConstrainingValue(List<Integer> sortedValues,
			List<Character> listValues)
	{
//		
//		String str = "";
//		for (int i=0; i<sortedValues.size(); i++) {
//			str += sortedValues.get(i) + " "; 
//		}
//		System.out.println(str);
		
		int temp;
		char c;
		for (int i=0; i<sortedValues.size(); i++) {
			for (int j=0; j<sortedValues.size() - 1; j++) {
				if (sortedValues.get(j) > sortedValues.get(j+1)) {
					temp = sortedValues.get(j);
					sortedValues.set(j, sortedValues.get(j+1));
					sortedValues.set(j + 1, temp);
					c = listValues.get(j);
					listValues.set(j, listValues.get(j+1));
					listValues.set(j + 1, c);
				}
			}
		}
	}


	public void runMapColoring()
	{
		generateLines();
		generateMap();
		
		StringBuilder temp = new StringBuilder();
		temp.append("plt.plot(");
		for (int i=0; i<segments.size(); i++) {
			temp.append("[" + segments.get(i).p1.x + "," +  segments.get(i).p2.x + "]" + ", \n" +
						"[" + segments.get(i).p1.y + "," +  segments.get(i).p2.y + "], \n");
		}
		temp.append("123");
		//System.out.println(temp.toString().replaceAll(", \n123","") + ")");
		
		List<String> values = new ArrayList<>();
		List<Character> assignment = new ArrayList<>();
		
		for (int i=0; i<N_VAL; i++) {
			values.add("RGYB");
			assignment.add(' ');
		}
		
		
		long start, end;
		
		//System.out.println();
		//System.out.println("MRV :");
		assignmentMadeOptimized = 0;
		start = System.currentTimeMillis();
		mrv(deepCopyValues(values), 0, deepCopyAssignment(assignment));
		end = System.currentTimeMillis();
		timeMRV = end - start;
		//System.out.println(asssignmentMadeOptimized);
		//System.out.println(end - start);
		//System.out.println();
		
		//System.out.println("Normal :");
		assignmentMadeNormal = 0;
		start = System.currentTimeMillis();
		backtracking(deepCopyValues(values), 0, deepCopyAssignment(assignment));
		end = System.currentTimeMillis();
		timeNormal = end - start;
		//System.out.println(asssignmentMadeNormal);
		//System.out.println(end - start);
		//System.out.println();
	}
	
	public void run()
	{
		List<Double> assignmentMadeNormalAvgList = new ArrayList<>();
		List<Double> assignmentMadeOptAvgList = new ArrayList<>();
		List<Double> timeNormalAvgList = new ArrayList<>();
		List<Double> timeOptimizedAvgList = new ArrayList<>();
		List<Double> edgesList = new ArrayList<>();
		
		for (int i=LOWER; i<=UPPER; i++) {
			Double assignmentMadeNormalAvg = 0.0,
				     assignmentMadeOptAvg = 0.0,
				     timeNormalAvg = 0.0,
				     timeOptimizedAvg = 0.0;
			int edges = 0;
			
			for (int j=0; j<N_ITER; j++) {
				N_VAL = i;
				runMapColoring();
				assignmentMadeNormalAvg += assignmentMadeNormal;
				assignmentMadeOptAvg += assignmentMadeOptimized;
				timeNormalAvg += timeNormal;
				timeOptimizedAvg += timeMRV;
				edges += segments.size();
			}
			
			System.out.println(i);
			System.out.println("Assingments made - normal : " + assignmentMadeNormalAvg/N_ITER);
			System.out.println("Assignments made - optimized : " + assignmentMadeOptAvg/N_ITER);
			System.out.println("Normal time : " + timeNormalAvg/N_ITER);
			System.out.println("Optimized time : " + timeOptimizedAvg/N_ITER);
			System.out.println("Edges : " + edges/(double)N_ITER);
			
			assignmentMadeNormalAvgList.add(assignmentMadeNormalAvg/N_ITER);
			assignmentMadeOptAvgList.add(assignmentMadeOptAvg/N_ITER);
			timeNormalAvgList.add(timeNormalAvg/N_ITER);
			timeOptimizedAvgList.add(timeOptimizedAvg/N_ITER);
			edgesList.add(edges/(double)N_ITER);
		}
		
		String str1 = "", str2 = "";
		for (int i=LOWER; i<=UPPER; i++) {
			str1 += "," + i;			
			str2 += "," + assignmentMadeNormalAvgList.get(i-LOWER);
		}
		str1 = str1.replaceFirst(",", "");
		str2 = str2.replaceFirst(",", "");
		str1 = str1.replace(",", ",\n");
		str2 = str2.replace(",", ",\n");
		System.out.println("plt.plot([" + str1 + "]" + ",\n" + "[" + str2 + "], label=\"Assignments made - normal\")");
		
		str1 = ""; str2 = "";
		for (int i=LOWER; i<=UPPER; i++) {
			str1 += "," + i;			
			str2 += "," + assignmentMadeOptAvgList.get(i-LOWER);
		}
		str1 = str1.replaceFirst(",", "");
		str2 = str2.replaceFirst(",", "");
		str1 = str1.replace(",", ",\n");
		str2 = str2.replace(",", ",\n");
		System.out.println("plt.plot([" + str1 + "]" + ",\n" + "[" + str2 + "], label=\"Assignments made - optimized\")");
		
		str1 = ""; str2 = "";
		for (int i=LOWER; i<=UPPER; i++) {
			str1 += "," + i;			
			str2 += "," + timeNormalAvgList.get(i-LOWER);
		}
		str1 = str1.replaceFirst(",", "");
		str2 = str2.replaceFirst(",", "");
		str1 = str1.replace(",", ",\n");
		str2 = str2.replace(",", ",\n");
		System.out.println("plt.plot([" + str1 + "]" + ",\n" + "[" + str2 + "], label=\"Time taken - normal\")");
		
		str1 = ""; str2 = "";
		for (int i=LOWER; i<=UPPER; i++) {
			str1 += "," + i;			
			str2 += "," + timeOptimizedAvgList.get(i-LOWER);
		}
		str1 = str1.replaceFirst(",", "");
		str2 = str2.replaceFirst(",", "");
		str1 = str1.replace(",", ",\n");
		str2 = str2.replace(",", ",\n");
		System.out.println("plt.plot([" + str1 + "]" + ",\n" + "[" + str2 + "], label=\"Time taken - optimized\")");
		
		str1 = ""; str2 = "";
		for (int i=LOWER; i<=UPPER; i++) {
			str1 += "," + i;			
			str2 += "," + edgesList.get(i-LOWER);
		}
		str1 = str1.replaceFirst(",", "");
		str2 = str2.replaceFirst(",", "");
		str1 = str1.replace(",", ",\n");
		str2 = str2.replace(",", ",\n");
		System.out.println("plt.plot([" + str1 + "]" + ",\n" + "[" + str2 + "], label=\"Edges\")");
		
		System.out.println("plt.legend(bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.)");
	}
	
	public static void main(String[] args)
	{
		MapColoring main = new MapColoring();
		main.run();
		System.exit(0);
	}
}