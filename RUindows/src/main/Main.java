package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;

public class Main extends Application {

	// Use TilePane with vertical orientation for desktop icons
	private TilePane iconBox = new TilePane(Orientation.VERTICAL);
	private int textFileCount = 0;
	private Set<String> savedFileNames = new HashSet<>();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = LoginPage(primaryStage);
		scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Login App");
		primaryStage.show();
	}

	public Scene LoginPage(Stage stage) {
		Image img = new Image(getClass().getResourceAsStream("/assets/nature.jpg"));
		ImageView imgBackground = new ImageView(img);
		imgBackground.setPreserveRatio(false);
		imgBackground.setSmooth(true);

		// Stretch the image to fit the window
		StackPane sp = new StackPane();
		imgBackground.fitWidthProperty().bind(sp.widthProperty());
		imgBackground.fitHeightProperty().bind(sp.heightProperty());

		ImageView profilePicture = new ImageView(
				new Image(getClass().getResourceAsStream("/assets/default_profile_pic.png")));
		profilePicture.setFitWidth(200);
		profilePicture.setFitHeight(200);
		profilePicture.setPreserveRatio(true);

		Label errorMsg = new Label();
		errorMsg.getStyleClass().add("error-label");

		Label welcomeLabel = new Label("Welcome RU24-2!");
		welcomeLabel.getStyleClass().add("welcome-label");

		PasswordField pwField = new PasswordField();
		pwField.setPromptText("Enter password");
		pwField.setMaxWidth(400);

		Button loginBtn = new Button("Login");

		loginBtn.setOnAction(e -> {
			if (pwField.getText().equals("admin")) {
				stage.setScene(HomePage(stage));
			} else {
				errorMsg.setText("Wrong Password!");
			}
		});

		HBox hb = new HBox(15);
		hb.setAlignment(Pos.CENTER);
		hb.getChildren().addAll(pwField, loginBtn);

		VBox vb = new VBox(15);
		vb.setAlignment(Pos.CENTER);
		vb.getChildren().addAll(profilePicture, welcomeLabel, hb, errorMsg);

		sp.getChildren().addAll(imgBackground, vb);
		return new Scene(sp, 1280, 720);
	}

	public Scene HomePage(Stage homeStage) {
		iconBox.getChildren().clear();
		savedFileNames.clear();

		// Load background image
		Image homeImg = new Image(getClass().getResourceAsStream("/assets/homepage.jpg"));
		ImageView homeBackground = new ImageView(homeImg);
		homeBackground.setPreserveRatio(false);
		homeBackground.setSmooth(true);

		// Background Image size same with StackPane size
		StackPane backgroundPane = new StackPane();
		backgroundPane.getChildren().add(homeBackground);
		homeBackground.fitWidthProperty().bind(backgroundPane.widthProperty());
		homeBackground.fitHeightProperty().bind(backgroundPane.heightProperty());

		// Configure TilePane for desktop icons
		iconBox.setPadding(new Insets(20, 0, 0, 20));
		iconBox.setAlignment(Pos.TOP_LEFT);
		iconBox.setPrefColumns(1); // Start with 1 column
		iconBox.setMaxWidth(1200); // Set max width to allow multiple columns
		iconBox.setMaxHeight(600); // Set max height to prevent icons from going below taskbar
		iconBox.setHgap(20); // Horizontal gap between columns
		iconBox.setVgap(20); // Vertical gap between icons in a column
		iconBox.getStyleClass().add("desktop-icons");

		// VBox containing all icons + labels
		VBox trashBox = createDesktopIcon("/assets/trash-icon.png", "Trash bin", null);
		VBox notepadBox = createDesktopIcon("/assets/notepad-icon.png", "Notepad", this::notepadWindow);
		VBox chromeBox = createDesktopIcon("/assets/chrome.png", "Chrome", this::chromeWindow);
		iconBox.getChildren().addAll(trashBox, notepadBox, chromeBox);

		// Icon buat Taskbar
		ImageView windowsLogo = new ImageView(new Image(getClass().getResourceAsStream("/assets/window-icon.png")));
		windowsLogo.setFitWidth(40);
		windowsLogo.setFitHeight(40);

		ImageView notepadLogo = new ImageView(new Image(getClass().getResourceAsStream("/assets/notepad-icon.png")));
		notepadLogo.setFitWidth(40);
		notepadLogo.setFitHeight(40);

		ImageView logoutLogo = new ImageView(new Image(getClass().getResourceAsStream("/assets/shutdown4-icon.png")));
		logoutLogo.setFitWidth(40);
		logoutLogo.setFitHeight(40);

		ImageView shutdownLogo = new ImageView(new Image(getClass().getResourceAsStream("/assets/logout3-icon.png")));
		shutdownLogo.setFitWidth(40);
		shutdownLogo.setFitHeight(40);

		// Remove all inline styling - use only CSS classes
		MenuItem logoutMenu = new MenuItem("Logout");
		logoutMenu.setGraphic(logoutLogo);
		logoutMenu.setOnAction(e -> homeStage.setScene(LoginPage(homeStage)));
		logoutMenu.getStyleClass().add("taskbar-menu-item");

		MenuItem shutdownMenu = new MenuItem("Shutdown");
		shutdownMenu.setGraphic(shutdownLogo);
		shutdownMenu.setOnAction(e -> homeStage.close());
		shutdownMenu.getStyleClass().add("taskbar-menu-item");

		Menu startMenu = new Menu();
		startMenu.getItems().addAll(logoutMenu, shutdownMenu);
		startMenu.setGraphic(windowsLogo);
		startMenu.getStyleClass().add("taskbar-menu");

		MenuItem openNotepadItem = new MenuItem("Open Notepad");
		openNotepadItem.setOnAction(e -> notepadWindow());
		openNotepadItem.getStyleClass().add("taskbar-menu-item");

		Menu notepadMenu = new Menu("");
		notepadMenu.setGraphic(notepadLogo);
		notepadMenu.getItems().add(openNotepadItem);
		notepadMenu.getStyleClass().add("taskbar-menu");

		// Taskbar - remove inline styling
		MenuBar taskBar = new MenuBar(startMenu, notepadMenu);
		taskBar.getStyleClass().add("taskbar");

		// Force taskbar to be black with inline styling as backup
		taskBar.setStyle("-fx-background-color: black; -fx-border-color: black;"); // paksa pake inline karena external
																					// css ga kerja T.T
		taskBar.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

		// Foreground layout over background (BorderPane)
		BorderPane foregroundPane = new BorderPane();
		foregroundPane.setLeft(iconBox);
		foregroundPane.setBottom(taskBar);

		// Gabungin Background sama Foregroundnya
		StackPane stackPane = new StackPane();
		stackPane.getChildren().addAll(backgroundPane, foregroundPane);

		return new Scene(stackPane, 1280, 720);
	}

	private VBox createDesktopIcon(String iconPath, String labelText, Runnable onClick) {
		ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
		icon.setFitHeight(64);
		icon.setFitWidth(64);
		icon.setPreserveRatio(true);

		Label label = new Label(labelText);
		label.getStyleClass().add("desktop-icon-label");

		VBox box = new VBox(5);
		box.setAlignment(Pos.CENTER);
		box.getChildren().addAll(icon, label);
		box.setPrefWidth(100); // Set preferred width for consistent icon spacing
		box.setPrefHeight(100); // Set preferred height for consistent icon spacing
		box.getStyleClass().add("desktop-icon");

		box.setOnMouseClicked(e -> {
			if (onClick != null)
				onClick.run();
			else
				openPhotoEditor(labelText, iconPath); // Open Photo Editor for image files
		});
		return box;
	}

	private void notepadWindow() {
		Stage notepadStage = new Stage();
		notepadStage.setTitle("Notepad");

		TextArea textArea = new TextArea();
		textArea.setWrapText(true);

		// Track current file info for new documents
		final String[] currentFilePath = { null };
		final String[] currentFileName = { null };

		// Save menu item
		MenuItem saveMenu = new MenuItem("Save");
		saveMenu.setOnAction(e -> {
			if (currentFilePath[0] == null) {
				// First time saving - act like Save As
				saveAsAction(textArea, notepadStage, currentFilePath, currentFileName);
			} else {
				// Save to existing file
				try {
					FileWriter writer = new FileWriter(currentFilePath[0]);
					writer.write(textArea.getText());
					writer.close();
					showAlert(Alert.AlertType.INFORMATION, "File saved successfully!");
				} catch (IOException ex) {
					showAlert(Alert.AlertType.ERROR, "Failed to save file: " + ex.getMessage());
				}
			}
		});

		// Save As menu item
		MenuItem saveAsMenu = new MenuItem("Save As");
		saveAsMenu.setOnAction(e -> saveAsAction(textArea, notepadStage, currentFilePath, currentFileName));

		// New menu item
		MenuItem newMenu = new MenuItem("New");
		newMenu.setOnAction(e -> notepadWindow());

		Menu fileMenu = new Menu("File");
		fileMenu.getItems().addAll(newMenu, saveMenu, saveAsMenu);

		MenuBar menuBar = new MenuBar(fileMenu);
		VBox layout = new VBox(menuBar, textArea);
		VBox.setVgrow(textArea, Priority.ALWAYS);

		Scene notepadScene = new Scene(layout, 900, 600);
		notepadScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

		// Add keyboard shortcuts
		notepadScene.setOnKeyPressed(event -> {
			if (event.isControlDown() && event.getCode() == KeyCode.S) {
				saveMenu.fire();
			} else if (event.isControlDown() && event.isShiftDown() && event.getCode() == KeyCode.S) {
				saveAsMenu.fire();
			} else if (event.isControlDown() && event.getCode() == KeyCode.N) {
				newMenu.fire();
			}
		});

		notepadStage.setScene(notepadScene);
		notepadStage.show();
	}

	// Helper method for Save As functionality
	private void saveAsAction(TextArea textArea, Stage stage, String[] currentFilePath, String[] currentFileName) {
		String defaultName = currentFileName[0] != null ? currentFileName[0]
				: (textFileCount == 0) ? "text.txt" : "text" + textFileCount + ".txt";

		TextInputDialog dialog = new TextInputDialog(defaultName);
		dialog.setTitle("Save File");
		dialog.setHeaderText("Save File");
		dialog.setContentText("File name:");

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(filename -> {
			if (!filename.toLowerCase().endsWith(".txt"))
				filename += ".txt";
			String nameOnly = filename.replace(".txt", "");

			if (!nameOnly.matches("[a-zA-Z0-9]+")) {
				showAlert(Alert.AlertType.ERROR, "Invalid filename. Only alphanumeric characters are allowed.");
				return;
			}
			if (savedFileNames.contains(filename) && !filename.equals(currentFileName[0])) {
				showAlert(Alert.AlertType.ERROR, "Filename already exists.");
				return;
			}

			try {
				File tempDir = new File(System.getProperty("java.io.tmpdir"));
				File file = new File(tempDir, filename);
				FileWriter writer = new FileWriter(file);
				writer.write(textArea.getText());
				writer.close();
				file.deleteOnExit();

				// Update current file info
				currentFilePath[0] = file.getAbsolutePath();
				currentFileName[0] = filename;
				stage.setTitle("Notepad - " + currentFilePath[0]);

				// Add to saved files if it's a new name
				if (!savedFileNames.contains(filename)) {
					textFileCount++;
					savedFileNames.add(filename);
					addNotepadShortcut(filename, file.getAbsolutePath());

					// Check if we need to add a new column
					if (iconBox.getChildren().size() > 10) {
						int currentColumns = iconBox.getPrefColumns();
						iconBox.setPrefColumns(currentColumns + 1);
					}
				}

				showAlert(Alert.AlertType.INFORMATION, "File saved as " + filename);
			} catch (IOException ex) {
				showAlert(Alert.AlertType.ERROR, "Failed to save file: " + ex.getMessage());
			}
		});
	}

	private void addNotepadShortcut(String fileName, String filePath) {
		VBox shortcut = createDesktopIcon("/assets/notepad-icon.png", fileName, () -> openExistingNotepad(filePath));
		iconBox.getChildren().add(shortcut);
	}

	private void openExistingNotepad(String path) {
		Stage stage = new Stage();
		stage.setTitle("Notepad - " + path);

		TextArea area = new TextArea();
		area.setWrapText(true);

		// Track the current file path for saving
		final String[] currentFilePath = { path };
		final String[] currentFileName = { new File(path).getName() };

		try {
			area.setText(Files.readString(Paths.get(path)));
		} catch (IOException e) {
			showAlert(Alert.AlertType.ERROR, "Cannot open file.");
			return;
		}

		// Save menu item (Ctrl+S)
		MenuItem saveMenu = new MenuItem("Save");
		saveMenu.setOnAction(e -> {
			try {
				FileWriter writer = new FileWriter(currentFilePath[0]);
				writer.write(area.getText());
				writer.close();
				showAlert(Alert.AlertType.INFORMATION, "File saved successfully!");
			} catch (IOException ex) {
				showAlert(Alert.AlertType.ERROR, "Failed to save file: " + ex.getMessage());
			}
		});

		// Save As menu item
		MenuItem saveAsMenu = new MenuItem("Save As");
		saveAsMenu.setOnAction(e -> {
			String defaultName = currentFileName[0];
			TextInputDialog dialog = new TextInputDialog(defaultName);
			dialog.setTitle("Save As");
			dialog.setHeaderText("Save File As");
			dialog.setContentText("File name: ");

			Optional<String> result = dialog.showAndWait();
			result.ifPresent(filename -> {
				if (!filename.toLowerCase().endsWith(".txt"))
					filename += ".txt";
				String nameOnly = filename.replace(".txt", "");

				if (!nameOnly.matches("[a-zA-Z0-9]+")) {
					showAlert(Alert.AlertType.ERROR, "Invalid filename. Only alphanumeric characters are allowed.");
					return;
				}
				if (savedFileNames.contains(filename) && !filename.equals(currentFileName[0])) {
					showAlert(Alert.AlertType.ERROR, "Filename already exists.");
					return;
				}

				try {
					File tempDir = new File(System.getProperty("java.io.tmpdir"));
					File newFile = new File(tempDir, filename);
					FileWriter writer = new FileWriter(newFile);
					writer.write(area.getText());
					writer.close();
					newFile.deleteOnExit();

					// Update current file info
					currentFilePath[0] = newFile.getAbsolutePath();
					currentFileName[0] = filename;
					stage.setTitle("Notepad - " + currentFilePath[0]);

					// Add to saved files if it's a new name
					if (!savedFileNames.contains(filename)) {
						savedFileNames.add(filename);
						addNotepadShortcut(filename, newFile.getAbsolutePath());

						// Check if we need to add a new column
						if (iconBox.getChildren().size() > 10) {
							int currentColumns = iconBox.getPrefColumns();
							iconBox.setPrefColumns(currentColumns + 1);
						}
					}

					showAlert(Alert.AlertType.INFORMATION, "File saved as " + filename);
				} catch (IOException ex) {
					showAlert(Alert.AlertType.ERROR, "Failed to save file: " + ex.getMessage());
				}
			});
		});

		// New menu item
		MenuItem newMenu = new MenuItem("New");
		newMenu.setOnAction(e -> notepadWindow());

		Menu fileMenu = new Menu("File");
		fileMenu.getItems().addAll(newMenu, saveMenu, saveAsMenu);

		MenuBar menuBar = new MenuBar(fileMenu);
		VBox layout = new VBox(menuBar, area);
		VBox.setVgrow(area, Priority.ALWAYS);

		Scene scene = new Scene(layout, 800, 600);
		scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
		stage.setScene(scene);

		// Add keyboard shortcuts
		scene.setOnKeyPressed(event -> {
			if (event.isControlDown() && event.getCode() == KeyCode.S) {
				saveMenu.fire(); // Trigger save action
			} else if (event.isControlDown() && event.isShiftDown() && event.getCode() == KeyCode.S) {
				saveAsMenu.fire(); // Trigger save as action
			} else if (event.isControlDown() && event.getCode() == KeyCode.N) {
				newMenu.fire(); // Trigger new action
			}
		});

		stage.show();
	}

	private void showAlert(Alert.AlertType type, String message) {
		Alert alert = new Alert(type);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void chromeWindow() {
		Stage chromeStage = new Stage();
		chromeStage.setTitle("ChRUme");

		// --- Search Bar and Button (centered and styled) ---
		TextField searchBar = new TextField();
		searchBar.setPromptText("Enter domain...");
		searchBar.setPrefWidth(400);
		searchBar.getStyleClass().add("searchbar");

		Button searchBtn = new Button("Search");

		HBox searchBox = new HBox(10, searchBar, searchBtn);
		searchBox.setAlignment(Pos.CENTER);
		searchBox.setPadding(new Insets(30, 0, 10, 0)); // Top padding to mimic screenshot

		// --- StackPane for dynamic content ---
		StackPane contentPane = new StackPane();
		contentPane.setPadding(new Insets(10));

		// --- Empty Website Content ---
		Label emptyContent = new Label("");
		emptyContent.getStyleClass().add("empty-content");

		// --- Domain Not Found Content (Styled like screenshot) ---
		VBox domainNotFoundContent = new VBox(10);
		domainNotFoundContent.setAlignment(Pos.CENTER);

		Label notFoundTitle = new Label("This site can't be reached");
		notFoundTitle.getStyleClass().add("notfound-title");

		Label notFoundDesc = new Label(); // Will set text dynamically
		notFoundDesc.getStyleClass().add("notfound-desc");

		domainNotFoundContent.getChildren().addAll(notFoundTitle, notFoundDesc);

		// --- RUtube.net Content (Styled) ---
		VBox ruTubeContent = new VBox(20);
		ruTubeContent.getStyleClass().add("rutube-content");
		ruTubeContent.setPadding(new Insets(20));
		ruTubeContent.setAlignment(Pos.TOP_CENTER);

		// Header (logo + label)
		HBox header = new HBox(10);
		header.setAlignment(Pos.CENTER_LEFT);

		ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/assets/youtube-logo.png")));
		logo.setFitHeight(30);
		logo.setPreserveRatio(true);

		Label logoLabel = new Label("RUtube");
		logoLabel.getStyleClass().add("logo-label");

		header.getChildren().addAll(logo, logoLabel);
		header.setPadding(new Insets(10, 0, 10, 0));

		// Video player
		String videoPath = getClass().getResource("/assets/DiamondJack.mp4").toExternalForm();
		Media media = new Media(videoPath);
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		MediaView mediaView = new MediaView(mediaPlayer);
		mediaView.setFitWidth(640);
		mediaView.setFitHeight(360);
		mediaView.setPreserveRatio(true);

		// Controls
		HBox controls = new HBox(10);
		controls.setAlignment(Pos.CENTER);
		Button playBtn = new Button("Play");
		Button pauseBtn = new Button("Pause");
		controls.getChildren().addAll(playBtn, pauseBtn);

		// Button actions
		playBtn.setOnAction(e -> mediaPlayer.play());
		pauseBtn.setOnAction(e -> mediaPlayer.pause());

		// Combine all elements
		ruTubeContent.getChildren().addAll(header, mediaView, controls);

		// Helper to stop media player
		Runnable stopVideo = () -> {
			mediaPlayer.pause();
			mediaPlayer.seek(mediaPlayer.getStartTime());
		};

		// --- RUtify.net Content (Styled like Spotify) ---
		VBox ruTifyContent = new VBox();
		ruTifyContent.getStyleClass().add("rutify-content");
		ruTifyContent.setMinHeight(800);
		ruTifyContent.setPadding(new Insets(20));
		ruTifyContent.setAlignment(Pos.TOP_LEFT);

		// Header: logo + title
		HBox rutifyHeader = new HBox(10);
		ImageView rutifyLogo = new ImageView(new Image(getClass().getResourceAsStream("/assets/spotify-logo.png")));
		rutifyLogo.setFitHeight(30);
		rutifyLogo.setPreserveRatio(true);
		Label rutifyTitle = new Label("RUtify");
		rutifyTitle.getStyleClass().add("rutify-title");
		rutifyHeader.getChildren().addAll(rutifyLogo, rutifyTitle);
		rutifyHeader.setAlignment(Pos.CENTER_LEFT);
		rutifyHeader.setPadding(new Insets(10));

		// Music player (centered vertically in a wrapper VBox)
		Media rutifyMedia = new Media(getClass().getResource("/assets/PromQueen.mp3").toExternalForm());
		MediaPlayer rutifyPlayer = new MediaPlayer(rutifyMedia);

		Slider rutifySlider = new Slider();
		rutifySlider.setDisable(true);
		rutifyPlayer.currentTimeProperty().addListener((obs, oldVal, newVal) -> {
			rutifySlider.setValue(newVal.toSeconds());
		});
		rutifyPlayer.setOnReady(() -> {
			rutifySlider.setMax(rutifyMedia.getDuration().toSeconds());
		});

		Button rutifyPlay = new Button("Play");
		Button rutifyPause = new Button("Pause");
		rutifyPlay.setOnAction(e -> rutifyPlayer.play());
		rutifyPause.setOnAction(e -> rutifyPlayer.pause());

		HBox rutifyControls = new HBox(10, rutifyPlay, rutifyPause);
		rutifyControls.setAlignment(Pos.CENTER);

		VBox rutifyPlayerBox = new VBox(10, rutifySlider, rutifyControls);
		rutifyPlayerBox.setAlignment(Pos.CENTER);
		rutifyPlayerBox.setPadding(new Insets(150, 0, 0, 0)); // spacing below the header

		// Final layout
		ruTifyContent.getChildren().addAll(rutifyHeader, rutifyPlayerBox);

		// Helper to stop media player
		Runnable stopAudio = () -> {
			rutifyPlayer.pause();
			rutifyPlayer.seek(rutifyPlayer.getStartTime());
		};

		// --- stockimages.net Content ---
		ScrollPane stockScrollPane = new ScrollPane();
		stockScrollPane.setFitToWidth(true);
		stockScrollPane.setPannable(true); // Enable mouse drag scrolling
		stockScrollPane.getStyleClass().add("stock-scroll");

		VBox.setVgrow(stockScrollPane, Priority.ALWAYS);
		VBox stockImagesBox = new VBox(20);
		stockImagesBox.getStyleClass().add("stock-images");
		stockImagesBox.setPadding(new Insets(20));
		stockImagesBox.setAlignment(Pos.TOP_CENTER);

		// Ensure content expands width-wise with ScrollPane
		stockImagesBox.setFillWidth(true);
		stockScrollPane.setContent(stockImagesBox);

		// List of actual image filenames
		String[] imageFiles = {
				"cat-image1.jpg",
				"cat-image2.jpg",
				"cat-image3.jpeg",
				"cat-image4.jpeg"
		};

		for (String imageName : imageFiles) {
			InputStream imgStream = getClass().getResourceAsStream("/assets/" + imageName);
			if (imgStream == null)
				continue;

			ImageView catImage = new ImageView(new Image(imgStream));
			catImage.setFitWidth(400);
			catImage.setPreserveRatio(true);

			Button downloadBtn = new Button("Download");
			downloadBtn.getStyleClass().add("download-button"); // Use CSS class instead of inline styling
			downloadBtn.setOnAction(e -> {
				TextInputDialog dialog = new TextInputDialog();
				dialog.setTitle("Save Image");
				dialog.setHeaderText("Save Image");
				dialog.setContentText("File name:");
				dialog.getEditor().setPromptText("Image Name");

				Optional<String> result = dialog.showAndWait();
				result.ifPresent(filename -> {
					// Automatically add .jpg if missing extension
					if (!(filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg"))) {
						filename += ".jpg";
					}

					// Extract name without extension
					String nameOnly = filename.replaceAll("\\.(jpg|jpeg)$", "");

					// Validate alphanumeric nameOnly
					if (!nameOnly.matches("[a-zA-Z0-9]+")) {
						showAlert(Alert.AlertType.ERROR, "Only alphanumeric names are allowed.");
						return;
					}

					// Check for duplicate
					if (savedFileNames.contains(filename)) {
						showAlert(Alert.AlertType.ERROR, "Filename already used.");
						return;
					}

					try {
						File tempFile = new File(System.getProperty("java.io.tmpdir"), filename);
						Files.copy(getClass().getResourceAsStream("/assets/" + imageName), tempFile.toPath(),
								StandardCopyOption.REPLACE_EXISTING);
						tempFile.deleteOnExit();
						savedFileNames.add(filename);

						final String finalFilename = filename;
						VBox shortcut = createDesktopIcon("/assets/" + imageName, filename, () -> {
							openPhotoEditor(imageName, tempFile.getAbsolutePath());
						});
						iconBox.getChildren().add(shortcut);

						// Check if we need to add a new column
						if (iconBox.getChildren().size() > 10) { // Adjust this threshold as needed
							int currentColumns = iconBox.getPrefColumns();
							iconBox.setPrefColumns(currentColumns + 1);
						}

						showAlert(Alert.AlertType.INFORMATION, "Downloaded " + filename);
					} catch (IOException ex) {
						showAlert(Alert.AlertType.ERROR, "Download failed: " + ex.getMessage());
					}
				});
			});

			HBox imageContainer = new HBox(20, catImage, downloadBtn);
			imageContainer.setAlignment(Pos.CENTER_LEFT);
			stockImagesBox.getChildren().add(imageContainer);
		}

		stockScrollPane.setContent(stockImagesBox);

		// Default to empty website
		contentPane.getChildren().add(emptyContent);

		// Search functionality
		Runnable doSearch = () -> {
			String domain = searchBar.getText().trim();
			stopVideo.run();
			stopAudio.run();
			contentPane.getChildren().clear();

			if (domain.isEmpty()) {
				contentPane.getChildren().add(emptyContent);
			} else if (domain.equals("RUtube.net")) {
				contentPane.getChildren().add(ruTubeContent);
			} else if (domain.equals("RUtify.net")) {
				contentPane.getChildren().add(ruTifyContent);
			} else if (domain.equals("stockimage.net")) {
				contentPane.getChildren().add(stockScrollPane);
			} else {
				notFoundDesc.setText(domain + " does not exist, try checking your spelling");
				contentPane.getChildren().add(domainNotFoundContent);
			}
		};

		searchBtn.setOnAction(e -> doSearch.run());
		searchBar.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				doSearch.run();
			}
		});

		// Untuk stop all media (video and audio) ketika window ChRUme ditutup
		chromeStage.setOnCloseRequest((WindowEvent e) -> {
			stopVideo.run();
			mediaPlayer.dispose();
			stopAudio.run();
			rutifyPlayer.dispose();
		});

		// --- Main Layout matching screenshot ---
		VBox mainLayout = new VBox(20, searchBox, contentPane);
		mainLayout.setAlignment(Pos.TOP_CENTER);
		mainLayout.getStyleClass().add("main-layout");

		Scene scene = new Scene(mainLayout, 800, 600);
		scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
		chromeStage.setScene(scene);
		chromeStage.show();
	}

	private void openPhotoEditor(String imageTitle, String imagePath) {
		Image image;
		if (imagePath.startsWith("/assets/")) {
			image = new Image(getClass().getResourceAsStream(imagePath));
		} else {
			File file = new File(imagePath);
			if (!file.exists()) {
				showAlert(Alert.AlertType.ERROR, "Image file not found: " + imagePath);
				return;
			}
			image = new Image(file.toURI().toString());
		}
		PhotoEditor photoEditor = new PhotoEditor(this, imageTitle, image);
		photoEditor.show();
	}

	// PhotoEditor class as an inner class
	public class PhotoEditor extends Stage {
		private final ImageView imageView;
		private double currentRotation = 0;

		public PhotoEditor(Main main, String imageTitle, Image image) {
			setTitle(imageTitle);

			// ImageView for the image
			imageView = new ImageView(image);
			imageView.setPreserveRatio(true);
			imageView.setFitWidth(600); // Set a default width
			imageView.setFitHeight(500); // Set a default height

			// ScrollPane to enable scrolling if zoomed in beyond window size
			ScrollPane scrollPane = new ScrollPane(imageView);
			scrollPane.setPannable(true);
			scrollPane.setFitToWidth(true);
			scrollPane.setFitToHeight(true);

			// Zoom slider from 0.5x to 3x zoom, default 1x
			Slider zoomSlider = new Slider(0.5, 3.0, 1.0);
			zoomSlider.setShowTickLabels(false);
			zoomSlider.setShowTickMarks(false);
			zoomSlider.setMajorTickUnit(0.5);
			zoomSlider.setBlockIncrement(0.1);
			zoomSlider.setPrefWidth(150);

			zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
				double scale = newVal.doubleValue();
				imageView.setScaleX(scale);
				imageView.setScaleY(scale);
			});

			// Label for zoom (now on the right)
			Label zoomLabel = new Label("Zoom:");

			// HBox for zoom controls with slider on left and label on right
			HBox zoomBox = new HBox(5, zoomSlider, zoomLabel);
			zoomBox.setAlignment(Pos.CENTER_LEFT);

			Button rotateButton = new Button("Rotate");
			rotateButton.setOnAction(e -> {
				currentRotation = (currentRotation + 90) % 360;
				imageView.setRotate(currentRotation);
			});

			// Place zoomBox and Button in an HBox
			HBox topBar = new HBox(10, zoomBox, rotateButton);
			topBar.setPadding(new Insets(5));
			topBar.setAlignment(Pos.CENTER_LEFT);

			// Layout
			BorderPane root = new BorderPane();
			root.setTop(topBar);
			root.setCenter(scrollPane);

			Scene scene = new Scene(root, 600, 500);
			scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
			setScene(scene);
		}
	}
}
