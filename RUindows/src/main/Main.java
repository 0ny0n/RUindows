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
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

	private VBox iconBox = new VBox(25);
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
		errorMsg.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
		Label welcomeLabel = new Label("Welcome RU24-2!");
		welcomeLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");
		PasswordField pwField = new PasswordField();
		pwField.setPromptText("Enter password");
		pwField.setMaxWidth(400);
		Button loginBtn = new Button("Login");

		loginBtn.setOnAction(e -> {
			if (pwField.getText().equals("Hello")) {
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

		iconBox.setPadding(new Insets(30, 0, 0, 30));
		iconBox.setAlignment(Pos.TOP_LEFT);
		// VBox containing all icons + labels
		// Wajib pake VBox untuk setiap icon + label biar spacingnya bagus.
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

		MenuItem logoutMenu = new MenuItem("Logout");
		logoutMenu.setGraphic(logoutLogo);
		logoutMenu.setOnAction(e -> homeStage.setScene(LoginPage(homeStage)));

		MenuItem shutdownMenu = new MenuItem("Shutdown");
		shutdownMenu.setGraphic(shutdownLogo);
		shutdownMenu.setOnAction(e -> homeStage.close());

		Menu startMenu = new Menu();
		startMenu.getItems().addAll(logoutMenu, shutdownMenu);
		startMenu.setGraphic(windowsLogo);

		MenuItem openNotepadItem = new MenuItem("Open Notepad");
		openNotepadItem.setOnAction(e -> notepadWindow());

		Menu notepadMenu = new Menu("");
		notepadMenu.setGraphic(notepadLogo);
		notepadMenu.getItems().add(openNotepadItem);

		// Taskbar
		MenuBar taskBar = new MenuBar(startMenu, notepadMenu);
		taskBar.setStyle("-fx-background-color: black;");

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
		VBox box = new VBox(5);
		box.setAlignment(Pos.CENTER);
		box.getChildren().addAll(icon, label);

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

		MenuItem saveMenu = new MenuItem("Save");
		saveMenu.setOnAction(e -> {
			String defaultName = (textFileCount == 0) ? "text.txt" : "text" + textFileCount + ".txt";
			TextInputDialog dialog = new TextInputDialog(defaultName);
			dialog.setTitle("Save File");
			dialog.setHeaderText("Enter file name (alphanumeric only):");
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
				if (savedFileNames.contains(filename)) {
					showAlert(Alert.AlertType.ERROR, "Filename already exists.");
					return;
				}

				try {
					File tempDir = new File(System.getProperty("java.io.tmpdir"));
					File file = new File(tempDir, filename);
					FileWriter writer = new FileWriter(file);
					writer.write(textArea.getText());
					writer.close();
					file.deleteOnExit(); // ensure file is deleted on app close

					textFileCount++;
					savedFileNames.add(filename);
					addNotepadShortcut(filename, file.getAbsolutePath());
					showAlert(Alert.AlertType.INFORMATION, "File temporarily saved as " + filename);
				} catch (IOException ex) {
					showAlert(Alert.AlertType.ERROR, "Failed to save file: " + ex.getMessage());
				}
			});
		});

		Menu fileMenu = new Menu("File");
		fileMenu.getItems().add(saveMenu);

		MenuBar menuBar = new MenuBar(fileMenu);
		VBox layout = new VBox(menuBar, textArea);
		VBox.setVgrow(textArea, Priority.ALWAYS);

		Scene notepadScene = new Scene(layout, 900, 600);
		notepadStage.setScene(notepadScene);
		notepadStage.show();
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

		try {
			area.setText(Files.readString(Paths.get(path)));
		} catch (IOException e) {
			showAlert(Alert.AlertType.ERROR, "Cannot open file.");
		}

		VBox layout = new VBox(new MenuBar(), area);
		VBox.setVgrow(area, Priority.ALWAYS);
		stage.setScene(new Scene(layout, 800, 600));
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
		searchBar.setStyle("-fx-border-color: deepskyblue; -fx-border-radius: 4px; -fx-focus-color: deepskyblue;");

		Button searchBtn = new Button("Search");

		HBox searchBox = new HBox(10, searchBar, searchBtn);
		searchBox.setAlignment(Pos.CENTER);
		searchBox.setPadding(new Insets(30, 0, 10, 0)); // Top padding to mimic screenshot

		// --- StackPane for dynamic content ---
		StackPane contentPane = new StackPane();
		contentPane.setPadding(new Insets(10));

		// --- Empty Website Content ---
		Label emptyContent = new Label("Empty Website");
		emptyContent.setStyle("-fx-font-size: 18px; -fx-text-fill: gray;");

		// --- Domain Not Found Content (Styled like screenshot) ---
		VBox domainNotFoundContent = new VBox(10);
		domainNotFoundContent.setAlignment(Pos.CENTER);

		Label notFoundTitle = new Label("This site can't be reached");
		notFoundTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333333;");

		Label notFoundDesc = new Label(); // Will set text dynamically
		notFoundDesc.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666;");

		domainNotFoundContent.getChildren().addAll(notFoundTitle, notFoundDesc);

		// --- RUtube.net Content (Styled) ---
		VBox ruTubeContent = new VBox(20);
		ruTubeContent.setStyle("-fx-background-color: #2b2b2b;"); // Dark background
		ruTubeContent.setPadding(new Insets(20));
		ruTubeContent.setAlignment(Pos.TOP_CENTER);

		// Header (logo + label)
		HBox header = new HBox(10);
		header.setAlignment(Pos.CENTER_LEFT);

		ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/assets/youtube-logo.png")));
		logo.setFitHeight(30);
		logo.setPreserveRatio(true);

		Label logoLabel = new Label("RUtube");
		logoLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: white; -fx-font-weight: bold;");

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
		ruTifyContent.setStyle("-fx-background-color: black;");
		ruTifyContent.setMinHeight(800);
		ruTifyContent.setPadding(new Insets(20));
		ruTifyContent.setAlignment(Pos.TOP_LEFT);

		// Header: logo + title
		HBox rutifyHeader = new HBox(10);
		ImageView rutifyLogo = new ImageView(new Image(getClass().getResourceAsStream("/assets/spotify-logo.png")));
		rutifyLogo.setFitHeight(30);
		rutifyLogo.setPreserveRatio(true);
		Label rutifyTitle = new Label("RUtify");
		rutifyTitle.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");
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
		rutifyPlayerBox.setPadding(new Insets(40, 0, 0, 0)); // spacing below the header

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
		stockScrollPane.setStyle("-fx-background: white;"); // Optional: match background

		VBox.setVgrow(stockScrollPane, Priority.ALWAYS);
		VBox stockImagesBox = new VBox(20);
		stockImagesBox.setStyle("-fx-background-color: white;");
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
			downloadBtn.setOnAction(e -> {
				TextInputDialog dialog = new TextInputDialog(imageName);
				dialog.setTitle("Download Image");
				dialog.setHeaderText(
						"Enter filename (alphanumeric, extension .jpg or .jpeg will be added if missing):");

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
		mainLayout.setStyle("-fx-background-color: #f5f5f5;");

		Scene scene = new Scene(mainLayout, 800, 600);
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
			Label zoomLabel = new Label("Zoom:");
			Slider zoomSlider = new Slider(0.5, 3.0, 1.0);
			zoomSlider.setShowTickLabels(true);
			zoomSlider.setShowTickMarks(true);
			zoomSlider.setMajorTickUnit(0.5);
			zoomSlider.setBlockIncrement(0.1);
			zoomSlider.setPrefWidth(150);

			zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
				double scale = newVal.doubleValue();
				imageView.setScaleX(scale);
				imageView.setScaleY(scale);
			});

			// HBox for zoom controls
			HBox zoomBox = new HBox(5, zoomLabel, zoomSlider);
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
			setScene(scene);
		}
	}

}
