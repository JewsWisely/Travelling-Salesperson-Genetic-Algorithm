public class Point{

	private int r;
	private int c;
	
	//initialize with random coordinates
	public Point()
	{
		this.r = (int)(Math.random() * PointPopulation.WIDTH);
		this.c = (int)(Math.random() * PointPopulation.HEIGHT);
	}
	
	public Point(int r, int c)
	{
		this.r = r;
		this.c = c;
	}

	public int getR() {
		return r;
	}

	public int getC() {
		return c;
	}

	@Override
	public String toString() {
		return "Point [r=" + r + ", c=" + c + "]";
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof Point)) {
			return false;
		}
		Point other = (Point)o;
		return r == other.getR() && c == other.getC();
	}

	//find the distance from this point to another
	public double distanceFrom(Point other) {
		return Math.sqrt(Math.pow(other.getR() - getR(), 2) + Math.pow(other.getC() - getC(), 2));
	}
	
}
