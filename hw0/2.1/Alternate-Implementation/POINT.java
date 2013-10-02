

public class POINT implements Comparable<POINT> {
	int x;
	int y;
	int count;
	int heuristic;
	

	public POINT(int i, int j, int c) {
		x = i;
		y = j;
		count = c;
	}

	public POINT(int i, int j, int c, int h) {
		x = i;
		y = j;
		count = c;
		heuristic = h;
	}

	@Override
	public int compareTo(POINT obj) {
		return heuristic - obj.heuristic;
	}
}