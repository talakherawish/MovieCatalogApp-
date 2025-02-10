package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;

public class Main extends Application {
	static ObservableList<Movie> movieList = FXCollections.observableArrayList();
	static ObservableList<Movie> searchResults = FXCollections.observableArrayList();
	static MovieCatalog movieCatalog = new MovieCatalog();
	static private TableView<Movie> tableView = new TableView<>();

	static int currentIndex = 0;
	static public Label index = new Label("Current Index: -");
	static public Label height = new Label("Tree Height: -");

	static VBox content;

	@Override
	public void start(Stage primaryStage) {
		StackPane root = new StackPane();

		// Background image for the first stage
		ImageView backgroundImage = new ImageView(new Image("file:C:/Users/tala/Downloads/movieHome.png"));
		backgroundImage.setFitWidth(1366);
		backgroundImage.setFitHeight(768);
		backgroundImage.setPreserveRatio(true);

		// Label for loading theater
		Label loadTheaterLabel = new Label("                        ");
		loadTheaterLabel.setStyle("-fx-font-size: 60px; -fx-text-fill: RED; -fx-padding: 10; -fx-font-weight: bold; ");
		loadTheaterLabel.setAlignment(Pos.CENTER);

		loadTheaterLabel.setOnMouseClicked(event -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Movie File");
			File selectedFile = fileChooser.showOpenDialog(primaryStage);
			MovieCatalog.loadMoviesFromFile(selectedFile);
			printAllMoviesWithTrees(); // Optional: Display the loaded movies and AVL structure
			openMovieCatalogPage(primaryStage);

		});

		root.getChildren().addAll(backgroundImage, loadTheaterLabel);
		StackPane.setAlignment(loadTheaterLabel, Pos.CENTER);

