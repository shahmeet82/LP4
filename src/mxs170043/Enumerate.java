package mxs170043;

import java.util.Comparator;

/**
 * @author      Abhishek Kulkarni<ask171730@utdallas.edu>
 * @author 		Meet Shah<mxs170043@utdallas.edu>
 * @author 		Aishwarya Patel<>
 * @author 		Yash Modi<>
 * @version     1.0          (current version number of program)
 * @since       1.0          (the version of the package this class was first added to)
 */

public class Enumerate<T> {
	T[] arr;
	int k;
	int count;
	Approver<T> app;

	/**
	 * constructor
	 * @param  arr T[]
	 * @param k int
     * @param app Approver <T>
	 *
	 */
	public Enumerate(T[] arr, int k, Approver<T> app) {
		this.arr = arr;
		this.k = k;
		this.count = 0;
		this.app = app;
	}

    /**
     * constructor
     * @param arr T[]
     * @param app Approver<T>
     */
	public Enumerate(T[] arr, Approver<T> app) {
		this(arr, arr.length, app);
	}

    /**
     * Constructor
     * @param arr T[]
     * @param k int
     */
	public Enumerate(T[] arr, int k) {
		this(arr, k, new Approver<T>());
	}

    /**
     * Constructor
     * @param arr T[]
     */

	public Enumerate(T[] arr) {
		this(arr, arr.length, new Approver<T>());
	}

	// -------------Methods of Enumerate class: To do-----------------

    /**
     * n = arr.length, choose k things, d elements arr[0..d-1] done
     * c more elements are needed from arr[d..n-1].  d = k-c.
     * @param c int
     */
	public void permute(int c) {
		// if nothing there to choose
		if (c == 0) {
			visit(arr);
		} else {
			int d = k - c;
			// permutation that has arr[d] in the (d+1)th spot
			if (app.select((arr[d]))) {
				permute(c - 1);
				app.unselect(arr[d]);
			}

			for (int i = (d + 1); i < arr.length; i++) {
				if (app.select(arr[i])) {
					swap(d, i);
					permute(c - 1);
					// restore original order
					swap(d, i);
					app.unselect(arr[i]);
				}
			}
		}
	}

	/**
     * choose c items from arr[0..i-1].
     * @param i int
	 * @param c int
	 */
	public void combine(int i, int c) {
		if (c == 0) {
			visit(arr);
		} else {
			int d = k - c;
			swap(d, i);
			combine(i + 1, c - 1);
			// restore order
			swap(d, i);
			if ((arr.length - i) > c) // can skip arr[i]
				combine(i + 1, c);
		}
	}

    /**
     * Method to generate all n! permutations. g more elements remaining
     * @param g int
     */
	public void heap(int g) {
		if (g == 1)
			visit(arr);
		else {
			// check for the looping conditions
			for (int i = 0; i < g - 1; i++) {
				heap(g - 1);
				if (g % 2 == 0)
					swap(i, g - 1);
				else
					swap(0, g - 1);
			}
			heap(g - 1);
		}
	}
    /**
     * Method to generate permutations in lexicographic order(knuth's algorithm)
     * @param c Comparator<T>
     */
	public void algorithmL(Comparator<T> c) {
		visit(arr);
		while (!isDescending(c)) {
			int j = arr.length - 2;
			while (c.compare(arr[j], arr[j + 1]) >= 0) {
				if (j == 0) {
					return;
				}
				j--;
			}

			int l = arr.length - 1;
			while (c.compare(arr[j], arr[l]) >= 0) {
				l--;
			}
			swap(j, l);
			reverse(j + 1, arr.length - 1);
			visit(arr);
		}
	}
    /**
     * Helper method to check if the array is in descending order or not
     * @param c Comparator<T>
     * @return boolean
     */
	private boolean isDescending(Comparator<T> c) {
		for (int i = 1; i < arr.length; i++) {
			if (c.compare(arr[i], arr[i - 1]) > 0)
				return false;
		}
		return true;
	}

    /**
     * Member method to count and print the enumerations.
     * @param array T[]
     */

	public void visit(T[] array) {
		count++;
		app.visit(array, k);
	}

	// ----------------------Nested class: Approver-----------------------

	// Class to decide whether to extend a permutation with a selected item
	// Extend this class in algorithms that need to enumerate permutations with
	// precedence constraints
	public static class Approver<T> {
		/* Extend permutation by item? */
		public boolean select(T item) {
			return true;
		}

		/* Backtrack selected item */
		public void unselect(T item) {
		}

		/* Visit a permutation or combination */
		public void visit(T[] array, int k) {
			for (int i = 0; i < k; i++) {
				System.out.print(array[i] + " ");
			}
			System.out.println();
		}
	}

	// -----------------------Utilities-----------------------------

	void swap(int i, int j) {
		T tmp = arr[i];
		arr[i] = arr[j];
		arr[j] = tmp;
	}

	void reverse(int low, int high) {
		while (low < high) {
			swap(low, high);
			low++;
			high--;
		}
	}

	// --------------------Static methods----------------------------

	// Enumerate permutations of k items out of n = arr.length
	public static <T> Enumerate<T> permute(T[] arr, int k) {
		Enumerate<T> e = new Enumerate<>(arr, k);
		e.permute(k);
		return e;
	}

	// Enumerate combinations of k items out of n = arr.length
	public static <T> Enumerate<T> combine(T[] arr, int k) {
		Enumerate<T> e = new Enumerate<>(arr, k);
		e.combine(0, k);
		return e;
	}

	// Enumerate permutations of n = arr.length item, using Heap's algorithm
	public static <T> Enumerate<T> heap(T[] arr) {
		Enumerate<T> e = new Enumerate<>(arr, arr.length);
		e.heap(arr.length);
		return e;
	}

	// Enumerate permutations of items in array, using Knuth's algorithm L
	public static <T> Enumerate<T> algorithmL(T[] arr, Comparator<T> c) {
		Enumerate<T> e = new Enumerate<>(arr, arr.length);
		e.algorithmL(c);
		return e;
	}

	public static void main(String args[]) {
		int n = 4;
		int k = 3;
		if (args.length > 0) {
			n = Integer.parseInt(args[0]);
			k = n;
		}
		if (args.length > 1) {
			k = Integer.parseInt(args[1]);
		}
		Integer[] arr = new Integer[n];
		for (int i = 0; i < n; i++) {
			arr[i] = i + 1;
		}

		System.out.println("Permutations: " + n + " " + k);
		Enumerate<Integer> e = permute(arr, k);
		System.out.println("Count: " + e.count + "\n_________________________");

		System.out.println("combinations: " + n + " " + k);
		e = combine(arr, k);
		System.out.println("Count: " + e.count + "\n_________________________");

		System.out.println("Heap Permutations: " + n);
		e = heap(arr);
		System.out.println("Count: " + e.count + "\n_________________________");

		Integer[] test = { 1, 2, 2, 3, 3, 4 };
		System.out.println("Algorithm L Permutations: ");
		e = algorithmL(test, (Integer a, Integer b) -> a.compareTo(b));
		System.out.println("Count: " + e.count + "\n_________________________");
	}
}
