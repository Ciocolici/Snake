package application;
	
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;

public class Main extends Application {
    // variables
	static int score = 0;
    static int speed = 10;
    static int foodColor = 0;
    static int width = 20;
    static int height = 20;
    static int foodX = 0;
    static int foodY = 0;
    static int cornerSize = 25;
    static List<Corner> snake = new ArrayList<>();
    static Dir direction = Dir.left;
    static boolean gameOver = false;
    static Random rand = new Random();

    public enum Dir {
        left, right, up, down
    }

    public static class Corner {
        int x;
        int y;

        public Corner(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public void start(Stage primaryStage) {
        try {
        	// icon
            primaryStage.getIcons().add(new Image("file:icon.png"));

            // size of the window and the button based on the game game window size
            double buttonWindowWidth = width * cornerSize;
            double buttonWindowHeight = height * cornerSize;
            double buttonWidth = 150;
            double buttonHeight = 100;

            // Start Game window
            StackPane root = new StackPane();
            root.setStyle("-fx-background-color: radial-gradient(radius 100%, lightgreen, darkgreen);");

            // button 
            Button startButton = new Button("Start Game");
            startButton.setStyle("-fx-border-radius: 20px; -fx-background-radius: 20px; -fx-font-weight: bold; -fx-font-size: 20px;");
            startButton.setPrefWidth(buttonWidth);
            startButton.setPrefHeight(buttonHeight);
            startButton.setOnAction(event -> startGame(primaryStage));

            // button add
            root.getChildren().add(startButton);

            // scene Start Game window
            Scene scene = new Scene(root, buttonWindowWidth, buttonWindowHeight);

            // window details
            primaryStage.setScene(scene);
            primaryStage.setTitle("Snake by Cioco");
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startGame(Stage primaryStage) {
        snake.clear();
        speed = 10;
        gameOver = false;
        direction = Dir.left;
        snake.add(new Corner(width / 2, height / 2));
        snake.add(new Corner(width / 2, height / 2));
        snake.add(new Corner(width / 2, height / 2));

        newFood();

        StackPane gameRoot = new StackPane();
        Canvas c = new Canvas(width * cornerSize, height * cornerSize);
        GraphicsContext gc = c.getGraphicsContext2D();
        gameRoot.getChildren().add(c);

        new AnimationTimer() {
            long lastTick = 0;

            public void handle(long now) {
                if (lastTick == 0) {
                    lastTick = now;
                    tick(gc);
                    return;
                }
                if (now - lastTick > 1000000000 / speed) {
                    lastTick = now;
                    tick(gc);
                }
            }
        }.start();

        Scene gameScene = new Scene(gameRoot, width * cornerSize, height * cornerSize, Color.BLACK);
        gameScene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            if (key.getCode() == KeyCode.UP) {
                direction = Dir.up;
            }
            if (key.getCode() == KeyCode.DOWN) {
                direction = Dir.down;
            }
            if (key.getCode() == KeyCode.LEFT) {
                direction = Dir.left;
            }
            if (key.getCode() == KeyCode.RIGHT) {
                direction = Dir.right;
            }
        });

        primaryStage.setScene(gameScene);
    }
	
	// tick
	public static void tick(GraphicsContext gc) {
		if (gameOver) {
			gc.setFill(Color.DARKRED); // Game Over color
			gc.setStroke(Color.BLACK);
			gc.setLineWidth(2);
			gc.setFont(Font.font("Impact", 40)); // Game Over font and size
			gc.fillText("YOU DIED :'(\nGAME OVER", 150, 250);// Game Over position
			gc.strokeText("YOU DIED :'(\nGAME OVER", 150, 250);
			return;
		}
		
		for (int i = snake.size() - 1; i >= 1; i--) {
			snake.get(i).x = snake.get(i - 1).x;
			snake.get(i).y = snake.get(i - 1).y;
		}
		
		switch (direction) { // Game Over if the snake touches a border
		case up:
			snake.get(0).y--;
			if (snake.get(0).y < 0) {
				gameOver = true;
			}
			break;
		case down:
			snake.get(0).y++;
			if (snake.get(0).y > height) {
				gameOver = true;
			}
			break;
		case left:
			snake.get(0).x--;
			if (snake.get(0).x < 0) {
				gameOver = true;
			}
			break;
		case right:
			snake.get(0).x++;
			if(snake.get(0).x > width) {
				gameOver = true;
			}
			break;
		}
		
		// eat food
		if (foodX == snake.get(0).x && foodY == snake.get(0).y) { // snake grows
			snake.add(new Corner(-1, -1));
			newFood();
		}
		
		// suicide
		for (int i = 1; i < snake.size(); i++) { // Game Over if the snakes hits itself
			if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
				gameOver = true;
			}
		}
		
		// background
		Stop[] stops = new Stop[] { 
			    new Stop(0, Color.BROWN),     // Start color (at 0%)
			    new Stop(1, Color.DARKGREEN)     // End color (at 100%)
			};
		LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, null, stops);
		gc.setFill(gradient);
		gc.fillRect(0, 0, width * cornerSize, height * cornerSize);
		
		// score
		gc.setFill(Color.WHITE); // color
		gc.setFont(Font.font("Verdana", 15)); // font and size
		gc.fillText("SCORE: " + score, 390, 30); // position
		
		// random food color
		Color cc = Color.WHITE;
		
		switch (foodColor){
		case 0: cc = Color.LIMEGREEN;
		break;
		case 1: cc = Color.RED;
		break;
		case 2: cc = Color.YELLOW;
		break;
		case 3: cc = Color.rgb(255, 218, 185); // peach
		break;
		case 4: cc = Color.ORANGE;
		break;
		}
		gc.setFill(cc);
		gc.fillOval(foodX * cornerSize, foodY * cornerSize, cornerSize, cornerSize);
		
		// snake
		for (Corner c: snake) { // paint the snake in 2 colors and 2 sizes
			// shadow
			gc.setFill(Color.PALEGREEN);
			gc.fillRect(c.x * cornerSize, c.y * cornerSize, cornerSize - 1, cornerSize - 1); 
			// foreground
			gc.setFill(Color.DARKGREEN.darker());
			gc.fillRect(c.x * cornerSize, c.y * cornerSize, cornerSize - 2, cornerSize - 2);
		}
		
	}
	
	// food
	public static void newFood() {
		start: while (true){ // new food on random location foodX * foodY on the canvas (if there is no snake)
			foodX = rand.nextInt(width);
			foodY = rand.nextInt(height);
			
			for (Corner c : snake) {
				if (c.x == foodX && c.y == foodY) {
					continue start;
				}
			}
			foodColor = rand.nextInt(5); // new color
			score ++;
			break;
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
