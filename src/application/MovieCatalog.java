package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MovieCatalog {

	public AVLTree[] hashTable; // Each slot is an AVL tree
	private int size;
	private static final int INITIAL_SIZE = 11; // A prime number for initial size

	public MovieCatalog() {
		System.out.println("Initializing MovieCatalog with size: " + INITIAL_SIZE);
		allocate(INITIAL_SIZE);
	}

	private int hashFunction(String title, int tableSize) {
		int hash = 0;
		for (char c : title.toCharArray()) {
			hash = (hash * 31 + c) % tableSize;
		}
		return Math.abs(hash);
	}

//	private int hashFunction(String title, int tableSize) {
//		int i = 0;
//		int hash = 0;
//		while (i!=title.length()) {
//			hash = (hash<<5) + title.charAt(i++);
//		}
//		return hash%tableSize;
//	}

	// Allocate a new hash table with AVL trees
	private void allocate(int size) {
		this.size = size;
		hashTable = new AVLTree[size];
		for (int i = 0; i < size; i++) {
			hashTable[i] = new AVLTree();
		}
		System.out.println("Allocated hash table with " + size + " slots.");
	}


	private void resizeHashTable() {
		int newSize = findNextPrime(size * 2);
		System.out.println("Resizing hash table to new size: " + newSize);

		AVLTree[] newHashTable = new AVLTree[newSize];

		for (int i = 0; i < newSize; i++) {
			newHashTable[i] = new AVLTree();
		}

		// send to new size hash table
		for (AVLTree tree : hashTable) {
			if (tree != null) {
				for (Movie movie : tree.getAllMovies()) {
					int newIndex = hashFunction(movie.getTitle(), newSize);
					newHashTable[newIndex].insert(movie);
				}
			}
		}

		hashTable = newHashTable;
		size = newSize;
		System.out.println("Resizing triggered. New size: " + size);

	}

	// Hash function for string keys (movie titles)
	int hashFunction(String title) {
		int hash = 0;
		System.out.println("Hashing title: " + title);
		for (char c : title.toCharArray()) {
			hash = (hash * 31 + c) % size;
			System.out.println("Char: " + c + ", Intermediate Hash: " + hash);
		}
		System.out.println("Final Hash (mod size): " + Math.abs(hash));
		return Math.abs(hash);
	}

	private int findNextPrime(int num) {
		while (!isPrime(num)) {
			num++;
		}
		return num;
	}

	private boolean isPrime(int num) {
		if (num <= 1)
			return false;
		for (int i = 2; i * i <= num; i++) {
			if (num % i == 0) {
				return false;
			}
		}
		return true;
	}

	// Add a movie to the hash table
	public void put(Movie movie) {
		System.out.println("Adding movie: " + movie.getTitle());
		int index = hashFunction(movie.getTitle());
		System.out.println("Inserting movie into AVL tree at index: " + index);
		hashTable[index].insert(movie);
		System.out.println("Movie added successfully.");

		if (calculateAverageHeight() > 3) {
			resizeHashTable();
		}
	}

	public ObservableList<Movie> get(String title, Integer year) {
		if ((title == null || title.isEmpty()) && year == null) {
			return Main.movieList;
		}
		if (title != null && !title.isEmpty()) {
			int index = hashFunction(title);
			AVLTree tree = hashTable[index];
			Main.index.setText("Current Index: " + index + " / " + hashTable.length);
			Main.height.setText("Tree Height: " + hashTable[index].getRoot().height);
			Main.currentIndex = index;

			if (tree != null) {
				for (Movie movie : tree.getAllMovies()) {
					boolean matchesTitle = movie.getTitle().equalsIgnoreCase(title);
					boolean matchesYear = year != null && movie.getReleaseYear() == year;

					if (matchesTitle || matchesYear) {
						Main.searchResults.add(movie);
					}
				}
			}
		} else {
			for (AVLTree tree : hashTable) {
				if (tree != null) {
					for (Movie movie : tree.getAllMovies()) {
						if (year != null && movie.getReleaseYear() == year) {
							Main.searchResults.add(movie);
						}
					}
				}
			}
		}
		return Main.searchResults;

	}

	public void erase(String title) {
		System.out.println("Removing movie: " + title);
		int index = hashFunction(title);

		System.out.println("Deleting movie from AVL tree at index: " + index);
		AVLTree tree = hashTable[index];

		if (tree != null) {
			tree.delete(title);

			// If the tree becomes empty, reset the AVLTree at this index
			if (tree.getCount() == 0) {
				hashTable[index] = new AVLTree(); // Reset the AVLTree
				System.out.println("Tree at index " + index + " is now empty.");
			}
		} else {
			System.out.println("Error: No tree found at index " + index);
		}
	}

	public void printAllMovies() {
		System.out.println("Printing all movies in the catalog:");
		for (int i = 0; i < hashTable.length; i++) {
			AVLTree tree = hashTable[i];
			if (tree != null) {
				System.out.println("Index " + i + ":");
				Movie[] movies = tree.getAllMovies();
				for (Movie movie : movies) {
					System.out.println("  " + movie.getTitle() + " (" + movie.getReleaseYear() + ")");
				}
			}
		}
		System.out.println("Finished printing all movies.");
	}

	public int countMovies() {
		System.out.println("Counting all movies in the catalog...");
		int count = 0;
		for (AVLTree tree : hashTable) {
			if (tree != null) {
				count += tree.getAllMovies().length;
			}
		}
		System.out.println("Total movies in catalog: " + count);
		return count;
	}

	private double calculateAverageHeight() {
		int totalHeight = 0;
		int nonEmptyTrees = 0;

		for (AVLTree tree : hashTable) {
			if (tree != null && tree.getRoot() != null) { // Process only non-empty trees
				totalHeight += tree.getRoot().getHeight();
				nonEmptyTrees++;
			}
		}

		if (nonEmptyTrees == 0) {
			System.out.println("No non-empty trees found in hash table.");
			return 0.0; // Return 0 if all trees are empty
		} else {
			double averageHeight = (double) totalHeight / nonEmptyTrees;
			System.out.println("Average height of trees: " + averageHeight);
			return averageHeight;
		}
	}
	public void deallocate() {
	    System.out.println("Deallocating hash table and clearing all AVL trees...");
	    	    for (int i = 0; i < hashTable.length; i++) {
	        if (hashTable[i] != null) {
	            hashTable[i].setRoot(null);
	            hashTable[i].setCount(0);  
	            hashTable[i] = null;
	        }
	    }
	    hashTable = null;
	    size = 0;
	    System.out.println("Deallocation complete. All resources cleared.");
	}

	public static void loadMoviesFromFile(File selectedFile) {
		if (selectedFile != null) {
			try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
				String line;
				String title = null, description = null;
				int releaseYear = 0;
				double rating = 0.0;

				while ((line = br.readLine()) != null) {
					line = line.trim(); // Trim leading and trailing spaces

					if (line.startsWith("Title:")) {
						title = line.substring(6).trim();
					} else if (line.startsWith("Description:")) {
						description = line.substring(12).trim();
					} else if (line.startsWith("Release Year:")) {
						releaseYear = Integer.parseInt(line.substring(13).trim());
					} else if (line.startsWith("Rating:")) {
						try {
							rating = Double.parseDouble(line.substring(7).trim());
						} catch (NumberFormatException e) {
							System.err.println("Invalid rating format. Skipping movie.");
							rating = 0.0; // Default rating for invalid input
						}
					} else if (line.isEmpty() && title != null) {
						// Process the movie when a blank line is encountered
						Movie movie = new Movie(title, description, releaseYear, rating);
						Main.movieList.add(movie);
						Main.movieCatalog.put(movie);

						// Reset fields for the next movie
						title = null;
						description = null;
						releaseYear = 0;
						rating = 0.0;
					}
				}

				// Process the last movie if the file doesn't end with a blank line
				if (title != null) {
					Movie movie = new Movie(title, description, releaseYear, rating);
					Main.movieList.add(movie);
					Main.movieCatalog.put(movie);
				}

				System.out.println("Movies loaded successfully.");
			} catch (IOException e) {
				System.err.println("Error reading the file: " + e.getMessage());
			}
		} else {
			System.err.println("No file selected.");
		}
	}

}