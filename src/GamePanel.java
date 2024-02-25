/* GamePanel class acts as the main "game loop" - continuously runs the game and calls whatever needs to be called

Child of JPanel because JPanel contains methods for drawing to the screen

Implements KeyListener interface to listen for keyboard input

Implements Runnable interface to use "threading" - let the game do two things at once

*/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable, KeyListener{

  //dimensions of window
  public static final int GAME_WIDTH = 750;
  public static final int GAME_HEIGHT = 750;

  public Thread gameThread;
  public Image image;
  public Graphics graphics;
  public PlayerBall ball;
  public PlayerBall2 ball2;
  public Puck ball3;


  public GamePanel(){
    ball = new PlayerBall((GAME_WIDTH/2)-40, 0); //create a player controlled ball, set start location to middle of screen
    ball2 = new PlayerBall2((GAME_WIDTH/2)-40, GAME_HEIGHT); //create a player controlled ball, set start location to middle of screen
    ball3 = new Puck((GAME_WIDTH/2)-40, (GAME_HEIGHT/2)-60); //create a game controlled puck 
    this.setFocusable(true); //make everything in this class appear on the screen
    this.addKeyListener(this); //start listening for keyboard input

    //add the MousePressed method from the MouseAdapter - by doing this we can listen for mouse input. We do this differently from the KeyListener because MouseAdapter has SEVEN mandatory methods - we only need one of them, and we don't want to make 6 empty methods
    addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				ball.mousePressed(e);
			}
		});
    this.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));

    //make this class run at the same time as other classes (without this each class would "pause" while another class runs). By using threading we can remove lag, and also allows us to do features like display timers in real time!
    gameThread = new Thread(this);
    gameThread.start();
  }

  //paint is a method in java.awt library that we are overriding. It is a special method - it is called automatically in the background in order to update what appears in the window. You NEVER call paint() yourself
  public void paint(Graphics g){
    //we are using "double buffering here" - if we draw images directly onto the screen, it takes time and the human eye can actually notice flashes of lag as each pixel on the screen is drawn one at a time. Instead, we are going to draw images OFF the screen, then simply move the image on screen as needed. 
    image = createImage(GAME_WIDTH, GAME_HEIGHT); //draw off screen
    graphics = image.getGraphics();
    draw(graphics);//update the positions of everything on the screen 
    g.drawImage(image, 0, 0, this); //move the image on the screen

  }

  //call the draw methods in each class to update positions as things move
  public void draw(Graphics g){
    ball.draw(g);
    ball2.draw(g);
    ball3.draw(g);
  }

  //call the move methods in other classes to update positions
  //this method is constantly called from run(). By doing this, movements appear fluid and natural. If we take this out the movements appear sluggish and laggy
  public void move(){
    ball.move();
    ball2.move();
    ball3.move();
  }

  //handles all collision detection and responds accordingly
  public void checkCollision(){
    
    //force player to remain on screen
    if(ball.y<= 0){
      ball.y = 0;
    }
    if(ball.y >= (GAME_HEIGHT/2)-40 - PlayerBall.BALL_DIAMETER){
      ball.y = (GAME_HEIGHT/2)-40-PlayerBall.BALL_DIAMETER;
    }
    if(ball.x <= 0){
      ball.x = 0;
    }
    if(ball.x + PlayerBall.BALL_DIAMETER >= GAME_WIDTH){
      ball.x = GAME_WIDTH-PlayerBall.BALL_DIAMETER;
    }
    
    if(ball2.y<= 0){
        ball2.y = 0;
      }
      if(ball2.y >= GAME_HEIGHT - PlayerBall2.BALL_DIAMETER){
        ball2.y = GAME_HEIGHT-PlayerBall2.BALL_DIAMETER;
      }
      
      if(ball2.y <= (GAME_HEIGHT/2)+40 - PlayerBall2.BALL_DIAMETER){
          ball2.y = (GAME_HEIGHT/2)+40-PlayerBall2.BALL_DIAMETER;
        }
      if(ball2.x <= 0){
        ball2.x = 0;
      }
      if(ball2.x + PlayerBall2.BALL_DIAMETER >= GAME_WIDTH){
        ball2.x = GAME_WIDTH-PlayerBall2.BALL_DIAMETER;
      }
      
      if(ball3.y<= 0){
          ball3.y = 0;
        }
        if(ball3.y >= (GAME_HEIGHT)-40 - PlayerBall.BALL_DIAMETER){
          ball3.y = (GAME_HEIGHT)-40-PlayerBall.BALL_DIAMETER;
        }
        if(ball3.x <= 0){
          ball3.x = 0;
        }
        if(ball3.x + PlayerBall.BALL_DIAMETER >= GAME_WIDTH){
          ball3.x = GAME_WIDTH-PlayerBall.BALL_DIAMETER;
        }
  }

  //run() method is what makes the game continue running without end. It calls other methods to move objects,  check for collision, and update the screen
  public void run(){
    //the CPU runs our game code too quickly - we need to slow it down! The following lines of code "force" the computer to get stuck in a loop for short intervals between calling other methods to update the screen. 
    long lastTime = System.nanoTime();
    double amountOfTicks = 60;
    double ns = 1000000000/amountOfTicks;
    double delta = 0;
    long now;

    while(true){ //this is the infinite game loop
      now = System.nanoTime();
      delta = delta + (now-lastTime)/ns;
      lastTime = now;

      //only move objects around and update screen if enough time has passed
      if(delta >= 1){
        move();
        checkCollision();
        repaint();
        delta--;
      }
    }
  }

  //if a key is pressed, we'll send it over to the PlayerBall class for processing
  public void keyPressed(KeyEvent e){
    ball.keyPressed(e);
    ball2.keyPressed(e);
  }

  //if a key is released, we'll send it over to the PlayerBall class for processing
  public void keyReleased(KeyEvent e){
    ball.keyReleased(e);
    ball2.keyReleased(e);
  }

  //left empty because we don't need it; must be here because it is required to be overridded by the KeyListener interface
  public void keyTyped(KeyEvent e){

  }
}