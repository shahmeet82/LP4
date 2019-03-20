package mxs170043;

import rbk.Graph;
import rbk.Graph.Factory;
import rbk.Graph.GraphAlgorithm;
import rbk.Graph.Vertex;
import rbk.Graph.Edge;
import java.io.File;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

import static mxs170043.DFS.topologicalOrder1;

public class PERT extends GraphAlgorithm<PERT.PERTVertex> {

	private int nodeCountCritical;
	private int criticalPathLength;

	public static class PERTVertex implements Factory {

		private int EC;
		private int LC;
		private int slack;
		private boolean criticalNode;
		private int duration;

		public PERTVertex(Vertex u) {
			EC = 0;
			LC = 0;
			slack = 0;
			criticalNode = false;
			duration = 0;
		}

		public PERTVertex make(Vertex u) {
			return new PERTVertex(u);
		}
	}

	public PERT(Graph g) {
		super(g, new PERTVertex(null));
		for (Vertex vertex : g) {
			if (vertex.getName() != 1)
				g.addEdge(0, vertex.getName() - 1, 1);
			if (vertex.getName() != g.size())
				g.addEdge(vertex.getName() - 1, g.size() - 1, 1);

		}
	}

	public void setDuration(Vertex u, int d) {
		this.get(u).duration = d;
	}

	public int duration(Vertex u) {
		return this.get(u).duration;
	}

	public boolean pert() {
		if (!g.isDirected()) {
			return false;
		}

		int numCritical = 0;

		List<Vertex> topologicalOrder = topologicalOrder1(g);
//calculate EC
		for (Vertex vertex : topologicalOrder) {
			for (Edge edge : g.incident(vertex)) {
				if (ec(vertex) + duration(edge.otherEnd(vertex)) > ec(edge.otherEnd(vertex))) {
					this.get(edge.otherEnd(vertex)).EC = ec(vertex) + this.get(edge.otherEnd(vertex)).duration;
				}
			}
		}
//calculate LC
		int maxTime = get(g.getVertex(g.size())).EC;
		for (Vertex vertex : g)
			this.get(vertex).LC = maxTime;

		ListIterator<Vertex> reverseTopologicalOrderIterator = topologicalOrder.listIterator(topologicalOrder.size());
		while (reverseTopologicalOrderIterator.hasPrevious()) {
			Vertex vertex = reverseTopologicalOrderIterator.previous();
			for (Edge edge : g.incident(vertex)) {
				if (lc(vertex) > lc(edge.otherEnd(vertex)) - duration(edge.otherEnd(vertex))) {
					this.get(vertex).LC = lc(edge.otherEnd(vertex)) - this.get(edge.otherEnd(vertex)).duration;
				}
			}
			this.get(vertex).slack = lc(vertex) - ec(vertex);
			if (slack(vertex) == 0) {
				this.get(vertex).criticalNode = true;
				numCritical++;
			}
		}
		this.nodeCountCritical = numCritical;
		this.criticalPathLength = get(g.getVertex(g.size())).EC;
		return true;
	}

	public int ec(Vertex u) {
		return this.get(u).EC;
	}

	public int lc(Vertex u) {
		return this.get(u).LC;
	}

	public int slack(Vertex u) {
		return this.get(u).slack;
	}

	public int criticalPath() {
		return criticalPathLength;
	}

	public boolean critical(Vertex u) {
		return this.get(u).criticalNode;
	}

	public int numCritical() {
		return nodeCountCritical;
	}

	public static PERT pert(Graph g, int[] duration) {
		PERT p = new PERT(g);
		for (Vertex u : g.getVertexArray())
			p.setDuration(u, duration[u.getIndex()]);
		if (p.pert())
			return p;
		return null;
	}

	public static void main(String[] args) throws Exception {
		String graph = "11 12   2 4 1   2 5 1   3 5 1   3 6 1   4 7 1   5 7 1   5 8 1   6 8 1   6 9 1   7 10 1   8 10 1   9 10 1      0 3 2 3 2 1 3 2 4 1 0";
		Scanner in;
		// If there is a command line argument, use it as file from which
		// input is read, otherwise use input from string.
		in = args.length > 0 ? new Scanner(new File(args[0]))
				: new Scanner(new File("C:\\Users\\abhis\\Desktop\\lp4-pert\\lp4-pert4.txt"));
		Graph g = Graph.readDirectedGraph(in);
		g.printGraph(false);
		PERT p = new PERT(g);
		for (Vertex vertex : g) {
			p.setDuration(vertex, in.nextInt());
		}
		// Run PERT algorithm. Returns null if g is not a DAG
		if (!p.pert()) {
			System.out.println("Invalid graph: not a DAG");
		} else {
			System.out.println("Number of critical vertices: " + p.numCritical());
			System.out.println("u\tEC\tLC\tSlack\tCritical");
			for (Vertex u : g) {
				System.out.println(u + "\t" + p.get(u).duration + "\t" + p.ec(u) + "\t" + p.lc(u) + "\t" + p.slack(u)
						+ "\t" + p.critical(u));
			}
		}
	}
}
