package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

public class AVLTree {
	private int count = 0;
	private AVLNode root;

	public AVLNode getRoot() {
		return root;
	}

	public void setRoot(AVLNode root) {
		this.root = root;
	}

	public void setCount(int count) {
		this.count = count;
	}

	private int height(AVLNode node) {
		if (node == null)
			return 0;
		return node.height;
	}

	private int getBalance(AVLNode node) {
		if (node == null)
			return 0;
		return height(node.left) - height(node.right);
	}

	public void delete(String title) {
		if (search(title) != null) { // Only decrement if the movie exists in the tree
			root = deleteNode(root, title);
			count--;
		}
	}

	private AVLNode deleteNode(AVLNode node, String title) {
		if (node == null) {
			return null; // Node not found
		}

		if (title.compareTo(node.movie.getTitle()) < 0) {
			node.left = deleteNode(node.left, title); // Go to the left subtree
		} else if (title.compareTo(node.movie.getTitle()) > 0) {
			node.right = deleteNode(node.right, title); // Go to the right subtree
		} else {
			// Node to be deleted is found
			if (node.left == null) {
				return node.right; // Return right child if left is null
			} else if (node.right == null) {
				return node.left; // Return left child if right is null
			}

			// Node with two children: Get the inorder successor
			AVLNode successor = min(node.right); // Smallest node in the right subtree
			node.movie = successor.movie; // Replace current node's data with successor's data
			node.right = deleteNode(node.right, successor.movie.getTitle()); // Delete the successor
		}

		// Update the height of the current node
		node.height = Math.max(height(node.left), height(node.right)) + 1;

		// Rebalance the node
		return rebalance(node);
	}

	private AVLNode rebalance(AVLNode node) {
		int balance = getBalance(node);

		// Left-heavy
		if (balance > 1) {
			if (getBalance(node.left) >= 0) {
				return rightRotate(node); // Left-Left Case
			} else {
				node.left = leftRotate(node.left); // Left-Right Case
				return rightRotate(node);
			}
		}

		// Right-heavy
		if (balance < -1) {
			if (getBalance(node.right) <= 0) {
				return leftRotate(node); // Right-Right Case
			} else {
				node.right = rightRotate(node.right); // Right-Left Case
				return leftRotate(node);
			}
		}

		return node; // No rotation needed
	}

	// Right rotation
	private AVLNode rightRotate(AVLNode y) {
		AVLNode x = y.left;
		AVLNode T2 = x.right;

		x.right = y;
		y.left = T2;

		y.height = Math.max(height(y.left), height(y.right)) + 1;
		x.height = Math.max(height(x.left), height(x.right)) + 1;

		return x;
	}

	// Left rotation
	private AVLNode leftRotate(AVLNode x) {
		AVLNode y = x.right;
		AVLNode T2 = y.left;

		y.left = x;
		x.right = T2;

		x.height = Math.max(height(x.left), height(x.right)) + 1;
		y.height = Math.max(height(y.left), height(y.right)) + 1;

		return y;
	}

	// Insert a movie into the AVL tree
	public void insert(Movie movie) {
		if (search(movie.getTitle()) == null) { // Only increment if the movie is not already in the tree
			root = insertRec(root, movie);
			count++;
		}
	}

	private AVLNode insertRec(AVLNode node, Movie movie) {
		if (node == null)
			return new AVLNode(movie);

		int comparison = movie.getTitle().compareTo(node.movie.getTitle());

		if (comparison < 0)
			node.left = insertRec(node.left, movie);
		else if (comparison > 0)
			node.right = insertRec(node.right, movie);
		else
			return node; // Duplicate titles are not allowed

		node.height = Math.max(height(node.left), height(node.right)) + 1;

		int balance = getBalance(node);

		// Left Left Case
		if (balance > 1 && movie.getTitle().compareTo(node.left.movie.getTitle()) < 0)
			return rightRotate(node);

		// Right Right Case
		if (balance < -1 && movie.getTitle().compareTo(node.right.movie.getTitle()) > 0)
			return leftRotate(node);

		// Left Right Case
		if (balance > 1 && movie.getTitle().compareTo(node.left.movie.getTitle()) > 0) {
			node.left = leftRotate(node.left);
			return rightRotate(node);
		}

		// Right Left Case
		if (balance < -1 && movie.getTitle().compareTo(node.right.movie.getTitle()) < 0) {
			node.right = rightRotate(node.right);
			return leftRotate(node);
		}

		return node;
	}

	public Movie search(String title) {
		return search(root, title);
	}

	private Movie search(AVLNode node, String title) {
		if (node == null) {
			return null;
		}
		if (title.equals(node.movie.getTitle())) {
			return node.movie;
		} else if (title.compareTo(node.movie.getTitle()) < 0) {
			return search(node.left, title);
		} else {
			return search(node.right, title);
		}
	}

