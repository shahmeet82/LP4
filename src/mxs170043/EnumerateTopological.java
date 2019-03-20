package mxs170043;

import rbk.Graph.GraphAlgorithm;
import rbk.Graph.Vertex;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Stack;

import rbk.Graph;
import rbk.Graph.Edge;
import rbk.Graph.Factory;

public class EnumerateTopological extends GraphAlgorithm<EnumerateTopological.EnumVertex> {
	boolean print;
	long count;
	Selector sel;
	Stack<Vertex> stack;

	public EnumerateTopological(Graph g) {
		super(g, new EnumVertex());
		print = false;
		count = 0;
		sel = new Selector();
		stack = new Stack<>();
	}

	static class EnumVertex implements Factory {

		int count;
		int in;
		boolean seen;

		EnumVertex() {
			count = 0;
			in = 0;
		}

		public EnumVertex make(Vertex u) {
			return new EnumVertex();
		}
	}

	class Selector extends Enumerate.Approver<Vertex> {

		// Method to see if the given vertex cab be used for permutation
		@Override
		public boolean select(Vertex u) {
			if (get(u).in == 0 && !get(u).seen) {
				get(u).seen = true;
				stack.push(u);
				for (Edge edge : g.outEdges(u)) {
					get(edge.toVertex()).in--;
				}
				return true;
			} else
				return false;
		}

		@Override
		public void unselect(Vertex u) {
			get(u).seen = false;
			for (Edge edge : g.outEdges(u)) {
				get(edge.toVertex()).in++;
			}
			stack.pop();
		}

		@Override
		public void visit(Vertex[] arr, int k) {
			count++;
			if (print) {
				for (Vertex vertex : arr) {
					System.out.print(vertex + " ");
				}
				System.out.println();
			}
		}
	}

	// To do: LP4; return the number of topological orders of g
	public long enumerateTopological(boolean flag) {
		print = flag;
		for (Vertex vertex : g) {
			get(vertex).in = vertex.inDegree();
			get(vertex).seen = false;
		}
		Enumerate<Vertex> enumerate = new Enumerate<>(g.getVertexArray(), sel);
		enumerate.permute(g.getVertexArray().length);
		return count;
	}

	// -------------------static methods----------------------

	public static long countTopologicalOrders(Graph g) {
		EnumerateTopological et = new EnumerateTopological(g);
		return et.enumerateTopological(false);
	}

	public static long enumerateTopologicalOrders(Graph g) {
		EnumerateTopological et = new EnumerateTopological(g);
		return et.enumerateTopological(true);
	}

	public static void main(String[] args) throws FileNotFoundException {
		int VERBOSE = 0;
		if (args.length > 0) {
			VERBOSE = Integer.parseInt(args[0]);
		}
		Graph g = Graph.readDirectedGraph(new java.util.Scanner(
				new File("C:\\Users\\meets\\Desktop\\lp4-enumeratetopological\\permute-dag-50-800.txt")));
		Graph.Timer t = new Graph.Timer();
		long result;
		if (VERBOSE > 0) {
			result = enumerateTopologicalOrders(g);
		} else {
			result = countTopologicalOrders(g);
		}
		System.out.println("\n" + result + "\n" + t.end());
	}

}
