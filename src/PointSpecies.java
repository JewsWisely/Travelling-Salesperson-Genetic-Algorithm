import java.util.Arrays;

public class PointSpecies {
	
	//order of the points determines the path between them, simply connect each point with the two points adjacent to it
	private Point[] points;
	private double fitness;
	
	//initialize with either given or random order, then calculate fitness
	public PointSpecies(Point[] species, boolean scramble) {
		if(scramble) 
		{
			Point temp;
			for(int i = 0; i < species.length; i++)
			{
				temp = species[i];
				int b = (int)(Math.random() * (species.length));
				species[i] = species[b];
				species[b] = temp;
			}
		}
		this.points = species;
		calcFitness();
	}

	//find the distance between all cities given the current route between them, (based on their order)
	private void calcFitness() {
		double fitness = points[points.length - 1].distanceFrom(points[0]);
		for(int i = 0; i < points.length - 1; i++)
			fitness += points[i].distanceFrom(points[i + 1]);
		this.fitness = fitness;
	}

	public Point[] getPoints() {
		return Arrays.copyOf(points, points.length); //Arrays.copyOf is ESSENTIAL here
	}

	public double getFitness() {
		return fitness;
	}

	@Override
	public String toString() {
		return "PointSpecies [points=" + Arrays.toString(points) + ", fitness=" + fitness + "]";
	}
	
}
