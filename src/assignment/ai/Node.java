package assignment.ai;

public class Node {

	String name;
	double pathCost = 0;

	Node(String name, double d) {
		this.name = name;
		this.pathCost = d;
	}
}
