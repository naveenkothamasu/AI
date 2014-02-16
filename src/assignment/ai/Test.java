package assignment.ai;

import java.util.Comparator;
import java.util.PriorityQueue;

public class Test {

	private static  class MyComp implements Comparator<Node> {

		public int compare(Node f, Node s) {
			return (f.pathCost == s.pathCost) ? (f.name)
					.compareTo(s.name) : (((Double) f.pathCost)
					.compareTo(s.pathCost));
		}

	}

	public static void main(String[] args){
		
		MyComp c = new MyComp();
		PriorityQueue<Node> pq = new PriorityQueue<>(11, c);
		
		pq.add(new Node("R",22.9));
		pq.add(new Node("L",22.9));
		pq.add(new Node("c",23));
		
		while(!pq.isEmpty()){
			System.out.println(pq.remove().name);
		}
	}

}