	public Movie leastRanked() {
		if (root == null) {
			return null;
		}
		AVLNode current = root;
		while (current.left != null) {
			current = current.left;
		}
		return current.movie;

	}

	public Movie maxRanked() {
		if (root == null) {
			return null;
		}
		AVLNode current = root;
		while (current.right != null) {
			current = current.right;
		}
		return current.movie;
	}

	private AVLNode min(AVLNode node) {
		while (node.left != null) {
			node = node.left;
		}
		return node;
	}

	public AVLNode max() {
		return max(root);
	}

	private AVLNode max(AVLNode node) {
		while (node.right != null) {
			node = node.right;
		}
		return node;
	}

	// Get the next in-order node
	public AVLNode next(AVLNode node) {
		if (node.right != null) {
			return min(node.right);
		}

		AVLNode successor = null;
		AVLNode current = root;
		while (current != null) {
			int comparison = node.movie.getTitle().compareTo(current.movie.getTitle());
			if (comparison < 0) {
				successor = current;
				current = current.left;
			} else if (comparison > 0) {
				current = current.right;
			} else {
				break;
			}
		}
		return successor;
	}

	// Get the previous in-order node
	public AVLNode prev(AVLNode node) {
		if (node.left != null) {
			return max(node.left);
		}

		AVLNode predecessor = null;
		AVLNode current = root;
		while (current != null) {
			int comparison = node.movie.getTitle().compareTo(current.movie.getTitle());
			if (comparison > 0) {
				predecessor = current;
				current = current.right;
			} else if (comparison < 0) {
				current = current.left;
			} else {
				break;
			}
		}
		return predecessor;
	}

	public void saveToFile(File importedFile) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Save Movies");
		alert.setHeaderText("Choose an option");
		alert.setContentText("Would you like to update the imported file or create a new file?");

		ButtonType updateButton = new ButtonType("Update File");
		ButtonType newFileButton = new ButtonType("Create New File");
		ButtonType cancelButton = new ButtonType("Cancel", ButtonType.CANCEL.getButtonData());

		alert.getButtonTypes().setAll(updateButton, newFileButton, cancelButton);

		alert.showAndWait().ifPresent(response -> {
			if (response == updateButton) {
				saveMoviesToFile(importedFile);
			} else if (response == newFileButton) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Save As New File");
				File file = fileChooser.showSaveDialog(null);
				if (file != null) {
					saveMoviesToFile(file);
				}
			}
		});
	}

	private void saveMoviesToFile(File file) {
		try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
			saveMoviesRec(root, writer);
		} catch (IOException e) {
			System.err.println("Error saving to file: " + e.getMessage());
		}
	}

	private void saveMoviesRec(AVLNode node, PrintWriter writer) {
		if (node != null) {
			saveMoviesRec(node.left, writer);
			writer.println("Title: " + node.movie.getTitle());
			writer.println("Description: " + node.movie.getDescription());
			writer.println("Release Year: " + node.movie.getReleaseYear());
			writer.println("Rating: " + node.movie.getRating());
			writer.println();
			saveMoviesRec(node.right, writer);
		}
	}

	public int getCount() {
		return count;
	}

	public Movie[] getAllMovies() {
		if (root == null) {
			return new Movie[0]; // Return an empty array if the tree is empty
		}

		Movie[] movies = new Movie[getCount()];
		int[] index = { 0 };
		fillMoviesArray(root, movies, index);
		return movies;
	}

	private void fillMoviesArray(AVLNode node, Movie[] movies, int[] index) {
		if (node != null) {
			fillMoviesArray(node.left, movies, index); // Traverse left subtree
			movies[index[0]++] = node.movie; // Add current movie
			fillMoviesArray(node.right, movies, index); // Traverse right subtree
		}
	}

	public Movie[] getAllMoviesInReverse() {
		Movie[] movies = new Movie[getCount()]; // Ensure correct array size
		int[] index = { 0 }; // Index tracker for array insertion
		fillMoviesArrayInReverse(root, movies, index);
		return movies;
	}

	private void fillMoviesArrayInReverse(AVLNode node, Movie[] movies, int[] index) {
		if (node != null) {
			fillMoviesArrayInReverse(node.right, movies, index); // Traverse right subtree
			movies[index[0]++] = node.movie; // Add current movie
			fillMoviesArrayInReverse(node.left, movies, index); // Traverse left subtree
		}
	}

}

class AVLNode {
	Movie movie;
	AVLNode left, right;
	int height;

	public AVLNode(Movie movie) {
		this.movie = movie;
		this.height = 1;
	}

	public Movie getMovie() {
		return movie;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}

	public AVLNode getLeft() {
		return left;
	}

	public void setLeft(AVLNode left) {
		this.left = left;
	}

	public AVLNode getRight() {
		return right;
	}

	public void setRight(AVLNode right) {
		this.right = right;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
