package assignment.ai;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

public class search {

	protected static TreeMap<String, Long> allNodes = new TreeMap<>();
	protected static ArrayList<String[]> edges = new ArrayList<String[]>();
	protected static String tieBreakingFile = null;
	public static final Comparator<Node> my_total_order = new MyTotalOrder();
	private static Hashtable<String, Long> order = new Hashtable<>();

	private static class MyTotalOrder implements Comparator<Node> {

		public int compare(Node f, Node s) {
			if (f.pathCost == s.pathCost && order.get(f) != null
					&& order.get(s) != null) {
				return (int) (-order.get(f) + order.get(s));
			}
			return (int) (f.pathCost - s.pathCost);
		}
	};

	private static void loadOrder() {
		FileReader fr = null;
		Scanner scanner = null;
		long i = 0;
		try {
			fr = new FileReader(tieBreakingFile);
			scanner = new Scanner(fr);
			while (scanner.hasNextLine()) {
				order.put(scanner.nextLine(), i++);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				fr.close();
				scanner.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

		int taskNumber = 0;
		String startNode = null;
		String goalNode = null;
		String inputFile = null;

		String outputFile = null;
		String outputLog = null;
		boolean firstT = true;
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-t") && firstT) {
				taskNumber = Integer.parseInt(args[i + 1]);
				firstT = false;
			} else if (args[i].equals("-s")) {
				startNode = args[i + 1];
			} else if (args[i].equals("-g")) {
				goalNode = args[i + 1];
			} else if (args[i].equals("-i")) {
				inputFile = args[i + 1];
			} else if (args[i].equals("-t")) {
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
			FileReader fr = new FileReader(inputFile);
			s = new Scanner(fr);
			SortedSet<String> sortedEdges = new TreeSet<>();
			
			while(s.hasNextLine()){
				sortedEdges.add(s.nextLine());
			}
			/*
			Stack<String> localStack = new Stack<>();
			for(String str1: sortedEdges){
				localStack.push(str1);
			}
			sortedEdges = new TreeSet<>();
			String str1 = null;
			while(!localStack.isEmpty()){
				str1 = localStack.pop();
				str = str1.split(",");
				edges.add(str);
				allNodes.put(str[0], (long) 0);
				allNodes.put(str[1], (long) 0);
			}
			*/
			for(String str1: sortedEdges){
				str = str1.split(",");
				edges.add(str);
				allNodes.put(str[0], (long) 0);
				allNodes.put(str[1], (long) 0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		loadOrder();
		try {

			if (taskNumber == 1) {
				clearAllNodes();
				BFS(startNode, goalNode, outputFile, outputLog);
			} else if (taskNumber == 2) {
				clearAllNodes();
				DFS(startNode, goalNode, outputFile, outputLog);
			} else if (taskNumber == 3) {
				clearAllNodes();
				UCS(startNode, goalNode, outputFile, outputLog);
			} else if (taskNumber == 4) {
				clearAllNodes();
				findCommunities(outputFile, outputLog);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void BFS(String startNode, String goalNode,
			String outputFileName, String outputLogName) throws IOException {

		ArrayList<String> path = new ArrayList<>();
		LinkedList<String> queue = new LinkedList<>();
		Hashtable<String, String> parent = new Hashtable<>();
		Hashtable<String, Long> depth = new Hashtable<>();
		Hashtable<String, Double> pathCostMap = new Hashtable<>();
		boolean solutionFound = false;

		String currentNode = startNode;

		BufferedWriter outputFile = null;
		BufferedWriter outputLog = null;
		try {

			outputFile = new BufferedWriter(new FileWriter(outputFileName));
			outputLog = new BufferedWriter(new FileWriter(outputLogName));

			outputLog.write("name,depth,cost");
			outputLog.newLine();
			
			queue.add(startNode);
			path.add(startNode);
			depth.put(startNode, (long) 0);
			pathCostMap.put(startNode, 0.0);
			while (!queue.isEmpty()) {
				// add currentNode children to queue
				currentNode = queue.remove();
				if (currentNode.equals(goalNode)) {
					// Solution Found;
					Stack<String> outputStack = new Stack<>();
					String node = goalNode;
					while (node != null) {
						outputStack.push(node);
						node = parent.get(node);
					}
					outputLog.write(currentNode + "," + depth.get(currentNode)
							+ "," + pathCostMap.get(currentNode));
					outputLog.newLine();
					
					while (!outputStack.isEmpty()) {
						outputFile.write(outputStack.pop());
						outputFile.newLine();
					}
					solutionFound = true;
					break;
				}
				outputLog.write(currentNode + "," + depth.get(currentNode)
						+ "," + pathCostMap.get(currentNode));
				outputLog.newLine();
				ArrayList<String> children = null;
				for (String[] str : edges) {
					 children = new ArrayList<>();
					if (str[0].equals(currentNode)) {
						allNodes.put(currentNode, (long) 1);
						if (allNodes.get(str[1]) != 1) {
							children.add(str[1]);
							parent.put(str[1], currentNode);
							allNodes.put(str[1], (long) 1);
							depth.put(str[1], depth.get(currentNode) + 1);
							pathCostMap.put(
									str[1],
									pathCostMap.get(currentNode)
											+ Double.parseDouble(str[2]));
						}
					}

					if (str[1].equals(currentNode)) {
						allNodes.put(currentNode, (long) 1);
						if (allNodes.get(str[0]) != 1) {
							children.add(str[0]);
							parent.put(str[0], currentNode);
							allNodes.put(str[0], (long) 1);
							depth.put(str[0], depth.get(currentNode) + 1);
							pathCostMap.put(
									str[0],
									pathCostMap.get(currentNode)
											+ Double.parseDouble(str[2]));
						}
					}
					Collections.sort(children);
					Stack<String> localStack = new Stack<>();
					for(String str1: children){
						localStack.add(str1);
					}
					while(!localStack.isEmpty()){
						queue.add(localStack.pop());
					}
				}
			}
			if (!solutionFound) {
				outputFile.write("There is no path between " + startNode + " "
						+ goalNode);
				outputFile.newLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				outputFile.close();
				outputLog.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void DFS(String startNode, String goalNode,
			String outputFileName, String outputLogName) throws IOException {

		boolean solutionFound = false;
		Hashtable<String, Long> depth = new Hashtable<>();
		Hashtable<String, Double> pathCostMap = new Hashtable<>();
		ArrayList<String> path = new ArrayList<>();
		Stack<String> stack = new Stack<String>();
		Hashtable<String, String> parent = new Hashtable<>();
		String currentNode = startNode;

		stack.push(startNode);
		path.add(startNode);
		depth.put(startNode, (long) 0);
		pathCostMap.put(startNode, 0.0);
		ArrayList<String> children = null;

		BufferedWriter outputFile = null;
		BufferedWriter outputLog = null;
		try {

			outputFile = new BufferedWriter(new FileWriter(outputFileName));
			outputLog = new BufferedWriter(new FileWriter(outputLogName));

			outputLog.write("name,depth,cost");
			outputLog.newLine();
			
			while (!stack.isEmpty()) {
				children = new ArrayList<>();
				// add currentNode children to queue
				currentNode = stack.pop();
				if (currentNode.equals(goalNode)) {
					// Solution Found;
					Stack<String> outputStack = new Stack<>();
					String node = goalNode;

					while (node != null) {
						outputStack.push(node);
						node = parent.get(node);
					}

					outputLog.write(currentNode + "," + depth.get(currentNode)
							+ "," + pathCostMap.get(currentNode));
					outputLog.newLine();

					while (!outputStack.isEmpty()) {
						outputFile.write(outputStack.pop());
						outputFile.newLine();
					}
					solutionFound = true;
					break;
				}
				outputLog.write(currentNode + "," + depth.get(currentNode)
						+ "," + pathCostMap.get(currentNode));
				outputLog.newLine();
				
				for (String[] str : edges) {
					if (str[0].equals(currentNode)) {
						allNodes.put(currentNode, (long) 1);
						if (allNodes.get(str[1]) != 1) {
							//stack.push(str[1]);
							children.add(str[1]);
							parent.put(str[1], currentNode);
							allNodes.put(str[1], (long) 1);
							depth.put(str[1], depth.get(currentNode) + 1);
							pathCostMap.put(
									str[1],
									pathCostMap.get(currentNode)
											+ Double.parseDouble(str[2]));
						}
					}

					if (str[1].equals(currentNode)) {
						allNodes.put(currentNode, (long) 1);
						if (allNodes.get(str[0]) != 1) {
							children.add(str[0]);
							parent.put(str[0], currentNode);
							allNodes.put(str[0], (long) 1);
							depth.put(str[0], depth.get(currentNode) + 1);
							pathCostMap.put(
									str[0],
									pathCostMap.get(currentNode)
											+ Double.parseDouble(str[2]));
						}
					}
				}
				
				Collections.sort(children);
				Stack<String> localStack = new Stack<>();
				for(String str1 : children){
					localStack.push(str1);
				}
				while(!localStack.isEmpty()){
					stack.push(localStack.pop());
				}
			}

			if (!solutionFound) {
				outputFile.write("There is no path between " + startNode + " "
						+ goalNode);
				outputFile.newLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				outputFile.close();
				outputLog.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void UCS(String startNode, String goalNode,
			String outputFileName, String outputLogName) throws IOException {

		boolean solutionFound = false;
		Hashtable<String, Long> depth = new Hashtable<>();
		Hashtable<String, Double> pathCostMap = new Hashtable<>();

		ArrayList<String> path = new ArrayList<>();
		PriorityQueue<Node> pq = new PriorityQueue<Node>(11, my_total_order);
		Hashtable<Double, String> pathCost = new Hashtable<>();
		Hashtable<String, String> parent = new Hashtable<>();
		String currentNode = startNode;
		Double parentPathCost = 0.0;

		pq.add(new Node(startNode, 0.0));
		pathCost.put(0.0, startNode);
		path.add(startNode);
		depth.put(startNode, (long) 0);
		pathCostMap.put(startNode, 0.0);

		BufferedWriter outputFile = null;
		BufferedWriter outputLog = null;
		try {

			outputFile = new BufferedWriter(new FileWriter(outputFileName));
			outputLog = new BufferedWriter(new FileWriter(outputLogName));

			outputLog.write("name,depth,cost");
			outputLog.newLine();
			SortedMap<String, Double> children = null;
			while (!pq.isEmpty()) {
				children = new TreeMap<>();
				// add currentNode children to queue
				parentPathCost = pq.remove().pathCost;
				currentNode = pathCost.get(parentPathCost);
				if (currentNode.equals(goalNode)) {
					// Solution Found;
					Stack<String> outputStack = new Stack<>();
					String node = goalNode;

					while (node != null) {
						outputStack.push(node);
						node = parent.get(node);
					}

					outputLog.write(currentNode + "," + depth.get(currentNode)
							+ "," + pathCostMap.get(currentNode));
					outputLog.newLine();
					
					while (!outputStack.isEmpty()) {
						outputFile.write(outputStack.pop());
						outputFile.newLine();
					}
					solutionFound = true;
					break;

				}
				outputLog.write(currentNode + "," + depth.get(currentNode)
						+ "," + pathCostMap.get(currentNode) + "\n");
				outputLog.newLine();

				for (String[] str : edges) {
					if (str[0].equals(currentNode)) {
						allNodes.put(currentNode, (long) 1);
						if (allNodes.get(str[1]) != 1) {
							//pq.add(new Node(str[1], parentPathCost));
							children.put(str[1], parentPathCost+Double.parseDouble(str[2]));
							pathCost.put(parentPathCost+Double.parseDouble(str[2]), str[1]);
							parent.put(str[1], currentNode);
							allNodes.put(str[1], (long) 1);
							depth.put(str[1], depth.get(currentNode) + 1);
							pathCostMap.put(
									str[1],
									pathCostMap.get(currentNode)
											+ Double.parseDouble(str[2]));

						}
					}
					if (str[1].equals(currentNode)) {
						allNodes.put(currentNode, (long) 1);
						if (allNodes.get(str[0]) != 1) {
							//pq.add(new Node(str[0], parentPathCost));
							children.put(str[0], parentPathCost+Double.parseDouble(str[2]));
							pathCost.put(parentPathCost+Double.parseDouble(str[2]), str[0]);
							parent.put(str[0], currentNode);
							allNodes.put(str[0], (long) 1);
							depth.put(str[0], depth.get(currentNode) + 1);
							pathCostMap.put(
									str[0],
									pathCostMap.get(currentNode)
											+ Double.parseDouble(str[2]));

						}
					}
				}
				
				Stack<Node> localStack = new Stack<>();
				for(Entry<String, Double> str1: children.entrySet()){
					localStack.add(new Node(str1.getKey(), str1.getValue()));
				}
				while(!localStack.isEmpty()){
					pq.add(localStack.pop());
				}
			}
			if (!solutionFound) {
				outputFile.write("There is no path between " + startNode + " "
						+ goalNode);
				outputFile.newLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				outputFile.close();
				outputLog.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void clearAllNodes() {
		for (String outer : allNodes.keySet()) {
			allNodes.put(outer, (long) 0);
		}
	}

	public static void BFS4Communities(String startNode,
			Hashtable<String, Long> depth) {
		ArrayList<String> path = new ArrayList<>();
		LinkedList<String> queue = new LinkedList<>();
		Hashtable<String, String> parent = new Hashtable<>();
		String currentNode = startNode;

		queue.add(startNode);
		path.add(startNode);
		if (!depth.contains(startNode)) {
			depth.put(startNode, (long) 0);
		}
		while (!queue.isEmpty()) {
			// add currentNode children to queue
			currentNode = queue.remove();
			for (String[] str : edges) {
				if (str[0].equals(currentNode)) {
					allNodes.put(currentNode, (long) 1);
					if (allNodes.get(str[1]) != 1) {
						queue.add(str[1]);
						parent.put(str[1], currentNode);
						allNodes.put(str[1], (long) 1);
						depth.put(str[1], depth.get(currentNode) + 1);
					}
				}

				if (str[1].equals(currentNode)) {
					allNodes.put(currentNode, (long) 1);
					if (allNodes.get(str[0]) != 1) {
						queue.add(str[0]);
						parent.put(str[0], currentNode);
						allNodes.put(str[0], (long) 1);
						depth.put(str[0], depth.get(currentNode) + 1);
					}
				}
			}
		}
	}

	public static void findCommunities(String outputFileName,
			String outputLogName) throws IOException {

		BufferedWriter outputFile = null;
		BufferedWriter outputLog = null;
		try {

			//outputFile = new BufferedWriter(new FileWriter(outputFileName));
			outputLog = new BufferedWriter(new FileWriter(outputLogName));
			
			outputFile = new BufferedWriter(new PrintWriter(System.out));
			
			Hashtable<String, Long> depth = new Hashtable<>();

			outputLog.write("name,depth,group");
			outputLog.newLine();
			// use BFS to reach all the nodes
			clearAllNodes();
			long component_num = 1;
			long group = 0;

			for (String outer : allNodes.keySet()) {
				if (allNodes.get(outer) == 0) {
					BFS4Communities(outer, depth);
					component_num++;
					allNodes.put(outer, component_num);
					group = allNodes.get(outer) - 1;
					outputLog.write(outer + "," + depth.get(outer) + ","
							+ group);
					outputLog.newLine();
					outputFile.write(outer);
					for (String inner : allNodes.keySet()) {
						if (allNodes.get(inner) == 1) {
							allNodes.put(inner, component_num);
							group = allNodes.get(outer) - 1;
							outputLog.write(inner + "," + depth.get(inner)
									+ "," + group);
							outputLog.newLine();
							outputFile.write("," + inner);
						}
					}
					outputLog.write("------------------------");
					outputLog.newLine();
					outputFile.newLine();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				outputFile.close();
				outputLog.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	/*
	 * public static Hashtable<String, String> UniformCostSearch(String
	 * startNode, String goalNode) {
	 * 
	 * TreeSet<Node> open = new TreeSet<>(my_total_order); Hashtable<String,
	 * Double> closed = new Hashtable<>(); Hashtable<String, String> path = new
	 * Hashtable<>(); Hashtable<String, Double> pathCost = new Hashtable<>();
	 * Node node = null;
	 * 
	 * Double parentPathCost = 0.0; for (String str : allNodes.keySet()) { if
	 * (str.equals(startNode)) { open.add(new Node(str, 0));
	 * pathCost.put(startNode, 0.0); } else { open.add(new Node(str,
	 * Long.MAX_VALUE)); } } Node currentNode = null; while (true) { if
	 * (open.isEmpty()) { return null; } currentNode = open.first(); // TODO:
	 * populate path table if (currentNode.name.equals(goalNode)) { return path;
	 * // Solution found } parentPathCost = pathCost.get(currentNode.name); for
	 * (String[] str : edges) { if (str[0].equals(currentNode)) { parentPathCost
	 * += Double.parseDouble(str[2]); pathCost.put(str[1], parentPathCost); } if
	 * (str[1].equals(currentNode)) { parentPathCost +=
	 * Double.parseDouble(str[2]); pathCost.put(str[1], parentPathCost); }
	 * 
	 * if(!open.contains(child) && !closed.contains(child)){
	 * 
	 * }
	 * 
	 * if(open.contains(child)){
	 * 
	 * }
	 * 
	 * if(closed.contains(child)){
	 * 
	 * } } closed.put(currentNode.name, parentPathCost); }
	 * 
	 * }
	 */
}
