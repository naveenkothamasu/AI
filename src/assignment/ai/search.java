package assignment.ai;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;

public class search {

	protected static Hashtable<String, Long> allNodes = new Hashtable<>();
	protected static ArrayList<String[]> edges = new ArrayList<String[]>();

	public static void main(String[] args) {

		int taskNumber = 0;
		String startNode = null;
		String goalNode = null;
		String inputFile = null;
		String tieBreakingFile = null;
		String outputFile = null;
		String outputLog = null;

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-t")) {
				taskNumber = Integer.parseInt(args[i + 1]);
			} else if (args[i].equals("-s")) {
				startNode = args[i + 1];
			} else if (args[i].equals("-g")) {
				goalNode = args[i + 1];
			} else if (args[i].equals("-i")) {
				inputFile = args[i + 1];
			} else if (args[i].equals("-T")) {
				tieBreakingFile = args[i + 1];
			} else if (args[i].equals("-op")) {
				outputFile = args[i + 1];
			} else if (args[i].equals("-ol")) {
				outputLog = args[i + 1];
			}
		}
		String[] str = null;

		Scanner s;
		try {
			// File file = new File(inputFile);
			FileReader fr = new FileReader(inputFile);
			s = new Scanner(fr);
			while (s.hasNextLine()) {
				str = s.nextLine().split(",");
				edges.add(str);
				allNodes.put(str[0], (long) 0);
				allNodes.put(str[1], (long) 0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		 * System.out.println("Task Number: " + taskNumber);
		 * System.out.println("Start Node: " + startNode);
		 * System.out.println("Goal Node: " + goalNode);
		 * System.out.println("input File: " + inputFile);
		 * System.out.println("tie breaking File: " + tieBreakingFile);
		 * System.out.println("output File: " + outputFile);
		 * System.out.println("output Log: " + outputLog);
		 */
		System.out.println("BFS");
		System.out.println("----");
		Hashtable<String, String> path = BFS(startNode, goalNode);
		String parent = goalNode;
		if (path != null) {
			while (parent != null) {
				System.out.println(parent);
				parent = path.get(parent);
			}
		}
		System.out.println();
		System.out.println("DFS");
		System.out.println("----");
		path = DFS(startNode, goalNode);
		parent = goalNode;
		if (path != null) {
			while (parent != null) {
				System.out.println(parent);
				parent = path.get(parent);
			}
		}
		System.out.println();
		System.out.println("UCS");
		System.out.println("----");
		path = UCS(startNode, goalNode);
		parent = goalNode;
		if (path != null) {
			while (parent != null) {
				System.out.println(parent);
				parent = path.get(parent);
			}
		}
		
		findCommnities();
	}

	public static Hashtable<String, String> BFS(String startNode,
			String goalNode) {

		ArrayList<String> path = new ArrayList<>();
		LinkedList<String> queue = new LinkedList<>();
		Hashtable<String, String> parent = new Hashtable<>();
		String currentNode = startNode;

		queue.add(startNode);
		path.add(startNode);
		while (!queue.isEmpty()) {
			// add currentNode children to queue
			currentNode = queue.remove();
			if (currentNode.equals(goalNode)) {
				return parent; // Solution Found;
			}
			for (String[] str : edges) {
				if (str[0].equals(currentNode)) {
					if (allNodes.get(str[1]) != 1) {
						queue.add(str[1]);
						parent.put(str[1], currentNode);
						allNodes.put(str[1], (long) 1);
					}

				}
			}
		}
		return null;

	}

	public static Hashtable<String, String> DFS(String startNode,
			String goalNode) {

		ArrayList<String> path = new ArrayList<>();
		Stack<String> stack = new Stack<String>();
		Hashtable<String, String> parent = new Hashtable<>();
		String currentNode = startNode;

		stack.push(startNode);
		path.add(startNode);
		while (!stack.isEmpty()) {
			// add currentNode children to queue
			currentNode = stack.pop();
			// path.add(currentNode);
			if (currentNode.equals(goalNode)) {
				return parent; // Solution Found;
			}
			for (String[] str : edges) {
				if (str[0].equals(currentNode)) {
					if (allNodes.get(str[1]) != 1) {
						stack.push(str[1]);
						parent.put(str[1], currentNode);
						allNodes.put(str[1], (long) 1);
					}

				}
			}
		}
		return null;
	}

	public static Hashtable<String, String> UCS(String startNode,
			String goalNode) {
		//TODO: tie-breaking
		ArrayList<String> path = new ArrayList<>();
		PriorityQueue<Double> pq = new PriorityQueue<Double>();
		Hashtable<Double, String> pathCost = new Hashtable<>();
		Hashtable<String, String> parent = new Hashtable<>();
		String currentNode = startNode;

		pq.add(0.0);
		pathCost.put(0.0, startNode);
		path.add(startNode);
		while (!pq.isEmpty()) {
			// add currentNode children to queue
			currentNode = pathCost.get(pq.remove());
			if (currentNode.equals(goalNode)) {
				return parent; // Solution Found;
			}
			for (String[] str : edges) {
				if (str[0].equals(currentNode)) {
					if (allNodes.get(str[1]) != 1) {
						pq.add(Double.parseDouble(str[2]));
						pathCost.put(Double.parseDouble(str[2]), str[1]);
						parent.put(str[1], currentNode);
						allNodes.put(str[1], (long) 1);
					}

				}
			}
		}
		return null;
	}

	public static void findCommnities() {

		// use BFS to reach all the nodes
		long component_num = 1;
		for(String outer: allNodes.keySet()){
			allNodes.put(outer, (long) 0);
		}
		for (String outer : allNodes.keySet()) {
			if (allNodes.get(outer) == 0) {
				BFS(outer, "123");
				component_num++;
				allNodes.put(outer,component_num);
				for (String inner : allNodes.keySet()) {
					if (allNodes.get(inner) == 1) {
						allNodes.put(inner, component_num);
					}
					
				}
			}
		}
		
		for(String outer: allNodes.keySet()){
			System.out.println(outer + " "+ allNodes.get(outer));
		}
	}
}