		Scene scene = new Scene(root, 1366, 768, Color.BLACK);
		primaryStage.setTitle("Movie Catalog");
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		primaryStage.show();
	}

	private void openMovieCatalogPage(Stage primaryStage) {
		ImageView backgroundImage = new ImageView(new Image("file:C:/Users/tala/Downloads/cinema.png"));
		backgroundImage.setFitWidth(1366);
		backgroundImage.setFitHeight(768);
		backgroundImage.setPreserveRatio(true);

		// TableView
		tableView.setPrefHeight(400);
		tableView.setMinWidth(830);
		tableView.setMaxWidth(830);

		TableColumn<Movie, String> titleColumn = new TableColumn<>("Title");
		titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
		titleColumn.setPrefWidth(150);

		TableColumn<Movie, String> descriptionColumn = new TableColumn<>("Description");
		descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
		descriptionColumn.setPrefWidth(400);

		TableColumn<Movie, Integer> yearColumn = new TableColumn<>("Release Year");
		yearColumn.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));
		yearColumn.setPrefWidth(120);

		TableColumn<Movie, Double> ratingColumn = new TableColumn<>("Rating");
		ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
		ratingColumn.setPrefWidth(100);

		tableView.getColumns().clear();
		tableView.getColumns().addAll(titleColumn, descriptionColumn, yearColumn, ratingColumn);
		tableView.setItems(movieList);

		titleColumn.setStyle("-fx-alignment: CENTER;");
		descriptionColumn.setStyle("-fx-alignment: CENTER;");
		yearColumn.setStyle("-fx-alignment: CENTER;");
		ratingColumn.setStyle("-fx-alignment: CENTER;");

		ComboBox<String> fileMenu = new ComboBox<>();
		fileMenu.getItems().addAll("Save", "Exit");
		fileMenu.setPromptText("FILES"); // Placeholder text for the ComboBox
		fileMenu.setPrefWidth(200);
		fileMenu.setStyle(
				"-fx-font-size: 25; -fx-font-weight: bold; -fx-background-color: #943429; -fx-text-fill: white; -fx-pref-width: 200px; -fx-pref-height: 70px;");

		ComboBox<String> movieMenu = new ComboBox<>();
		movieMenu.getItems().addAll("Add Movie", "Update Movie", "Delete Movie", "Print Sorted", "Top/Least Ranked");
		movieMenu.setPromptText("MOVIE");
		movieMenu.setPrefWidth(200);
		movieMenu.setStyle(
				"-fx-font-size: 20; -fx-font-weight: bold; -fx-background-color: #943429; -fx-text-fill: white; -fx-pref-width: 200px; -fx-pref-height: 70px;");
		fileMenu.setOnAction(event -> {
			String selectedAction = fileMenu.getValue();
			if ("Save".equals(selectedAction)) {
				saveMoviesToFile();
			} else if ("Exit".equals(selectedAction)) {
				System.exit(0);
			}
			fileMenu.getSelectionModel().clearSelection();
		});

		movieMenu.setOnAction(event -> {
			String selectedAction = movieMenu.getValue();

			if (selectedAction == null) {
				return;
			}

			switch (selectedAction) {
			case "Add Movie":
//				movieMenu.getSelectionModel().clearSelection();
				Platform.runLater(() -> {
					movieMenu.getSelectionModel().clearSelection();
					openAddMovieStage();
				});
				break;

			case "Update Movie":
//				movieMenu.getSelectionModel().clearSelection();
				Platform.runLater(() -> {
					movieMenu.getSelectionModel().clearSelection();
					openUpdateMovieStage();
				});
				break;

			case "Delete Movie":
//				movieMenu.getSelectionModel().clearSelection();
				Platform.runLater(() -> {
					movieMenu.getSelectionModel().clearSelection();
					removeMovie();

				});
				break;

			case "Print Sorted":
//				movieMenu.getSelectionModel().clearSelection();
				Platform.runLater(() -> {
					movieMenu.getSelectionModel().clearSelection();
				});
				openPrintSortedStage();
				break;

			case "Top/Least Ranked":
//				movieMenu.getSelectionModel().clearSelection();
				Platform.runLater(() -> {
					movieMenu.getSelectionModel().clearSelection();
				});
				printTopAndLeastRanked();
				break;

			default:
//				movieMenu.getSelectionModel().clearSelection();
				Platform.runLater(() -> {
					movieMenu.getSelectionModel().clearSelection();
				});
				break;
			}
//			movieMenu.getSelectionModel().clearSelection();
			Platform.runLater(() -> {
				movieMenu.getSelectionModel().clearSelection();
			});

		});

		Button next = new Button(">");
		next.setOnAction(event ->

		handleNext());
		next.setStyle(
				"-fx-font-size: 25; -fx-font-weight: bold; -fx-background-color: #943429; -fx-text-fill: white; -fx-pref-width: 50px; -fx-pref-height: 30px;");

		Button prev = new Button("<");
		prev.setOnAction(event -> handlePrevious());
		prev.setStyle(
				"-fx-font-size: 25; -fx-font-weight: bold; -fx-background-color: #943429; -fx-text-fill: white; -fx-pref-width: 50px; -fx-pref-height: 30px;");

		index.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: white; -fx-pref-height: 30px;");
		height.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: white; -fx-pref-height: 30px;");

		HBox navigationBar = new HBox(20, index, prev, next, height);
		navigationBar.setAlignment(Pos.CENTER);
		navigationBar.setPrefHeight(80);
		navigationBar.setPrefWidth(1000);

		// Button bar at the top
		HBox topBar = new HBox(1000, fileMenu, movieMenu);
		topBar.setPrefHeight(100);
		Insets x = new Insets(15, 25, 0, 20);
		topBar.setPadding(x);
		topBar.setStyle(" -fx-alignment: center-left; -fx-background-color: transparent;");

		TextField titleField = new TextField();
		titleField.setPromptText("Enter movie title...");
		titleField.setStyle("-fx-font-size: 14px; -fx-pref-width: 200px;");

		TextField yearField = new TextField();
		yearField.setPromptText("Enter release year...");
		yearField.setStyle("-fx-font-size: 14px; -fx-pref-width: 200px;");

		Button searchButton = new Button("⌕");
		searchButton.setStyle(
				"-fx-background-color: lightblue; -fx-text-fill: navy; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 5px 10px;");

		// Handle search button click
		searchButton.setOnAction(event -> {
			String title = titleField.getText().trim(); // Get the title from the input field
			Integer year = null; // Initialize year

			try {
				String yearInput = yearField.getText().trim();
				if (!yearInput.isEmpty()) {
					year = Integer.parseInt(yearInput);
				}
			} catch (NumberFormatException e) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Invalid Year");
				alert.setHeaderText("Year Format Error");
				alert.setContentText("Please enter a valid year as a number.");
				alert.showAndWait();
				return; // Exit the search operation
			}

			if (title.isEmpty() && year == null) {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Invalid");
				alert.setHeaderText("Search Results");
				alert.setContentText("at least one value should be provided");
				alert.showAndWait();
				tableView.setItems(FXCollections.observableArrayList(movieList));
				return;
			}

			ObservableList<Movie> results = movieCatalog.get(title, year);

			if (results.isEmpty()) {
				tableView.setItems(FXCollections.observableArrayList(movieList));
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("No Results");
				alert.setHeaderText("Search Results");
				alert.setContentText("No movies found with the attributes you provided.");
				alert.showAndWait();
			} else {
				tableView.setItems(FXCollections.observableArrayList(results));
				results.clear();
			}

		});

		// Create HBox and add elements
		HBox search = new HBox(10, titleField, yearField, searchButton);
		search.setAlignment(Pos.CENTER);

		// VBox for the content
		content = new VBox(10);
		content.getChildren().addAll(search, tableView, navigationBar);
		content.setAlignment(Pos.CENTER);

		// BorderPane layout
		BorderPane layout = new BorderPane();
		layout.setTop(topBar);
		layout.setCenter(content);

		// StackPane for background and layout
		StackPane root = new StackPane(backgroundImage, layout);

		Scene scene = new Scene(root, 1366, 768);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Movie Catalog - Table View");
		primaryStage.show();

	}

	private void handleNext() {
		do {
			currentIndex++;
			if (currentIndex >= movieCatalog.hashTable.length) {
				currentIndex = 0; // Wrap around to the start
			}

			AVLTree currentTree = movieCatalog.hashTable[currentIndex];
			if (currentTree != null && currentTree.getCount() > 0) {
				updateTableView(currentTree.getAllMovies()); // Display all movies in the current tree
				index.setText("Current Index: " + currentIndex + " / " + movieCatalog.hashTable.length);
				height.setText("Tree Height: " + movieCatalog.hashTable[currentIndex].getRoot().height);
				return;
			}
		} while (currentIndex != 0); // Prevent infinite loop
	}

	private void handlePrevious() {
		do {
			currentIndex--;
			if (currentIndex < 0) {
				currentIndex = movieCatalog.hashTable.length - 1; // Wrap around to the end
			}

			AVLTree currentTree = movieCatalog.hashTable[currentIndex];
			if (currentTree != null && currentTree.getCount() > 0) {
				updateTableView(currentTree.getAllMovies()); // Display all movies in the current tree
				index.setText("Current Index: " + currentIndex + " / " + movieCatalog.hashTable.length);
				height.setText("Tree Height: " + movieCatalog.hashTable[currentIndex].getRoot().height);
				return;
			}
		} while (currentIndex != movieCatalog.hashTable.length - 1); // Prevent infinite loop

	}

	private void updateTableView(Movie[] movie) {
		ObservableList<Movie> movieData = FXCollections.observableArrayList(movie);
		tableView.setItems(movieData);
	}

	private void saveMoviesToFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Movie File");
		File file = fileChooser.showSaveDialog(null);

		if (file != null) {
			try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
				for (Movie movie : movieList) {
					writer.println("Title: " + movie.getTitle());
					writer.println("Description: " + movie.getDescription());
					writer.println("Release Year: " + movie.getReleaseYear());
					writer.println("Rating: " + movie.getRating());
					writer.println();
				}
			} catch (IOException e) {
				System.err.println("Error saving the file: " + e.getMessage());
			}
		}
	}

	private void removeMovie() {
		Movie selectedMovie = tableView.getSelectionModel().getSelectedItem();

		if (selectedMovie == null) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("No Selection");
			alert.setHeaderText("No Movie Selected");
			alert.setContentText("Please select a movie to delete.");
			alert.showAndWait();
			return;
		}

		Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
		confirmation.setTitle("Confirm Deletion");
		confirmation.setHeaderText("Delete Movie");
		confirmation.setContentText("Are you sure you want to delete the movie: \"" + selectedMovie.getTitle() + "\"?");
		confirmation.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				movieList.remove(selectedMovie);
				movieCatalog.erase(selectedMovie.getTitle());

				Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
				successAlert.setTitle("Movie Deleted");
				successAlert.setHeaderText(null);
				successAlert.setContentText(
						"The movie \"" + selectedMovie.getTitle() + "\" has been successfully deleted.");
				successAlert.showAndWait();
			}
		});
	}

	// Method to open the "Add Movie" stage
	private void openAddMovieStage() {
		Stage addMovieStage = new Stage();
		addMovieStage.setTitle("Add Movie");

		ImageView backgroundImage = new ImageView(new Image("file:C:/Users/tala/Downloads/add.png"));
		backgroundImage.setFitWidth(500);
		backgroundImage.setFitHeight(500);
		backgroundImage.setPreserveRatio(false);

		Label nameLabel = new Label("           ");
		TextField nameField = new TextField();
		nameField.setPromptText("Enter movie name");

		Label yearLabel = new Label("           ");
		DatePicker yearPicker = new DatePicker(LocalDate.now());
		yearPicker.setEditable(true);

		Label descriptionLabel = new Label("            ");
		TextArea descriptionArea = new TextArea();
		descriptionArea.setPromptText("Enter movie description");
		descriptionArea.setWrapText(true);

		Label ratingLabel = new Label("            ");
		Spinner<Double> ratingSpinner = new Spinner<>(0.0, 10.0, 5.0, 0.1);
		ratingSpinner.setEditable(true);

		Label ratingNote = new Label("Note: Ratings can only be between 0 and 10.");
		ratingNote.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

		// Submit button
		Button submitButton = new Button("  ADD  ");
		submitButton.setStyle("-fx-background-color: #943429; -fx-text-fill: white; -fx-font-weight: bold;");
		submitButton.setOnAction(event -> {
			try {
				String name = nameField.getText();
				LocalDate date = yearPicker.getValue();
				if (date == null) {
					throw new IllegalArgumentException("Please select a valid release year.");
				}
				int year = date.getYear();
				String description = descriptionArea.getText();
				double rating = ratingSpinner.getValue();

				if (rating < 0.0 || rating > 10.0) {
					throw new IllegalArgumentException("Rating must be between 0 and 10.");
				}

				if (!name.isEmpty() && !description.isEmpty()) {
					Movie newMovie = new Movie(name, description, year, rating);
					movieList.add(newMovie); // Add to the observable list
					movieCatalog.put(newMovie); // Add to the hash table
					addMovieStage.close();
				} else {
					Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.setTitle("Input Error");
					alert.setHeaderText("Invalid Input");
					alert.setContentText("Please fill out all fields.");
					alert.showAndWait();
				}
			} catch (IllegalArgumentException ex) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Invalid Input");
				alert.setHeaderText("Input Error");
				alert.setContentText(ex.getMessage());
				alert.showAndWait();
			}
		});

		VBox form = new VBox(10, nameLabel, nameField, yearLabel, yearPicker, descriptionLabel, descriptionArea,
				ratingLabel, ratingSpinner, ratingNote, submitButton);
		form.setAlignment(Pos.CENTER);
		form.setPadding(new Insets(20));
		form.setStyle("-fx-background-color: transparent;"); // Make form background transparent

		StackPane root = new StackPane(backgroundImage, form);

		Scene scene = new Scene(root, 500, 500);
		addMovieStage.setScene(scene);
		addMovieStage.show();

	}

	private void openUpdateMovieStage() {
		Movie selectedMovie = tableView.getSelectionModel().getSelectedItem();
		if (selectedMovie == null) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("No Selection");
			alert.setHeaderText("No Movie Selected");
			alert.setContentText("Please select a movie to update.");
			alert.showAndWait();
			return;
		}

		Stage updateMovieStage = new Stage();
		updateMovieStage.setTitle("Update Movie");

		ImageView backgroundImage = new ImageView(new Image("file:C:/Users/tala/Downloads/add.png"));
		backgroundImage.setFitWidth(500);
		backgroundImage.setFitHeight(500);
		backgroundImage.setPreserveRatio(false);

		Label updateName = new Label("           ");
		TextField nameField = new TextField(selectedMovie.getTitle());

		Label updateYear = new Label("           ");
		DatePicker yearPicker = new DatePicker(LocalDate.of(selectedMovie.getReleaseYear(), 1, 1));
		yearPicker.setEditable(true);

		Label updateDescription = new Label("           ");
		TextArea descriptionArea = new TextArea(selectedMovie.getDescription());

		Label updateRating = new Label("             ");
		Spinner<Double> ratingSpinner = new Spinner<>(0.0, 10.0, selectedMovie.getRating(), 0.1);
		ratingSpinner.setEditable(true);

		Label ratingNote = new Label("Note: Ratings can only be between 0 and 10.");
		ratingNote.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

		Button submitButton = new Button("Update Movie");
		submitButton.setStyle("-fx-background-color: #943429; -fx-text-fill: white; -fx-font-weight: bold;");
		submitButton.setOnAction(event -> {
			try {
				String name = nameField.getText();
				LocalDate date = yearPicker.getValue();
				if (date == null) {
					throw new IllegalArgumentException("Please select a valid release year.");
				}
				int year = date.getYear();
				String description = descriptionArea.getText();
				double rating = ratingSpinner.getValue();

				if (rating < 0.0 || rating > 10.0) {
					throw new IllegalArgumentException("Rating must be between 0 and 10.");
				}

				selectedMovie.setTitle(name);
				selectedMovie.setReleaseYear(year);
				selectedMovie.setDescription(description);
				selectedMovie.setRating(rating);

				tableView.refresh(); // Refresh the table view
				movieCatalog.put(selectedMovie); // Update the hash table
				updateMovieStage.close();

			} catch (IllegalArgumentException ex) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Invalid Input");
				alert.setHeaderText("Input Error");
				alert.setContentText(ex.getMessage());
				alert.showAndWait();
			}
		});

		VBox form = new VBox(10, updateName, nameField, updateYear, yearPicker, updateDescription, descriptionArea,
				updateRating, ratingSpinner, ratingNote, submitButton);
		form.setAlignment(Pos.CENTER);
		form.setPadding(new Insets(20));
		form.setStyle("-fx-background-color: transparent;");

		StackPane root = new StackPane(backgroundImage, form);
		Scene scene = new Scene(root, 500, 500);
		updateMovieStage.setScene(scene);
		updateMovieStage.show();
	}

	private void openPrintSortedStage() {
		Stage sortStage = new Stage();
		sortStage.setTitle("Print Sorted Movies");

		ComboBox<String> sortOptions = new ComboBox<>();
		sortOptions.getItems().addAll("Ascending", "Descending");
		sortOptions.setPromptText("Select Order");
		sortOptions.setPrefWidth(200);
		sortOptions.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");

		// Button to confirm the sorting order
		Button confirmButton = new Button("Sort");
		confirmButton.setStyle(
				"-fx-background-color: #943429; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px;");
		confirmButton.setOnAction(event -> {
			String selectedOption = sortOptions.getValue();
			if (selectedOption == null) {
				Alert alert = new Alert(Alert.AlertType.WARNING);
				alert.setTitle("No Selection");
				alert.setHeaderText("Sorting Order Not Selected");
				alert.setContentText("Please select either Ascending or Descending order.");
				alert.showAndWait();
				return;
			}

			// Perform sorting directly with number-aware logic
			int n = movieList.size();
			for (int i = 0; i < n - 1; i++) {
				for (int j = 0; j < n - i - 1; j++) {
					Movie m1 = movieList.get(j);
					Movie m2 = movieList.get(j + 1);

					String title1 = m1.getTitle().toLowerCase();
					String title2 = m2.getTitle().toLowerCase();

					boolean shouldSwap = (selectedOption.equals("Ascending") && compareTitles(title1, title2) > 0)
							|| (selectedOption.equals("Descending") && compareTitles(title1, title2) < 0);

					if (shouldSwap) {
						// Swap the movies
						movieList.set(j, m2);
						movieList.set(j + 1, m1);
					}
				}
			}

			// Explicitly clear the TableView selection and refresh it
			tableView.getSelectionModel().clearSelection();
			tableView.setItems(FXCollections.observableArrayList(movieList));
			tableView.refresh();

			// Clear menu selection and close the stage
			sortOptions.getSelectionModel().clearSelection();
			sortStage.close();
		});

		// VBox layout
		VBox layout = new VBox(10, sortOptions, confirmButton);
		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(20));

		Scene scene = new Scene(layout, 300, 200);
		sortStage.setScene(scene);
		sortStage.show();
	}

	// Enhanced comparison to handle numbers correctly
	private int compareTitles(String title1, String title2) {
		int len1 = title1.length();
		int len2 = title2.length();
		int i = 0, j = 0;

		while (i < len1 && j < len2) {
			char ch1 = title1.charAt(i);
			char ch2 = title2.charAt(j);

			if (Character.isDigit(ch1) && Character.isDigit(ch2)) {
				// Parse full numbers from both strings
				StringBuilder num1 = new StringBuilder();
				StringBuilder num2 = new StringBuilder();

				while (i < len1 && Character.isDigit(title1.charAt(i))) {
					num1.append(title1.charAt(i++));
				}
				while (j < len2 && Character.isDigit(title2.charAt(j))) {
					num2.append(title2.charAt(j++));
				}

				// Compare the numeric values
				int number1 = Integer.parseInt(num1.toString());
				int number2 = Integer.parseInt(num2.toString());
				if (number1 != number2) {
					return number1 - number2;
				}
			} else {
				// Compare characters
				if (ch1 != ch2) {
					return ch1 - ch2;
				}
				i++;
				j++;
			}
		}
		return len1 - len2;
	}

	public void printTopAndLeastRanked() {
		Stage rankStage = new Stage();
		rankStage.setTitle("Top and Least Ranked Movies");

		TableView<String[]> tableView = new TableView<>();
		tableView.setEditable(false);

		TableColumn<String[], String> indexColumn = new TableColumn<>("Hash Table Index");
		indexColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0]));
		indexColumn.setPrefWidth(150);

		TableColumn<String[], String> topColumn = new TableColumn<>("Least Ranked Movie");
		topColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[1]));

		TableColumn<String[], String> leastColumn = new TableColumn<>("Top Ranked Movie");
		leastColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[2]));

		tableView.getColumns().addAll(indexColumn, topColumn, leastColumn);
		tableView.setPrefWidth(750);
		topColumn.setPrefWidth(300);
		leastColumn.setPrefWidth(300);

		ObservableList<String[]> data = FXCollections.observableArrayList();
		for (int i = 0; i < movieCatalog.hashTable.length; i++) {
			AVLTree tree = movieCatalog.hashTable[i];
			if (tree != null && tree.getRoot() != null) {
				Movie minRanked = tree.leastRanked();
				Movie maxRanked = tree.maxRanked();

				if (minRanked != null && maxRanked != null) {
					data.add(new String[] { "Index " + i,
							maxRanked.getTitle() + "                                               "
									+ String.format("%.2f", maxRanked.getRating()) + " ★",
							minRanked.getTitle() + "                                               "
									+ String.format("%.2f", minRanked.getRating()) + " ★" });
				}
			}
		}
		tableView.setItems(data);

		Button closeButton = new Button("Close");
		closeButton.setStyle(
				"-fx-background-color: #943429; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px;");
		closeButton.setOnAction(event -> rankStage.close());

		VBox layout = new VBox(10, tableView, closeButton);
		layout.setPadding(new Insets(20));
		layout.setAlignment(Pos.CENTER);

		Scene scene = new Scene(layout, 800, 600);
		rankStage.setScene(scene);
		rankStage.show();
	}

	
	public void printAllMoviesWithTrees() {
		System.out.println("Printing all keys and their corresponding AVL trees:");
		for (int i = 0; i < movieCatalog.hashTable.length; i++) {
			AVLTree tree = movieCatalog.hashTable[i];
			if (tree != null) {
				System.out.println("Hash Table Index " + i + ":");
				Movie[] movies = tree.getAllMovies();
				if (movies.length == 0) {
					System.out.println("  [Empty AVL Tree]");
				} else {
					System.out.println("  Movies in AVL Tree:");
					for (Movie movie : movies) {
						System.out.println("    Title: " + movie.getTitle() + ", Year: " + movie.getReleaseYear()
								+ ", Rating: " + movie.getRating());
					}
				}
			}
		}
		System.out.println("Finished printing all keys and AVL trees.");
	}

	public static void main(String[] args) {
		launch(args);
	}
}
