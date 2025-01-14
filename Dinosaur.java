import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Dinosaur extends JPanel implements ActionListener, KeyListener {

    int boardWidth = 750;
    int boardHeight = 280;

    boolean showBonus = false; // Flag to check if the "100+" message should be shown
    int bonusX = 0;            // X position of the "100+" message
    int bonusY = 0;            // Y position of the "100+" message
    Timer bonusTimer;          // Timer for displaying "100+" for 2 seconds

    // Images
    Image dinosaurImg;
    Image dinosaurDeadImg;
    Image dinosaurJumpImg;
    Image cactus1Img;
    Image cactus2Img;
    Image cactus3Img;
    Image backgroundImg;
    Image bigCactus1Img;
    Image bigCactus2Img;
    Image bigCactus3Img;
    Image meatImg;

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image img;

        Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    // Dinosaur
    int dinosaurWidth = 95;
    int dinosaurHeight = 100;
    int dinosaurX = 50;
    int dinosaurY = (boardHeight - 10) - dinosaurHeight;
    Block dinosaur;

    // Cactus
    int cactus1Width = 34;
    int cactus2Width = 69;
    int cactus3Width = 90;
    int bigCactus1Width = 50;
    int bigCactus2Width = 70;
    int bigCactus3Width = 120;

    int cactusHeight = 70;
    int bigCactusHeight = 100;
    int cactusX = 700;
    int cactusY = (boardHeight - 5) - cactusHeight;
    ArrayList<Block> obstacaleArray;

    // Meat
    int meatWidth = 60;
    int meatHeight = 80;
    int meatX = 700;
    int meatY = boardHeight - meatHeight;

    // Missing variables
    boolean gameOver = false;
    int velocityY = 0; // Vertical speed of dinosaur
    int velocityX = -10; // Horizontal speed of dinosaur (move leftwards)
    int gravity = 1;   // Gravity effect on the dinosaur
    int score = 0;     // Game score

    Timer gameLoop;
    Timer placeCactusTimer;

    public Dinosaur() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);
        backgroundImg = new ImageIcon(getClass().getResource("./img/background.png")).getImage();
        dinosaurImg = new ImageIcon(getClass().getResource("./img/dino-run.gif")).getImage();
        dinosaurDeadImg = new ImageIcon(getClass().getResource("./img/dino-dead.png")).getImage();
        dinosaurJumpImg = new ImageIcon(getClass().getResource("./img/dino-jump.png")).getImage();
        cactus1Img = new ImageIcon(getClass().getResource("./img/cactus1.png")).getImage();
        cactus2Img = new ImageIcon(getClass().getResource("./img/cactus2.png")).getImage();
        cactus3Img = new ImageIcon(getClass().getResource("./img/cactus3.png")).getImage();
        bigCactus1Img = new ImageIcon(getClass().getResource("./img/big-cactus1.png")).getImage();
        bigCactus2Img = new ImageIcon(getClass().getResource("./img/big-cactus2.png")).getImage();
        bigCactus3Img = new ImageIcon(getClass().getResource("./img/big-cactus3.png")).getImage();
        meatImg = new ImageIcon(getClass().getResource("./img/meat.png")).getImage();

        // Dinosaur
        dinosaur = new Block(dinosaurX, dinosaurY, dinosaurWidth, dinosaurHeight, dinosaurImg);

        // Obstacles
        obstacaleArray = new ArrayList<Block>();

        // Game timer
        gameLoop = new Timer(1000 / 90, this);
        gameLoop.start();

        // Place cactus timer
        placeCactusTimer = new Timer(800, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeObstacale();
            }
        });
        placeCactusTimer.start();
    }

    void placeObstacale() {
        if (gameOver) {
            return;
        }
        double placeObsChance = Math.random(); // 0 - 0.99999
        if (placeObsChance > 0.9) { // 10% chance for cactus3
            Block cactus = new Block(boardWidth, cactusY, cactus3Width, cactusHeight, cactus3Img);
            obstacaleArray.add(cactus);
        } else if (placeObsChance > 0.8) { // 10% for cactus2
            Block cactus = new Block(boardWidth, cactusY, cactus2Width, cactusHeight, cactus2Img);
            obstacaleArray.add(cactus);
        } else if (placeObsChance > 0.6) { // 20% for cactus1
            Block cactus = new Block(boardWidth, cactusY, cactus1Width, cactusHeight, cactus1Img);
            obstacaleArray.add(cactus);
        } else if (placeObsChance > 0.4) { // 20% for meat
            Block meat = new Block(boardWidth, meatY, meatHeight, meatWidth, meatImg);
            obstacaleArray.add(meat); // Add meat to obstacles
        }
    }

    public void move() {
        // Dinosaur movement
        velocityY += gravity;
        dinosaur.y += velocityY;

        if (dinosaur.y > dinosaurY) {
            dinosaur.y = dinosaurY;
            velocityY = 0;
            dinosaur.img = dinosaurImg;
        }

        // Handle collisions with obstacles
        Iterator<Block> iterator = obstacaleArray.iterator();
        while (iterator.hasNext()) {
            Block obstacle = iterator.next();
            obstacle.x += velocityX;

            // Remove obstacle if it moves off screen
            if (obstacle.x + obstacle.width < 0) {
                iterator.remove();
                continue;
            }

            if (obstacle.img == meatImg && collision(dinosaur, obstacle)) {
                // If the dinosaur hits the meat, add 100 points
                score += 100;

                // Set the "100+" display position near the score and show the message
                  // Y position same as score
                showBonus = true;

                // Remove the meat after collection
                iterator.remove();

                // Start the timer to hide the "100+" message after 2 seconds
                bonusTimer = new Timer(1500, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showBonus = false;  // Hide the bonus message
                    }
                });
                bonusTimer.setRepeats(false);  // Only run once after 2 seconds
                bonusTimer.start();
            } else if (collision(dinosaur, obstacle)) {
                // If collision with cactus, game over
                dinosaur.img = dinosaurDeadImg;
                gameOver = true;
                break;  // Stop checking for further collisions if the game is over
            }
        }
        // Score increment
        score++;
    }

    boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placeCactusTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (dinosaur.y == dinosaurY) {
                velocityY = -17;
                dinosaur.img = dinosaurJumpImg;
            }
            if (gameOver) {
                // Restart game
                dinosaur.y = dinosaurY;
                dinosaur.img = dinosaurImg;
                velocityY = 0;
                obstacaleArray.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placeCactusTimer.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw Background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        draw(g);
    }

    public void draw(Graphics g) {
        // Draw Dinosaur
        g.drawImage(dinosaur.img, dinosaur.x, dinosaur.y, dinosaur.width, dinosaur.height, null);

        // Draw Obstacles (cactus and meat)
        for (Block cactus : obstacaleArray) {
            g.drawImage(cactus.img, cactus.x, cactus.y, cactus.width, cactus.height, null);
        }

        // Score
        g.setColor(Color.white);
        g.setFont(new Font("Calibri", Font.BOLD, 25));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf(score), 10, 35);
            g.drawString("Press Space to Restart", 10, 70);
        } else {
            g.drawString("Score: " + String.valueOf(score), 10, 35);
        }

        // Display "100+" for 2 seconds
        if (showBonus) {
            g.setColor(Color.yellow);  // Set the color for the bonus message
            g.setFont(new Font("Calibri", Font.BOLD, 25));  // Use a larger font
            g.drawString("100++", 140, 35);

        }
    }
}
