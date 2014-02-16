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
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;


public class search {

	protected static SortedMap<String, Long> allNodes = new TreeMap<String, Long>();
	protected static ArrayList<String[]> edges = new ArrayList<String[]>();
	protected static String tieBreakingFile = null;
	public static final Comparator<Node> my_total_order = new MyTotalOrder();
	public static final Comparator<String> my_total_order2 = new MyTotalOrder2();
	private static Hashtable<String, Long> order = new Hashtable<String, Long>();

	private static class MyTotalOrder implements Comparator<Node> {

		public int compare(Node f, Node s) {

			return (f.pathCost == s.pathCost) ? (order.get(f.name)
					.compareTo(order.get(s.name))) : (((Double) f.pathCost)
					.compareTo(s.pathCost));
		}
	};
	
	private static class MyTotalOrder2 implements Comparator<String> {

		public int compare(String f, String s) {
			return (int) (order.get(f)-order.get(s));
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
			SortedSet<String> sortedEdges = new TreeSet<String>();

			while (s.hasNextLine()) {
				sortedEdges.add(s.nextLine());
			}

			for (String str1 : sortedEdges) {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void BFS(String startNode, String goalNode,
			String outputFileName, String outputLogName) throws IOException {

		ArrayList<String> path = new ArrayList<String>();
		LinkedList<String> queue = new LinkedList<String>();
		Hashtable<String, String> parent = new Hashtable<String, String>();
		Hashtable<String, Long> depth = new Hashtable<String, Long>();
		Hashtable<String, Double> pathCostMap = new Hashtable<String, Double>();
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
					Stack<String> outputStack = new Stack<String>();
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
				children = new ArrayList<String>();
				for (String[] str : edges) {
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
					
				}
				Collections.sort(children, my_total_order2);
				for (String str1 : children) {
					queue.add(str1);
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
		Hashtable<String, Long> depth = new Hashtable<String, Long>();
		Hashtable<String, Double> pathCostMap = new Hashtable<String, Double>();
		ArrayList<String> path = new ArrayList<String>();
		Stack<String> stack = new Stack<String>();
		Hashtable<String, String> parent = new Hashtable<String, String>();
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
				children = new ArrayList<String>();
				// add currentNode children to queue
				currentNode = stack.pop();
				if (currentNode.equals(goalNode)) {
					// Solution Found;
					Stack<String> outputStack = new Stack<String>();
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
							// stack.push(str[1]);
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

				Collections.sort(children, my_total_order2);
				Stack<String> localStack = new Stack<String>();
				for (String str1 : children) {
					localStack.push(str1);
				}
				while (!localStack.isEmpty()) {
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
		Hashtable<String, Long> depth = new Hashtable<String, Long>();
		Hashtable<String, Double> pathCostMap = new Hashtable<String, Double>();

		ArrayList<String> path = new ArrayList<String>();
		PriorityQueue<Node> pq = new PriorityQueue<Node>(11, my_total_order);
		Hashtable<String, String> parent = new Hashtable<String, String>();
		String currentNode = startNode;
		Double parentPathCost = 0.0;

		pq.add(new Node(startNode, 0.0));
		path.add(startNode);
		depth.put(startNode, (long) 0);
		pathCostMap.put(startNode, 0.0);

		BufferedWriter outputFile = null;
		BufferedWriter outputLog = null;
		try {

			outputFile = new BufferedWriter(new PrintWriter(outputFileName));
			outputLog = new BufferedWriter(new PrintWriter(outputLogName));

			outputLog.write("name,depth,cost");
			outputLog.newLine();
			Node node1 = null;
			while (!pq.isEmpty()) {
				// add currentNode children to queue
				node1 = pq.remove();
				currentNode = node1.name;
				parentPathCost = node1.pathCost;
				if (currentNode.equals(goalNode)) {
					// Solution Found;
					Stack<String> outputStack = new Stack<String>();
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
						if (pathCostMap.get(str[1]) == null
								|| pathCostMap.get(str[1]) > parentPathCost
										+ Double.parseDouble(str[2])) {
							pq.add(new Node(str[1], parentPathCost
									+ Double.parseDouble(str[2])));
							parent.put(str[1], currentNode);
							depth.put(str[1], depth.get(currentNode) + 1);
							pathCostMap.put(
									str[1],
									pathCostMap.get(currentNode)
											+ Double.parseDouble(str[2]));
						}
					}
					if (str[1].equals(currentNode)) {
						if (pathCostMap.get(str[0]) == null
								|| pathCostMap.get(str[0]) > parentPathCost
										+ Double.parseDouble(str[2])) {
							pq.add(new Node(str[0], parentPathCost
									+ Double.parseDouble(str[2])));
							parent.put(str[0], currentNode);
							depth.put(str[0], depth.get(currentNode) + 1);
							pathCostMap.put(
									str[0],
									pathCostMap.get(currentNode)
											+ Double.parseDouble(str[2]));
						}
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

	public static void clearAllNodes() {
		for (String outer : allNodes.keySet()) {
			allNodes.put(outer, (long) 0);
		}
	}

	public static ArrayList<String> BFS4Communities(String startNode,
			Hashtable<String, Long> depth, BufferedWriter outputFile,
			BufferedWriter outputLog) {
		ArrayList<String> path = new ArrayList<String>();
		LinkedList<String> queue = new LinkedList<String>();
		Hashtable<String, String> parent = new Hashtable<String, String>();
		ArrayList<String> ret = new ArrayList<String>();
		String currentNode = startNode;

		queue.add(startNode);
		path.add(startNode);
		if (!depth.contains(startNode)) {
			depth.put(startNode, (long) 0);
		}
		ArrayList<String> children = null;
		while (!queue.isEmpty()) {
			children = new ArrayList<String>();
			// add currentNode children to queue
			currentNode = queue.remove();
			for (String[] str : edges) {
				if (str[0].equals(currentNode)) {
					allNodes.put(currentNode, (long) 1);
					if (allNodes.get(str[1]) != 1) {
						// queue.add(str[1]);
						children.add(str[1]);
						parent.put(str[1], currentNode);
						allNodes.put(str[1], (long) 1);
						depth.put(str[1], depth.get(currentNode) + 1);
					}
				}

				if (str[1].equals(currentNode)) {
					allNodes.put(currentNode, (long) 1);
					if (allNodes.get(str[0]) != 1) {
						// queue.add(str[0]);
						children.add(str[0]);
						parent.put(str[0], currentNode);
						allNodes.put(str[0], (long) 1);
						depth.put(str[0], depth.get(currentNode) + 1);
					}
				}
			}

			Collections.sort(children, my_total_order2);
			for (String str1 : children) {
				queue.add(str1);
				ret.add(str1);
			}
		}

		return ret;
	}

	public static void findCommunities(String outputFileName,
			String outputLogName) throws IOException {

		BufferedWriter outputFile = null;
		BufferedWriter outputLog = null;
		try {

			outputLog = new BufferedWriter(new FileWriter(outputLogName));
			outputFile = new BufferedWriter(new PrintWriter(outputFileName));

			Hashtable<String, Long> depth = new Hashtable<String, Long>();

			outputLog.write("name,depth,group");
			outputLog.newLine();
			// use BFS to reach all the nodes
			clearAllNodes();
			long component_num = 1;
			long group = 0;
			ArrayList<String> ret = null;
			for (String outer : allNodes.keySet()) {
				if (allNodes.get(outer) == 0) {
					ret = BFS4Communities(outer, depth, outputFile, outputLog);
					component_num++;
					allNodes.put(outer, component_num);
					group = allNodes.get(outer) - 1;
					outputLog.write(outer + "," + depth.get(outer) + ","
							+ group);
					outputLog.newLine();
					outputFile.write(outer);
					for (String inner : ret) {
						allNodes.put(inner, component_num);
						group = allNodes.get(outer) - 1;
						outputLog.write(inner + "," + depth.get(inner) + ","
								+ group);
						outputLog.newLine();

					}
					Collections.sort(ret, my_total_order2);
					for (String inner : ret) {
						outputFile.write("," + inner);
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
}
