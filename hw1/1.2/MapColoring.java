import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MapColoring
{
	public static int N_VAL = 50;
	public int asssignmentMadeNormal;
	public int asssignmentMadeOptimized;
	
	public List<POINT> points;
	public List<SEGMENT> segments;
	
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
	}
	
	public double getDistance(int i, int j)
	{
		double x1 = points.get(i).x - points.get(j).x;
		double y1 = points.get(i).y - points.get(j).y;
		
		return Math.sqrt(x1 * x1 + y1 * y1);
	}
	
	public boolean checkValidSegment(int i, int j)
	{
		SEGMENT sdash = new SEGMENT(points.get(i), points.get(j));
		
		for (int k=0; k<segments.size(); k++) {
			SEGMENT s = segments.get(k);
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
	
	public int getValidPoint(int random)
	{
		int index = -1;
		Double dist = Double.MAX_VALUE, temp;
		
		for (int i=0; i < points.size(); i++) {
			if (i != random && checkValidSegment(i, random)) {
				temp = getDistance(i, random);
				if (temp < dist) {
					dist = temp;
					index = i;
				}
			}
		}
		
		return index;
	}
	
	public void generateMap()
	{
		Random randomGenerator = new Random();
		int random, index;
		segments = new ArrayList<>();
		
		while (true) {
			random = randomGenerator.nextInt(N_VAL);
			
			index = getValidPoint(random);
			if (index == -1) {
				return;
			}
			else {
				segments.add(new SEGMENT(points.get(random), points.get(index)));
			}
		}
	}

	public List<List<String>> deepCopyValues(List<List<String>> values)
	{
		List<List<String>> valuesCopy = new ArrayList<>();
		List<String> temp;
		for (int i=0; i<values.size(); i++) {
			temp = new ArrayList<String>();
			for (int j=0; j<values.get(i).size(); j++) {
				temp.add(values.get(i).get(j));
			}
			valuesCopy.add(temp);
		}
		return valuesCopy;
	}
	
	public List<String> deepCopyAssignment(List<String> assignment)
	{
		List<String> assignmentCopy = new ArrayList<>();
		for (int i=0; i<assignment.size(); i++) {
			assignmentCopy.add(assignment.get(i));
		}
		return assignmentCopy;
	}
	
	public boolean backtracking(List<List<String>> values, int index, List<String> assignment)
	{ 
		if (index == N_VAL) {
			for (int i=0; i<points.size(); i++) {
				//System.out.println(points.get(i).x + " " + points.get(i).y + " " + assignment.get(i));
			}
			return true;
		}
		
		for (int i=0; i<segments.size(); i++) {
			if (segments.get(i).p1.equals(points.get(index))) {
				int j = points.indexOf(segments.get(i).p2);
				if (!assignment.get(j).equals("")) {
					values.get(index).remove(assignment.get(j));
				}
			}
			else if (segments.get(i).p2.equals(points.get(index))) {
				int j = points.indexOf(segments.get(i).p1);
				if (!assignment.get(j).equals("")) {
					values.get(index).remove(assignment.get(j));
				}
			}
		}
		
		List<List<String>> valuesCopy = deepCopyValues(values);
		List<String> assignmentCopy = deepCopyAssignment(assignment);
		for (int i=0; i<values.get(index).size(); i++) {
			assignmentCopy.set(index, values.get(index).get(i));
//			for (int j=0; j<values.size(); j++) {
//				System.out.print(values.get(j).size() + " ");
//			}
//			System.out.println();
			asssignmentMadeNormal++;
			if(backtracking(valuesCopy, index + 1, assignmentCopy)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void forwardChecking(List<List<String>> values, List<String> assignment)
	{
		int index;
		for (int i=0; i<assignment.size(); i++) {
			if (!assignment.get(i).equals("")) {
				for (int j=0; j<segments.size(); j++) {
					if (segments.get(j).p1.equals(points.get(i))) {
						index = points.indexOf(segments.get(j).p2);
						values.get(index).remove(assignment.get(i));
					}
					else if (segments.get(j).p2.equals(points.get(i))) {
						index = points.indexOf(segments.get(j).p1);
						values.get(index).remove(assignment.get(i));
					}
				}				
			}
		}
	}
	
	public boolean forwardChecking(List<List<String>> values, int index, List<String> assignment)
	{ 
		if (index == N_VAL) {
			for (int i=0; i<points.size(); i++) {
				//System.out.println(points.get(i).x + " " + points.get(i).y + " " + assignment.get(i));
			}
			return true;
		}
		
		for (int i=0; i<values.size(); i++) {
			if (values.get(i).size() == 0) {
				return false;
			}
		}
		
		for (int i=0; i<segments.size(); i++) {
			if (segments.get(i).p1.equals(points.get(index))) {
				int j = points.indexOf(segments.get(i).p2);
				if (!assignment.get(j).equals("")) {
					values.get(index).remove(assignment.get(j));
				}
			}
			else if (segments.get(i).p2.equals(points.get(index))) {
				int j = points.indexOf(segments.get(i).p1);
				if (!assignment.get(j).equals("")) {
					values.get(index).remove(assignment.get(j));
				}
			}
		}
		
		List<String> assignmentCopy = deepCopyAssignment(assignment);
		for (int i=0; i<values.get(index).size(); i++) {
			assignmentCopy.set(index, values.get(index).get(i));
			asssignmentMadeOptimized++;
			
			List<List<String>> valuesCopy = deepCopyValues(values);
			forwardChecking(values, assignment);
						
			if(forwardChecking(valuesCopy, index + 1, assignmentCopy)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean mrv(List<List<String>> values, int count, List<String> assignment)
	{ 
		if (count == N_VAL) {
			for (int i=0; i<points.size(); i++) {
				//System.out.println(points.get(i).x + " " + points.get(i).y + " " + assignment.get(i));
			}
			return true;
		}
		
		int index = 0, min_val_index = Integer.MAX_VALUE;
		for (int i=0; i<values.size(); i++) {
			if (values.get(i).size() == 0) {
				return false;
			}
			else if (min_val_index > values.size()) {
				index = i;
				min_val_index = values.size();
			}
		}
		
		for (int i=0; i<segments.size(); i++) {
			if (segments.get(i).p1.equals(points.get(index))) {
				int j = points.indexOf(segments.get(i).p2);
				if (!assignment.get(j).equals("")) {
					values.get(index).remove(assignment.get(j));
				}
			}
			else if (segments.get(i).p2.equals(points.get(index))) {
				int j = points.indexOf(segments.get(i).p1);
				if (!assignment.get(j).equals("")) {
					values.get(index).remove(assignment.get(j));
				}
			}
		}
		
		List<String> assignmentCopy = deepCopyAssignment(assignment);
		for (int i=0; i<values.get(index).size(); i++) {
			assignmentCopy.set(index, values.get(index).get(i));
			asssignmentMadeOptimized++;
			
			List<List<String>> valuesCopy = deepCopyValues(values);
			forwardChecking(values, assignment);
						
			if(mrv(valuesCopy, count + 1, assignmentCopy)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void run()
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
		System.out.println(temp.toString().replaceAll(", \n123","") + ")");
		
		List<List<String>> values = new ArrayList<>();
		List<String> assignment = new ArrayList<>();
		
		for (int i=0; i<N_VAL; i++) {
			List<String> domain = new ArrayList<>();
			domain.add("R");
			domain.add("G");
			domain.add("Y");
			domain.add("B");
			values.add(domain);
			
			assignment.add("");
		}
		
		System.out.println();
		System.out.println("MRV :");
		asssignmentMadeOptimized = 0;
		mrv(deepCopyValues(values), 0, deepCopyAssignment(assignment));
		System.out.println(asssignmentMadeOptimized);
		
		System.out.println("Optimized :");
		asssignmentMadeOptimized = 0;
		forwardChecking(deepCopyValues(values), 0, deepCopyAssignment(assignment));
		System.out.println(asssignmentMadeOptimized);
		
		System.out.println("Normal :");
		asssignmentMadeNormal = 0;
		backtracking(values, 0, assignment);
		System.out.println(asssignmentMadeNormal);
	}
	
	public static void main(String[] args) {
		MapColoring main = new MapColoring();
		main.run();
		System.exit(0);
	}
}