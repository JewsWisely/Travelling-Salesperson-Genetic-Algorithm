import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class PointPopulation extends JPanel{

	static final int HEIGHT = 600;
	static final int WIDTH = 900;
	static final byte DELAY = 5;
	private int numPoints;
	private int gen = 0;
	private int gensWithoutChange = 0;
	private int timesNuked = 0;
	private int popSize;
	private PointSpecies[] population;
	private Point[] points;
	private double minMutationRate;
	private double mutationRate;
	private double maxMutationRate;
	private Random rng = new Random();
	
	private double count = 0;
	private double sum = 0;

	public PointPopulation(int popSize, double minMutationRate, double maxMutationRate, Point[] p, int numPoints)
	{
		this.numPoints = numPoints > 2 ? numPoints : 50;
		this.popSize = popSize;
		this.maxMutationRate = Math.min(Math.max(maxMutationRate, 0), 1);
		this.minMutationRate = minMutationRate;
		this.mutationRate = Math.min(minMutationRate, maxMutationRate);
		this.population =  new PointSpecies[popSize];

		//create the points array
		if(p != null && p.length > 3)
		{
			this.points = p;
			this.numPoints = p.length;
		}
		else
		{
			this.points = new Point[numPoints];
			for(int i = 0; i < numPoints; i++)
				this.points[i] = new Point();
		}

		//create the population of PointSpecies
		population = new PointSpecies[popSize];
		for(int i = 0; i < popSize; i++)
			population[i] = new PointSpecies(points, true);

		calculateShortestPath();

		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.BLACK);
		Timer timer = new Timer(DELAY, (ActionEvent e) -> {
			//evolve here
			
			double t0 = System.nanoTime();
			
			for(int i = 0; i < 10; i++)
				createNextPopulation();

			double diff = (System.nanoTime() - t0) / 1000000;
			this.sum += diff;
			this.count++;
//			System.out.println((int)(diff) + "ms " + (int)(this.sum / this.count));
			
			repaint();
		});
		timer.start();
	}

	public void paintComponent(Graphics g)
	{
		
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.RED);
		for(int i = 0; i < numPoints; i++)
			g2.fillRect(points[i].getR() - 2, points[i].getC() - 2, 5, 5);
		g2.setColor(Color.WHITE);
		for(int i = 0; i < numPoints - 1; i++)
			g2.drawLine(population[0].getPoints()[i].getR(), population[0].getPoints()[i].getC(), population[0].getPoints()[i + 1].getR(), population[0].getPoints()[i + 1].getC());
		g2.drawLine(population[0].getPoints()[numPoints - 1].getR(), population[0].getPoints()[numPoints - 1].getC(), population[0].getPoints()[0].getR(), population[0].getPoints()[0].getC());
		g2.drawString("Generation: " + gen,  WIDTH / 2 - 50, 15);
		g2.drawString("Shortest Path: " + population[0].getFitness(), WIDTH / 2 - 100, 30);
		//System.out.println(population[0]);
	}

	//only called once initially because in createNextPopulation the fittest species is always put to the beginning
	private void calculateShortestPath()
	{
		PointSpecies min = population[0];
		int index = 0;
		for(int i = 1; i < popSize; i++)
			if(population[i].getFitness() < min.getFitness()) {
				min = population[i];
				index = i;
			}
		PointSpecies temp = population[0];
		population[0] = min;
		population[index] = temp;
	}

	private void createNextPopulation()
	{
		
		mutationRate = (maxMutationRate - minMutationRate) / (1 + Math.pow(Math.E, -.01 * (gen - numPoints * 10))) + minMutationRate; //dynamic mutation rate with sigmoid function
//		System.out.println(mutationRate);

		PointSpecies[] newPop = new PointSpecies[popSize];
		newPop[0] = population[0]; //preserve the fittest

		Point[] father, mother, baby;


		//nuke the population, start afresh except for most fit
		if(gensWithoutChange == 100 * (timesNuked + 1)) {
			for(int i = 1; i < popSize; i++)
				newPop[i] = new PointSpecies(newPop[0].getPoints(), true);
			gensWithoutChange = 0;
			timesNuked++;
		}
		
		for(int i = 1; i < popSize; i++)
		{
			//mutate
			if(Math.random() < mutationRate) {
				//simple mutation makes new random baby
				//newPop[i] = new PointSpecies(points, true); 

				//more complex mutation process, switch 2 random points
				baby = population[0].getPoints();
				int big = rng.nextInt(numPoints), small = rng.nextInt(numPoints);
				if(big < small)
				{
					int ree = big;
					big = small;
					small = ree;
				}
				/*Point temp;
				if(big - small < numPoints - big + small) {
					while(big - small > 0) {
						temp = baby[big];
						baby[big--] = baby[small];
						baby[small++] = temp;
					}
				}
				else {
					while(Math.abs(big - small) > 1) {
						temp = baby[big % numPoints];
						baby[big++] = baby[small];
						baby[small--] = temp;
						if(small < 0)
							small = numPoints - 1;
						if(big == numPoints)
							big = 0;
					}
					if(small == big + 1) {
						temp = baby[big % numPoints];
						baby[big++] = baby[small];
						baby[small--] = temp;
					}
				}*/
				baby = reverse(baby, small, big); //900
			}
			else
			{	
				
				mother = getMate(); 
				father = getMate(); 
				baby = new Point[numPoints];
				
//				ArrayList<Point> aba = new ArrayList<Point>(Arrays.asList(father)); //51
//
//				int maxIndex = 0;
//				for(int k = rng.nextInt(numPoints); maxIndex < numPoints / 2 + rng.nextInt(numPoints / 2); k = (k + 1) % numPoints) //very important line
//				{
//					baby[maxIndex++] = mother[k];
//					aba.remove(mother[k]);
//				}
//				
//				for(Point p : aba)
//					baby[maxIndex++] = p;
				
				//code below is the same as code above but takes around 70% as much time

				HashSet<Point> seen = new HashSet<Point>();

				int maxIndex = 0;
				for(int k = rng.nextInt(numPoints); maxIndex < numPoints / 2 + rng.nextInt(numPoints / 2); k = (k + 1) % numPoints) //very important line
				{
					baby[maxIndex++] = mother[k];
					seen.add(mother[k]);
				}
				
				for(Point p : father) {
					if(!seen.contains(p)) {
						baby[maxIndex++] = p;
					}
				}
				
			}
			
			newPop[i] = new PointSpecies(baby, false);
			
			if(newPop[i].getFitness() < newPop[0].getFitness())
			{
				PointSpecies temp = newPop[i];
				newPop[i] = newPop[0];
				newPop[0] = temp;
				this.gensWithoutChange = -1; // because we will increment by 1 after this loop breaks
			}

		}
		this.population = newPop;
		this.gen++;
		this.gensWithoutChange++;
	}

	private Point[]	getMate()
	{

		int parent = rng.nextInt(popSize), limiter = rng.nextInt(popSize);
		while(population[parent].getFitness() > population[limiter].getFitness())
			parent = rng.nextInt(popSize);
		return population[parent].getPoints();
	}

	private Point[] reverse(Point[] a, int from, int to) {
		Point temp;
		while(from < to) {
			temp = a[from];
			a[from++] = a[to];
			a[to--] = temp;
		}
		return a;
	}

	public static void main(String[] args)
	{
		int popSize = 100;
		int numPoints = 100;
		float minMutation = 0;
		float maxMutation = 0.05f;
		Point[] points = new Point[numPoints];
		for(int i = 0; i < numPoints; i++)
			points[i] = new Point();

		SwingUtilities.invokeLater(() -> {
			final JFrame frame = new JFrame("Point Connector");
			frame.setLocation(50, 100);
			frame.setResizable(false);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(new PointPopulation(popSize, minMutation, maxMutation, points, numPoints), BorderLayout.CENTER);
			frame.pack();
			frame.setVisible(true);
		});
		
//		SwingUtilities.invokeLater(() -> {
//			final JFrame frame = new JFrame("Point Connector");
//			frame.setLocation(951, 100);
//			frame.setResizable(false);
//			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			frame.add(new PointPopulation(popSize, minMutation, maxMutation, points, numPoints), BorderLayout.CENTER);
//			frame.pack();
//			frame.setVisible(true);
//		});
	}

}
