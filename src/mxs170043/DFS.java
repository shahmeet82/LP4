package mxs170043;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;

import rbk.Graph;
import rbk.Graph.Edge;
import rbk.Graph.Factory;
import rbk.Graph.GraphAlgorithm;
import rbk.Graph.Vertex;

/*
 * Implementation of Topological ordering in DAG
 * by Abhishek Kulkarni (ask171730) and Khushboo Desai (kxd180009)
 * on 26th October 2018 as part of CS 5V81.001 course.
 * The program generates a topological ordering for a DAG
 * and detects if the input graph is cyclic or undirected.
 */

public class DFS extends GraphAlgorithm<DFS.DFSVertex> {
	LinkedList<Vertex> firstList;
	int topNum;
	boolean[] recStack;

	public static class DFSVertex implements Factory {
		int cno;
		boolean seen;
		Vertex parent;
		int top;

		public DFSVertex(Vertex u) {
			this.seen = false;
			this.top = 0;
			this.parent = null;
		}

		public DFSVertex make(Vertex u) {
			return new DFSVertex(u);
		}
	}

	public DFS(Graph g) {
		super(g, new DFSVertex(null));
	}

	public boolean dfs() {
		topNum = g.size();
		firstList = new LinkedList<Vertex>();

		// recStack is used to check for cyclic graph
		recStack = new boolean[topNum];
		Arrays.fill(recStack, false);

		for (Vertex u : g) {
			get(u).seen = false;
			get(u).parent = null;
			get(u).top = 0;
		}

		for (Vertex u : g) {
			if (!get(u).seen) {
				// return false if cyclic graph
				if (!dfsVisit(u)) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean dfsVisit(Vertex u) {
		get(u).seen = true;
		recStack[u.getIndex()] = true;

		for (Edge e : g.incident(u)) {
			Vertex v = e.toVertex();
			if (recStack[v.getIndex()]) {
				return false;
			}

			if (!get(v).seen) {
				if (!dfsVisit(v)) {
					return false;
				}
			}

		}
		get(u).top = topNum--;
		firstList.addFirst(u);
		recStack[u.getIndex()] = false;
		return true;
	}

	public static DFS depthFirstSearch(Graph g) {
		DFS d = new DFS(g);

		// check if the graph is directed or not
		if (g.isDirected()) {
			d.dfs();
		} else
			return null;
		return d;
	}

	// Member function to find topological order
	public List<Vertex> topologicalOrder1() {
		return this.dfs() == true ? firstList : null;
	}

	// Find the number of connected components of the graph g by running dfs.
	// Enter the component number of each vertex u in u.cno.
	// Note that the graph g is available as a class field via GraphAlgorithm.
	public int connectedComponents() {
		return 0;
	}

	// After running the onnected components algorithm, the component no of each
	// vertex can be queried.
	public int cno(Vertex u) {
		return get(u).cno;
	}

	// Find topological oder of a DAG using DFS. Returns null if g is not a DAG.
	public static List<Vertex> topologicalOrder1(Graph g) {
		DFS d = new DFS(g);
		return d.topologicalOrder1();
	}

	// Find topological oder of a DAG using the second algorithm. Returns null if g
	// is not a DAG.
	public static List<Vertex> topologicalOrder2(Graph g) {
		return null;
	}

	public static void main(String[] args) throws Exception {
//		String string = "7 8 1 2 2 1 3 3 2 4 5 3 4 4 4 5 1 5 1 7 6 7 1 7 6 1 0";
//		String string = "6 6  6 3 1  6 1 1  5 1 1  5 2 1  3 4 1  4 2 1";
//		String string = "6 10  1 2 1  1 4 1  2 3 1  2 4 1  3 4 1  3 5 1  3 6 1  4 5 1  4 6 1  5 6 1";
		String string = "10 12  1 3 1   3 2 1  2 4 1   4 7 1  1 8 1  8 2 1  8 5 1  5 10 1 5 4 1  6 8 1  6 10 1  10 9 1";
//		String string = "3 3  1 2 1  2 3 1 3 1 1";
		Scanner in;
		// If there is a command line argument, use it as file from which
		// input is read, otherwise use input from string.
		in = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(string);

		// Read graph from input
		Graph g = Graph.readGraph(in, true);
		g.printGraph(false);

		DFS d = new DFS(g);
		List<Vertex> List1 = d.topologicalOrder1();

		if (List1 != null) {
			// int numcc = d.connectedComponents();
			// System.out.println("Number of components: " + numcc + "\nu\tcno");
			for (Vertex u : List1) {
				System.out.print(u + " ");
			}
		} else {
			System.out.println("It is a cyclic graph or undirected graph");
		}
	}
}