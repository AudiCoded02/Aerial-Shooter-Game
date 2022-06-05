import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.MouseInfo;
import java.awt.image.*; 
import java.io.*; 
import javax.imageio.*; 
import java.util.ArrayList;
import java.awt.geom.*;
import java.util.Scanner;


/*The basic template is from Game4
 * 
 */
public class FinalGame extends JFrame implements ActionListener{
 Timer myTimer;   
 GamePanel game;
 
 
  
    public FinalGame() {
  super("Aerial Shooter");//name of the game
  
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  setSize(800,650);
  
  myTimer = new Timer(10, this);  // trigger every 10 ms
  

  game = new GamePanel(this);
  add(game);
  

  setResizable(false);
  
  setVisible(true);
    }
 
 public void start(){
  myTimer.start();
 }

 public void actionPerformed(ActionEvent evt){
   
   if(game!=null){
     game.levelOne();//method to call all level one methods
     game.repaint();//repaint usual
     game.update();//updating the settings of some stuff like mouse, frameCounters, etc that will be used for the highlighting purposes of the menu buttons
     game.levelTwo();//method to call all level two methods
     game.gameLost();//calling methods if game has been lost or won
     game.gameWin();
   }
   
   
 }

    public static void main(String[] arguments) {
      FinalGame frame = new FinalGame();  
    }
}

class GamePanel extends JPanel implements KeyListener,MouseListener{
  
  private Point mouse;
 
  
  
 private int enemyCounter=20;//how many enemies there will be
 private int playerBullets=80;//max amount of bullets the player will have
 //for all the screen pages excluding LEFT AND RIGHT
 public static final int MENU=1,INSTRUC=2,CRED=3,ONE=4,TWO=5;//pages for menu,instruction,credits,level one and level two
 public static final int INSTRUC2=6;//second intruction pages
 public static final int STORY=9,STORYONE=10,STORYTWO=11;//story pages(beginning story button page in menu,story before first level and second level
 public static final int RIGHT=7,LEFT=8;//for boss direction
 public static final int WIN=12,LOST=13;//pages when you win the game and when you lost the game at any of the 2 levels
 //
 private int bossDir;//the boss direction
 
 public int screen;
 Font fontSys=null;//Font for the strings
 Font text=null;//miniature version of text for the description under each header/title for the pages
 Font bossText=null;//like a bigger emphasized text for level 1 and 2
   
   
 //
 private int lives=2;//amount of lives the player have
 private int health=100;//the health of the player
 private int playerX,playerY;//x,y coords for the player
 private int bulletX,bulletY;//x,y coords for the bullet for the player
 private int enemyX,enemyY;//enemy x and y coordinates for all types of enemies
 private int enemyBulletX,enemyBulletY;//x and y coordinates for the enemyBullets
 
 
 private int bossX,bossY;//boss coordinates
 private int missileX,missileY;//coordinates for the missiles that will zoom across the screen
 private int bossHealth=500;//health for the boss
 private ArrayList<Image>mList=new ArrayList<Image>();//arrayList to contain all of the missiles when spawned on the stage
 private int mCount=0;//counter timer for when to add a missile
 private int bBulletX,bBulletY;//coordinates of the boss's bullet
 private ArrayList<Image>bList=new ArrayList<Image>();//arrayList to contain all the bullets of the boss
 private Image bBullet;//image of the boss's bullet
 private int oh=200;//overheat timer whenever the boss minigun counter reaches too high, this will come into effect
 
 private boolean []keys;
 private Image back,menuBack;//background image for the game
 private Image enemyJet,enemyDemon,enemyTank,enemyBoss;//image for 3 enemies types
 private Image playerJet;//image for the player
 private Image live1,live2;//miniature versions of the player to indicate the lives remaining
 private Image bullet,enemyBullet;//an image for the player's and enemy's bullet
 private Image missile;//image for the missile
 private FinalGame mainFrame;
 private ArrayList<Image>bullets=new ArrayList<Image>();//an ArrayList to contain all of the player's bullets
 private ArrayList<Image>lived=new ArrayList<Image>();//an ArrayList to contain all of the player's lives
 private ArrayList<Image>enemy=new ArrayList<Image>();//an ArrayList to contain enemies when spawned
 private ArrayList<Image>eBullets=new ArrayList<Image>();//an ArrayList to contain all of the enemies bullets
 
 private Rectangle instruc,cred,play,menu,instruc2,backI,story;//for the menu buttons and its respectful pages
 private Rectangle levelO,levelT;//buttons to proceed on to level 1 and 2 when in the story page after the completion of each level
 
 //for the instruction and second instruction page
 private Image arrow;
 private Image spaceB;
 private Image boss;
 private Image smallMiss;
 private Image arrowR;
 private Image smallBoss;
 
 
 
 
 //end 
 //for the other pages
 private Image tyrium;
 private Image destroy;
 private Image explode;
 private Image firee;
 //background images for the other menu screens throughout game
 private Image wonBack;
 private Image lostBack;
 private Image credBack;
 private Image nationBack;
 
 
 
 private Image logo;//my personalized logo
 private int spawnCount=0;//a counter that is acting as a timer to spawn an enemy 
 private int fire=0;//same deal but when to spawn a bullet that is "fired" from the enemy
 private int startFire=0;//counter that when going, it will continiously add bullets for the boss to fire then it will "overheat" at 1 point before resetting similar to how a minigun function
 private int frameCount=0;//drawing the string for 2nd level to indicate objective 
 private int frameCounter=0;//same thing for the 1st level to indicate objective
 

 
 public GamePanel(FinalGame m){
   screen=MENU;//screen will always be set to menu page by default
   
  
   
  playerX=400;//player initial x
  playerY=490;//player initial y
  bulletX=playerX;//initial player bullet x and y
  bulletY=playerY;
  enemyY=0;//enemy will always spawn at y=0 (top of the screen);
  enemyX=randint(10,680);//randomizing the enemyX coordinate at the beginning 
  
  enemyBulletX=enemyX+40;
  enemyBulletY=enemyY;
  //the buttons for the pages except the game pages
  play= new Rectangle(300,200,230,50);
  instruc = new Rectangle(300,300,230,50);
  cred= new Rectangle(300,400,230,50);
  story= new Rectangle(300,500,230,50);
  menu=new Rectangle(10,20,100,50);
  instruc2=new Rectangle(680,20,100,50);
  backI=new Rectangle(10,20,100,50);
  levelO=new Rectangle(580,20,200,50);
  levelT=new Rectangle(580,20,200,50);
  
  
  
  screen=MENU;//screen will always be set to menu page by default
  
  keys = new boolean[KeyEvent.KEY_LAST+1];
  //opening images for the game and resizing them
  back = new ImageIcon("Land.jpg").getImage();
  back = back.getScaledInstance(800,650,Image.SCALE_SMOOTH);//resizing background image to my preferred standards
  
  playerJet=new ImageIcon("Player Jet.png").getImage();
  playerJet = playerJet.getScaledInstance(100,90,Image.SCALE_SMOOTH);//resizing background image to my preferred standards
  enemyJet=new ImageIcon("Enemy Jet.png").getImage();
  enemyJet = enemyJet.getScaledInstance(80,110,Image.SCALE_SMOOTH);//resizing background image to my preferred standards
  enemyTank=new ImageIcon("Tank Plane.png").getImage();
  enemyTank = enemyTank.getScaledInstance(150,90,Image.SCALE_SMOOTH);//resizing background image to my preferred standards
  enemyDemon=new ImageIcon("Speed Demon.png").getImage();
  enemyDemon = enemyDemon.getScaledInstance(100,110,Image.SCALE_SMOOTH);//resizing background image to my preferred standards
  enemyBoss=new ImageIcon("boss.png").getImage();
  enemyBoss = enemyBoss.getScaledInstance(450,300,Image.SCALE_SMOOTH);//resizing background image to my preferred standards
  bullet=new ImageIcon("bullets.png").getImage();
  bullet=bullet.getScaledInstance(25,25,Image.SCALE_SMOOTH);
  enemyBullet=new ImageIcon("enemy bullet.png").getImage();
  enemyBullet=enemyBullet.getScaledInstance(25,25,Image.SCALE_SMOOTH);
  missile=new ImageIcon("missile.png").getImage();
  missile=missile.getScaledInstance(25,100,Image.SCALE_SMOOTH);
  fontSys = new Font("Times New Roman",Font.PLAIN,32);//setting font to Times
  text=new Font("Times New Roman",Font.PLAIN,16);
  bossText=new Font("Times New Roman",Font.PLAIN,50);
  live1 = playerJet.getScaledInstance(40,40,Image.SCALE_SMOOTH);//resizing background image to my preferred standards
  live2 = live1;//2nd life is obviously same as first one
  lived.add(live1);//add both of them into life arrayList
  lived.add(live2);
  
  logo=new ImageIcon("logo.png").getImage();//my personalized logo
  logo=logo.getScaledInstance(150,130,Image.SCALE_SMOOTH);
  
  
  //for both instruction pages
  arrow=new ImageIcon("arrowKeys.png").getImage();//arrow keys
  arrow=arrow.getScaledInstance(100,50,Image.SCALE_SMOOTH);
  spaceB=new ImageIcon("spaceBar.png").getImage();//space bar
  spaceB=spaceB.getScaledInstance(100,25,Image.SCALE_SMOOTH);
  boss=enemyBoss.getScaledInstance(120,100,Image.SCALE_SMOOTH);//scaled the boss short here just so it can fit and doesnt take up too much space
  smallMiss=missile.getScaledInstance(25,50,Image.SCALE_SMOOTH);//miniature version of boss's missiles just so it can fit on instruction page
  arrowR=new ImageIcon("arrowRight.png").getImage();
  arrowR=arrowR.getScaledInstance(50,30,Image.SCALE_SMOOTH);
  
  menuBack=new ImageIcon("menuBack.jpg").getImage();//background for the menu page
  menuBack=menuBack.getScaledInstance(800,800,Image.SCALE_SMOOTH);
  
  //for other pages
  tyrium=new ImageIcon("Tyrium.png").getImage();//
  tyrium=tyrium.getScaledInstance(150,150,Image.SCALE_SMOOTH);
  explode=new ImageIcon("explosion.png").getImage();//
  explode=explode.getScaledInstance(300,300,Image.SCALE_SMOOTH);
  destroy=new ImageIcon("destroyed.png").getImage();//
  destroy=destroy.getScaledInstance(1000,150,Image.SCALE_SMOOTH);
  firee=new ImageIcon("fire.png").getImage();//
  firee=firee.getScaledInstance(800,50,Image.SCALE_SMOOTH);
  //other background pages
  wonBack=new ImageIcon("won.jpg").getImage();//
  wonBack=wonBack.getScaledInstance(800,700,Image.SCALE_SMOOTH);
  lostBack=new ImageIcon("lost.jpg").getImage();//
  lostBack=lostBack.getScaledInstance(800,700,Image.SCALE_SMOOTH);
  credBack=new ImageIcon("credit.jpg").getImage();//
  credBack=credBack.getScaledInstance(800,800,Image.SCALE_SMOOTH);
  nationBack=new ImageIcon("nation.jpg").getImage();//
  nationBack=nationBack.getScaledInstance(800,800,Image.SCALE_SMOOTH);
  
  
  //Level Two
  bossX=200;
  bossY=-1000;//boss current coordinate at the start(boss will spawn off screen and make its way towards the fight)
  missileX=randint(100,700);//missile x values where always be random as there is no telling where it is coming from
  missileY=-200;//missile y value will always be this to make it look like its appearing out of nowhere and onto the fight
  bBulletX=bossX+215;
  bBulletY=bossY+240;//where the coordinate will first spawn
  bBullet=new ImageIcon("bossBullet.png").getImage();//the boss's bullet
  bBullet=bBullet.getScaledInstance(25,25,Image.SCALE_SMOOTH);
  smallBoss=enemyBoss.getScaledInstance(200,150,Image.SCALE_SMOOTH);
  
  
  //
  
  
  
  //
  mainFrame = m;
  setSize(800,600);
  addKeyListener(this);
  addMouseListener(this);
  
  
  int r=randint(0,2);//randomizing the beginning enemy that will spawn first
  if(frameCounter>=1){//this is here to prevent an enemy unit from appearing on the screen when the objective text is visible
  if(r==0){
    enemy.add(enemyJet);
  }
  else if(r==1){
    enemy.add(enemyTank);
  }
  else if(r==2){
    enemy.add(enemyDemon);
  }
  
  

 
  
 }
 }
 
  public void addNotify() {
      super.addNotify();
      requestFocus();
      mainFrame.start();
    }
 
 public void move(){//for all the movement in the first level(includes movement of player, enemy, bullets, etc.)
  if(keys[KeyEvent.VK_RIGHT] ){
   playerX+=8;
   //the bullet x coordinate will be the same as the player's x coordinate
   bulletX=playerX;
  }
  if(keys[KeyEvent.VK_LEFT] ){
   playerX-=8;
   bulletX=playerX;
   
  }
  if(keys[KeyEvent.VK_UP] ){
    playerY-=5;
   
  }  
  if(keys[KeyEvent.VK_DOWN] ){
    playerY+=5;
    
   
  }
  
  
  
  enemyBulletX=enemyX+40;//the enemyBullet X will always be set to the current enemy x +40(to make it look like its actually shooting from the front)
  if(enemy.size()==0){//when there are no enemies in arrayList, the bullet for the enemies location will reset based on the next enemy location
    enemyBulletX=enemyX+40;
    enemyBulletY=-100;
  }
  
  if(enemy.size()==0){//if there is no enemy presented in the arrayList, it will randomize the next enemy x coordinate
    enemyX=randint(10,680);
    
  }
  
  if(eBullets.size()>=1){//if there is more than 1 enemuy bullet presented in arrayList
    for(int h=0;h<eBullets.size();h++){
      enemyBulletY+=40;//enemyBullet will move down the screen
      if(enemyBulletY>=490){//when it reaches the "border line", it will disappear and restart at the top
        eBullets.remove(eBullets.get(h));
        enemyBulletY=enemyY;//enemyBullet Coordinate will reset based on the enemy coordinates
        enemyBulletX=enemyX+40;
      }
    }
  }
 
  if(bullets.size()>=1){//if there are more than or equal to 1 bullet(s) in the arrayList
    for(int i=0;i<bullets.size();i++){//going through every bullet
      bulletY-=40;//y value for the bullets decreases
      
      if(bulletY<=0){//once it gets to below or at 0
        
        bullets.remove(bullets.get(i));//removing that bullet
        bulletY=playerY;//that bulletY will reset to playerY
        bulletX=playerX;//same deal for X
      
    }
     
    }
 }
  
  //if the enemy size is greater than 1
  if(enemy.size()>=1){
    for(int j=0;j<enemy.size();j++){
      if(enemy.get(j)==enemyJet){//if the enemy is the standard enemy jet
        enemyY+=5;//considered default speed
      }
      else if(enemy.get(j)==enemyTank){//if the enemy is the tank
        enemyY+=3;//will go slower
      }
      else if(enemy.get(j)==enemyDemon){//if the enemy is the demon enemy
        enemyY+=7;//will go faster
      }
      if(enemyY>=480){
        enemy.remove(enemy.get(j));//if it touches the white line border, the enemyY will be back to 0 and will remove that current enemy
        enemyY=0;
        enemyCounter--;//the reason why enemies left to destroy will decrease if one reaches the border is because it represents that it went pass the "last line of defense" to destroy the 
        //citizens, thus losing a life
        lives--;
        
        
        for(int i=0;i<lived.size();i++){//going through lives arrayList
         lived.remove(lived.get(i)); //removes a live that is displayed on screen
        }
      }
      
      
    }
    
  }
  
  
  
 }
 public void update(){//updating some settings like mouse, frameCounter,etc. as it goes through the game 
   

   mouse = MouseInfo.getPointerInfo().getLocation();//mouse will always be updating as it is used for the buttons
   Point offset = getLocationOnScreen();
   mouse.translate(-offset.x, -offset.y);
   if(screen==TWO){
     frameCount+=1;
   }
   if(screen==ONE){
     frameCounter+=1;
   }
   
 }
 
 
 
 public void enemyFire(){//method for when the enemy fires (i only created a method separately here just to organize it)
   if(frameCounter>=180){
   fire+=1;
   if(enemy.size()>=1){
     if(fire%280==0){//will only fire a bullet once it reaches "4 seconds" and if there are more than 1 enemy presented on the screen just so it doesnt look like the bullet
       //is like spassing out at the top trying to determine where the next enemy x location is.
       eBullets.add(enemyBullet);
       
     }
   }
     if(fire>=280){//if fire gets to or above 280, it'll reset
       fire=0;
     }
   
   }
 }
 
 public void spawnEnemy(){//function to spawn enemies aka adding them to the arraylist every time interval
   
   spawnCount+=1;//spawn counter will increase by 1
   
   if(spawnCount%200==0){//when it hits 200 and that divide by 200 is even(0)
     int g=randint(0,2);//create a random number between 0,1 and 2
     if(g==0){//if 0, enemy added is jet
       enemy.add(enemyJet);
     }
     else if(g==1){
       enemy.add(enemyTank);//same for tank and demon
     }
     else if(g==2){
       enemy.add(enemyDemon);
     }
   }
   if(spawnCount>=200){//if it reaches 200 or above, spawnCount will reset
     spawnCount=0;
   }
   
 }

 
    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        if(keys[KeyEvent.VK_SPACE]){
          if(screen==ONE && frameCounter>=250){//basically until the text has been ridded
          playerBullets-=1;//the counter for number of bullets reduce by 1
          bullets.add(bullet);//adding a bullet to the arrayList
          }
          //to prevent any sort of "unfairness" or "imbalancing" the player cannot shoot in advance until the boss makes it way down the screen and appears
          else if(screen==TWO && bossY>=50){
            playerBullets-=1;
            bullets.add(bullet);
          }
        }
        
    }
    
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
    public void setBoundary(){//the boundaries/limit to how far the player can move
      if(playerX<0){
        playerX=0;
        
      }
      else if(playerX>680){
        playerX=680;
      }
      if(playerY<400){
        playerY=400;
        
      }
      else if(playerY>490){
        playerY=490;
      }
      
      
    }
    public void mouseClicked(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    
    public void mousePressed(MouseEvent e){
      if(screen==MENU){
        //if the play button is pressed, proceeds on to level one
        if(play.contains(mouse)){
          screen=STORYONE;
        }
        //if instcruction button is pressed, goes to instruction page
        else if(instruc.contains(mouse)){
          screen=INSTRUC;
        }
        //if credits button is pressed, goes to credits page
        else if(cred.contains(mouse)){
          screen=CRED;
        }
        else if(story.contains(mouse)){
          screen=STORY;
        }
      }
      else if(screen==CRED){
        //if menu button is pressed, proceeds to menu page
        if(menu.contains(mouse)){
          screen=MENU;
        }
      }
      else if(screen==INSTRUC){
        if(menu.contains(mouse)){
          screen=MENU;
        }
        //if next button on instruction page is pressed, proceeds to next instruction page
        else if(instruc2.contains(mouse)){
          screen=INSTRUC2;
        }
      }
      //if the page is set to story page, click menu button to go back to menu
      else if(screen==STORY){
        if(menu.contains(mouse)){
          screen=MENU;
        }
      }
      //if back button on second instruction page is pressed, proceeds to first instruction page
      else if(screen==INSTRUC2){
        if(backI.contains(mouse)){
          screen=INSTRUC;
        }
      }
      //if the screen is the first storypage when you hit the play button, click the level one button to actually play level 1
      else if(screen==STORYONE){
        if(levelO.contains(mouse)){
          screen=ONE;
        }
      }
      //same deal after level 1, you will be redirected to level 2 story, click on the button to actually play level 2
      else if(screen==STORYTWO){
        if(levelT.contains(mouse)){
          screen=TWO;
        
        }
      }
      
    }
    public void mouseReleased(MouseEvent e){}
    public void lifeLost(){//checking for a life lost
      
      if(health<=0 && lives!=0){//if health is 0 and the number of lives has not reached 0 yet
        lives--;//number of lives reduced by 1
        for(int i=0;i<lived.size();i++){//going through lives arrayList
         lived.remove(lived.get(i)); //removes a live that is displayed on screen
        }
        health=100;//health will reset to 100
        
        
      }
      
    }
    public void collisions(){//checking for collision
      Rectangle enemyRect=new Rectangle(enemyX,enemyY,0,0);//creating a default rect for enemy
      Rectangle ebRect=new Rectangle(enemyBulletX,enemyBulletY,25,25);//creates a rect for the enemyBullet
      for(int j=0;j<enemy.size();j++){
        if(enemy.get(j)==enemyDemon){//if enemy is the demon
          enemyRect=new Rectangle(enemyX,enemyY,100,110); //creates a rectangle based on its coordinates and size
          
          
        }
       else if(enemy.get(j)==enemyJet){//same for jet
          enemyRect=new Rectangle(enemyX,enemyY,80,110); 
          
        }
       else if(enemy.get(j)==enemyTank){//same for tank
          enemyRect=new Rectangle(enemyX,enemyY,150,90); 
          
        }
      }
      //im not sure if i need this tho where i create a rectangle for every enemy bullet on the arrayList
      for(int s=0;s<eBullets.size();s++){
        ebRect=new Rectangle(enemyBulletX,enemyBulletY,25,25);
      }
      
     Rectangle playerRect=new Rectangle(playerX,playerY,100,90);//rectangle  for the player
     Rectangle bulletRect=new Rectangle(bulletX,bulletY,25,25);//rectangle for bullets
     if(bulletRect.intersects(enemyRect)){//if collision occurs
       for(int i=0;i<enemy.size();i++){//going through bullets and enemy list
         for(int k=0;k<bullets.size();k++){
           enemy.remove(enemy.get(i));//remove enemy and bullets
           enemyY=0;
           enemyCounter--;//1 less enemy in arrayList
           bullets.remove(bullets.get(k));
         
        
         }
       }
     }
     if(playerRect.intersects(enemyRect)){//when there is a head on collision with the enemy itself and the player
       for(int w=0;w<enemy.size();w++){
         if(enemy.get(w)==enemyDemon){//if the enemy is of the demon type
           health-=35;//health loss is 35
         }
         else if(enemy.get(w)==enemyTank){//if enemy is of the tank type
           health-=30;//health loss is 30
         }
         else if(enemy.get(w)==enemyJet){//if enemy is of the jet standard type
           health-=25;//health loss is 25
         }
         enemy.remove(enemy.get(w));//that enemy will be removed because you know when a plane crashes, it stays dead......
         enemyY=0;//enemy y position resets at top for the next enemy to spawn
         enemyCounter--;//enemy counter will decrease by 1
         
       }
     }
     if(ebRect.intersects(playerRect)){//if the player collides with an enemy bullet
       for(int a=0;a<enemy.size();a++){
         for(int d=0;d<bullets.size();d++){
           if(enemy.get(a)==enemyDemon){//if the current enemy that "fired" the bullet is of the demon
             health-=10;//health loss is 10
           }
           else if(enemy.get(a)==enemyTank){//if enemy that "fired" the bullet is of the tank
             health-=20;//health loss is 20
           }
           else if(enemy.get(a)==enemyJet){
             health-=15;//health loss is 15 if bullet fired from enemy is the regular jet
           }
           eBullets.remove(eBullets.get(d));//removing that enemy bullet
         }
       }
     }
     
    }
    
     public static int randint(int low, int high){//of course, this method is from the teacher 
       return (int)(Math.random()*(high-low+1)+low);
     } 
     public void winLevel(){//when all enemies are defeated, the screen will proceed to the story before the 2nd level
       if(enemyCounter==0){
         screen=STORYTWO;
       }
       
     }
     //contains all the methods for level one once the level starts
     public void levelOne(){
       if(screen==ONE && frameCounter>=250){//until the text of objective disappears, nothing will happen
         enemyFire();
         spawnEnemy();
         setBoundary();
         move();
         lifeLost();
         collisions();
         winLevel();
         
       }
     }
      
    
     //method to call all the methods for level two
     public void levelTwo(){
       if(screen==TWO){
         moveTwo();
         setBoundary();
         bossDirections();
         collideTwo();
         missileIncoming();
         bossFire();
         lifeLost2();
         regenForBossFight();
       }
     }
   
     public void moveTwo(){//movement for the second level(includes movement for player, boss, bullets, missiles, etc.)
       if(keys[KeyEvent.VK_RIGHT] ){
         playerX+=8;
         bulletX=playerX;
       }
       if(keys[KeyEvent.VK_LEFT] ){
         playerX-=8;
         bulletX=playerX;
         
       }
       if(keys[KeyEvent.VK_UP] ){
         playerY-=5;
         
       }  
       if(keys[KeyEvent.VK_DOWN] ){
         playerY+=5;
         
         
       }
       if(bullets.size()>=1){//if there are more than or equal to 1 bullet(s) in the arrayList
         for(int i=0;i<bullets.size();i++){//going through every bullet
           bulletY-=40;//y value for the bullets decreases
           
           if(bulletY<=0){//once it gets to below or at 0
             
             bullets.remove(bullets.get(i));//removing that bullet
             bulletY=playerY;//that bulletY will reset to playerY
             bulletX=playerX;//same deal for X
             
           }
           
         }
         
       }
       
       if(mList.size()>=1){//if there is a missile in the list or more than 1 missile in list
         for(int k=0;k<mList.size();k++){
           missileY+=20;//missile will travel down the screen
           if(missileY>=600){//if the missile exits the screen, without any collisions happening
             mList.remove(mList.get(k));//missile will be removed
             missileX=randint(100,700);//missile x coordinate will be random so they wont always be coming from 1 spot on screen
             missileY=-200;//missile y will reset to top, beyond screen
           }
         }
       }
       if(bList.size()>=1){//if there is 1 or more bossBullets in list
         for(int t=0;t<bList.size();t++){
           bBulletY+=30;//bossBullet will fly down screen
           if(bBulletY>=580){//if its past the screen(almost) 
             bBulletX=bossX+215;//bossBullet will reset based on its x+215
             bBulletY=bossY+240;//and based on y+240(makes it look like its actually shooting at front) 
             bList.remove(bList.get(t));//obviously remove bullet from screen
             
           }
         }
       }
       
      
       //the boss is outside of the screen, so it will make it way down to and on the screen and then the battle will commence! 
       if(bossY<50){
         bossY+=5;
       }
       //once the boss reaches the screen at that point of y 50, it will instantly move to the right
       if(bossY==50){
         bossDir=RIGHT;
       }
       //***************Of Course, I had help from Mr.Mckenzie for this*****************\\
       //if the boss is in the right direction
       if(bossDir==RIGHT){
         if(bossX==400){//if the boss is at x 400 which is basically where it practically/looks like its touching the border of the screen, it will move to the left
           bossY=51;//thanks to the help of Mr.McKenzie, the boss will move down a little to y 51 and it will still look like the boss is at the same location of y 50 because of how big it is
           bossDir=LEFT;//the boss will change it's direction to the left
         }
       }
       else if(bossDir==LEFT){//once it reaches the other border at x -50 where it looks like the boss is colliding with the border on the left side, the boss will move to the right
         if(bossX==-50){
           bossDir=RIGHT;
         }
       }
       //essentially its just a ping pong notion as the boss moves from the right to the left and vice versa
         }
     public void missileIncoming(){//this method is for whenever a missile should be added to the screen 
       mCount+=1;
       if(mCount%250==0){
         mList.add(missile);
       }
       if(mCount>=250){
         mCount=0;
       }
     
       
       
     }
     public void bossFire(){//this method is whenever the boss fires its main minigun where it will constant spew out bullets before "overheating"
       oh-=1;//at the start of game overheat counter will continiously drop
       //so basically the player will have 2s of breathing room right at the start to hit the boss as much as they can before the shooting begins
       if(bossY>=50){//of course the shooting happens when the boss is at y 50
         if(oh<=0){//once the overheat is below or at 0 , startFire counter will increase 
         startFire+=1;
         }
       }
       
       
       if(startFire%10==1){// when startFire counter%10==1, bullet will be fired from the boss
         
         bList.add(bBullet);
         
         if(startFire>=500){
           oh=125;//at 500 count for startFire, startFire will reset and overHeat will go back to 200
           //this now makes it to where the player have a 1.25 second breathing room to shoot boss as much as they can before the boss goes back on a shooting frenzy
           
           startFire=0;
         }
         
       }
       
     }
       
         
     public void bossDirections(){
       if(bossDir==RIGHT){//if boss direction is right, then the boss will slowly move to the right
         bossX+=2;
         bBulletX=bossX+215;//bullet x of the boss will be the same as the boss x+215 to make it actually look like the boss is shooting from the front
         }
         
       else if(bossDir==LEFT){//same with this but whenever direction is left, the boss will slowly move to the left
         bossX-=2;
         bBulletX=bossX+215;
     }
     }
     public void collideTwo(){//for all the collisions that takes place for level 2
       Rectangle bossHitBox=new Rectangle(bossX+100,bossY+20,25,30);//this hit box is for when ever the missiles hit boss if that ever happens(which is more likely than you realize)
       Rectangle bulletRect=new Rectangle(bulletX,bulletY,25,25);//rectangle for bullets
       Rectangle missRect=new Rectangle(missileX,missileY,25,100);//rectangle for the missiles
       Rectangle playRect=new Rectangle(playerX,playerY,100,90);//rectangle for the player
       Rectangle bBulletRect=new Rectangle(bBulletX,bBulletY,25,25);//rectangle for the boss's bullets
       Rectangle bossRect=new Rectangle(bossX+122,bossY+25,195,250);//rectangle for the boss(the + x and y coordinates is also sort of like a hitbox for the player to actually hit the boss with)
       //the reason why i did this is because if i leave it at the bossX and bossY, this will mean the entire boss image, including all the empty void holes in the image
       //so to make it look like you are actually hitting the boss and not the void holes in the image or the chopper blades, i decided to do this
       for(int i=0;i<bullets.size();i++){
         if(bulletRect.intersects(bossRect)){
           bossHealth-=20;
           bullets.remove(bullets.get(i));
         }
       }
       for(int j=0;j<mList.size();j++){//theres a dual possibility for missile collision; for both the player and the boss itself
         if(missRect.intersects(playRect)){
           health-=35;//obviously to the player, big damage will be dealt
           mList.remove(mList.get(j));
           missileY=-200;//missile coordinate will be restarted at -200 y and will then slowly proceed to make it back onto the screen
          
         }
         if(missRect.intersects(bossHitBox)){//the boss has prepared for anything should his own missiles hit him, he shall take very little damage as he is very resistant to his own missiles
           bossHealth-=5;//same deal for the boss, but just so that the player doesn't "cheat" by camping in the corner and wait for the missiles to kill the boss without doing anything
           //only little damage will be applied on to the boss, plus the added fact that there is a small hitbox the missiles must hit in order for damage to the boss to actually be applie
           mList.remove(mList.get(j));
           missileY=-200;
         }
       }
       for(int k=0;k<bList.size();k++){
         
         if(bBulletRect.intersects(playRect)){//if the boss bullet collides with the player
           health-=10;//the player will only lose 10 health, which doesn't seem like much, but the player should still avoid getting shot as the boss has fast fire rate and that multiple bullets
           //could hit the player at once without knowing(the fire rate is the minigun strength)
           bList.remove(bList.get(k));
            
         }
       }
     }
     public void lifeLost2(){//whenever a life has been lost in the second level(same functionality as the lifeLost method for the first level)
       if(health<=0 && lives!=0){
         lives--;
         health=100;
         for(int i=0;i<lived.size();i++){
           lived.remove(lived.get(i));
         }
       }
     }
     
     //when the game has been lost
     public void gameLost(){
       //the usual if lives is 0 and no lives are shown and if the health gets to 0 or below, YOU LOSE!
         if(health<=0 && lived.size()==0 && lives<=0){
           screen=LOST;
         }
         //this is a rare situation and usually happens if no enemies were shot down or rammed but if they pass the white line border as if an enemy crosses it, u lose a life
         if(health>0 && lives<0 && lived.size()==0){
           screen=LOST;
         }
         //if ammo reaches 0, you lose (because you know......you're basically useless without any ammo)
         if(playerBullets<=0){
           screen=LOST;
         }
       
     }
     //when the game has been beaten
     public void gameWin(){
       if(screen==TWO){
         if(bossHealth<=0){//if the boss health reaches below or equal to 0, YOU WIN!
           screen=WIN;
         }
       }
     }
     
     public void regenForBossFight(){//just so the player actually have a better chance at survival, the health will regenerate and cap out at 100 (if it's below 100)from the last level
       if(frameCount<=180 && health<100){
         health+=1;
       }
       
     }
    public void paintComponent(Graphics g){ 
      
     //this will only draw when the screen is set to level one
     if(screen==ONE){
       g.drawImage(back,0,0,null); //drawing background
     
     g.setColor(Color.WHITE);//setting color to white
     if(enemy.size()>=1){
       for(int k=0;k<enemy.size();k++){
         
         g.drawImage(enemy.get(k),enemyX,enemyY,null);//drawing an enemy if there is 1 or more in arrayList
       }
     }
     
     g.drawImage(playerJet,playerX,playerY,null);//drawing player at it's coords
     for(int j=0;j<lived.size();j++){//going through lives array
       g.drawImage(lived.get(j),750-(50*j),570,null);//displaying both lives at bottom
     
     }
     if(frameCounter<=200){//text to display for level 1 that appears before level 1 starts
       g.setFont(bossText);
       g.setColor(Color.RED);
       g.drawString("Eliminate All Enemies To Proceed",50,300);
       
     }
     g.setColor(Color.BLUE);
     g.setFont(fontSys);//setting the font 
     g.drawString("Ammo: "+playerBullets,5,600);//showing how much ammo the player has currently
     g.setColor(Color.WHITE);
     g.drawLine(0,575,800,575);//drawing a line above lives and ammo(reason: thinking of doing it where the player instantly loses a life if enemy cross this line "border")
     g.setColor(Color.BLACK);
     g.drawRect(349,584,101,21);//this is like a container or outline to solve any confusion on how much health you have left in the current life
     g.setColor(Color.RED);//setting color to red
     g.fillRect(350,585,health,20);//drawing a rectangle as wide as the amount of health it says it has
     g.drawString("Enemy: "+enemyCounter,500,600);//shows how many enemies are left remaining on the screen
     
     if(bullets.size()>=1){//if there are more than 1 bullet
       for(int i=0;i<bullets.size();i++){//going through every bullet
         
         g.drawImage(bullets.get(i),bulletX+40,bulletY,null);//drawing that bullet on screen with regards to bullet's coords
       }
     }
     if(eBullets.size()>=1){//drawing the enemy's bullet when it appears
       for(int q=0;q<eBullets.size();q++){
         g.drawImage(eBullets.get(q),enemyBulletX,enemyBulletY,null);
         
       }
     }
     
     
    }
     if(screen==TWO){
       g.drawImage(back,0,0,null);
       g.drawImage(enemyBoss,bossX,bossY,null);//draws boss and player
       g.drawImage(playerJet,playerX,playerY,null);
       g.setColor(Color.BLUE);
       g.setFont(fontSys);//setting the font 
       g.drawString("Ammo: "+playerBullets,5,600);//showing how much ammo the player has currently
       g.setColor(Color.BLACK);
       g.drawRect(349,584,101,21);//this is like a container or outline to solve any confusion on how much health you have left in the current life
       g.setColor(Color.RED);//setting color to red
       g.fillRect(350,585,health,20);//drawing a rectangle as wide as the amount of health it says it has
       if(bullets.size()>=1){//if there are more than 1 bullet
         for(int i=0;i<bullets.size();i++){//going through every bullet
           
           g.drawImage(bullets.get(i),bulletX+40,bulletY,null);//drawing that bullet on screen with regards to bullet's coords
         }
     }
       for(int j=0;j<lived.size();j++){//going through lives array
       g.drawImage(lived.get(j),750-(50*j),570,null);//displaying both lives at bottom
     
     }
       if(frameCount<=180){//in this frame while the counter is increasing, it will show a text to show objective of what to do(for the 2nd level)
         g.setFont(bossText);
         g.setColor(Color.RED);
         g.drawString("BOSS STAGE",300,250);
         g.setFont(fontSys);
         if(frameCount>=50){
           g.drawString("Eliminate Boss To Win or Die a Gruesome Death!",100,300);
         }
       }
       if(bossY>=50){//when the boss is at y 50, it will show its healthbar
         g.setColor(Color.RED);
         g.fillRect(200,5,bossHealth,30);
       }
       if(mList.size()>=1){//draws the missile on the screen if there is any
         for(int q=0;q<mList.size();q++){
           g.drawImage(mList.get(q),missileX,missileY,null);
         }
       }
       if(bList.size()>=1){//same for the boss's bullet
         for(int e=0;e<bList.size();e++){
           g.drawImage(bList.get(e),bBulletX,bBulletY,null);
         }
       }
       
     }
     //when the screen is at the menu screen
     if(screen==MENU){
       g.drawImage(menuBack,0,0,null);//draws the background menu
       
       g.setColor(Color.RED);
       
       //the three buttons(instruction, credits and level one play button)
       g.drawRect(instruc.x,instruc.y,instruc.width,instruc.height);
       g.drawRect(cred.x,cred.y,cred.width,cred.height);
       g.drawRect(play.x,play.y,play.width,play.height);
       g.drawRect(story.x,story.y,story.width,story.height);
       g.setFont(fontSys);//set font
       g.drawString("PLAY",350,230);//text for each of the 4 buttons
       g.drawString("INSTRUCTIONS",300,330);
       g.drawString("CREDITS",350,430);
       g.drawString("STORY",350,530);
       g.setColor(Color.WHITE);
       g.drawString("AERIAL SHOOTER",280,50);//the title of the game
      //highlights the button if the mouse is above it
       if(play.contains(mouse)){
         g.setColor(Color.BLUE);
         g.drawRect(play.x,play.y,play.width,play.height);  
         g.drawString("PLAY",350,230);
       }
       else if(instruc.contains(mouse)){
         g.setColor(Color.BLUE);
         g.drawRect(instruc.x,instruc.y,instruc.width,instruc.height);  
         g.drawString("INSTRUCTIONS",300,330);
       }
       else if(cred.contains(mouse)){
         g.setColor(Color.BLUE);
         g.drawRect(cred.x,cred.y,cred.width,cred.height);   
         g.drawString("CREDITS",350,430);
       }
       else if(story.contains(mouse)){
         g.setColor(Color.BLUE);
         g.drawRect(story.x,story.y,story.width,story.height);  
         g.drawString("STORY",350,530);
       }
       g.drawImage(logo,640,20,null);//draws my personalized logo
       
     }
     if(screen==CRED){
       g.drawImage(credBack,0,0,null);
       g.setColor(Color.WHITE);
       g.setFont(bossText);
       g.drawString("CREDITS",300,50);
       g.setFont(fontSys);
       g.drawString("Credit Maker: Muhammad Umar Khan",100,150);
       g.drawString("Game Developer: Muhammad Umar Khan",100,200);//writes the credits 
       g.drawString("Logo Creator: Muhammad Umar Khan",100,250);
       
       g.drawRect(menu.x,menu.y,menu.width,menu.height);//draws the menu button
       g.drawString("MENU",12,55);
       //highlight the button if mouse is hovering above it
       if(menu.contains(mouse)){
         g.setColor(Color.BLUE);
         g.drawRect(menu.x,menu.y,menu.width,menu.height);
         g.drawString("MENU",12,55);
       }
       g.drawImage(logo,640,30,null);//draws my logo
     }
     if(screen==STORY){
       g.drawImage(menuBack,0,0,null);
       g.setColor(Color.BLACK);
       g.setFont(fontSys);
       g.drawRect(menu.x,menu.y,menu.width,menu.height);//menu button again 
       g.drawString("MENU",12,55);
       if(menu.contains(mouse)){
         g.setColor(Color.BLUE);
         g.drawRect(menu.x,menu.y,menu.width,menu.height);
         g.drawString("MENU",12,55);
       }
       g.setFont(bossText);
       g.setColor(Color.BLUE);
       g.drawString("PROLOGUE",300,50);
       g.setFont(fontSys);
       g.setColor(Color.BLACK);
       //the story caption for the PROLOGUE
       g.drawString("Long ago, on the planet of Tyrium, five nations lived",20,125);
       g.drawString("together in harmony.The world was at peace until the",20,170);
       g.drawString("nation of Castershal attacked the others led by Supreme",20,215);
       g.drawString("General Rowin HavenThorn,the last member of the Royal",20,260);
       g.drawString("Imperial Family(ROA).He sought a future for the most loyal",20,305);
       g.drawString("but the smallest nation on the planet,conquering the world,",20,350);
       g.drawString("and take their place among the stars as the greatest force to",20,395);
       g.drawString("ever live. However,there is hope.The last line of defense",20,440);
       g.drawString("to save the planet from Romin's tyranny,the noble people",20,485);
       g.drawString("of Alfenwerch.The future and survival of Tyrium is in their",20,530);
       g.drawString("hands.",20,575);
       //......just to make the beginning story page less dull
       g.drawImage(playerJet,200,560,null);
       g.drawImage(playerJet,500,560,null);
       g.setFont(bossText);
       g.setColor(Color.RED);
       g.drawString("TO WAR",300,600);
     }
     //all you need to know page about the player and the enemies
     if(screen==INSTRUC){
       g.drawImage(menuBack,0,0,null);
       g.setColor(Color.WHITE);
       g.setFont(fontSys);
       g.setColor(Color.BLACK);
       g.drawString("HOW TO PLAY:PLAYERS & ENEMIES",120,60);
       g.drawRect(menu.x,menu.y,menu.width,menu.height);//menu button again 
       g.drawString("MENU",12,55);
       if(menu.contains(mouse)){
         g.setColor(Color.BLUE);
         g.drawRect(menu.x,menu.y,menu.width,menu.height);
         g.drawString("MENU",12,55);
       }
       //draws the next button that will take you to next page and highlights the button if mouse is touching it
       g.setColor(Color.BLACK);
       g.drawRect(instruc2.x,instruc2.y,instruc2.width,instruc2.height);
       g.drawString("NEXT",685,55);
       if(instruc2.contains(mouse)){
         g.setColor(Color.BLUE);
         g.drawRect(instruc2.x,instruc2.y,instruc2.width,instruc2.height);
         g.drawString("NEXT",685,55);
       }
       g.setColor(Color.BLUE);
       g.drawString("PLAYER",630,250);
       g.drawString("PLAYER",10,160);
       g.setColor(Color.RED);
       g.drawString("ENEMY",350,100);//anything in CAPS, just take it as its a "title" for the section
       g.drawString("BOSS",350,410);
       g.setColor(Color.BLACK);
       g.drawImage(playerJet,10,200,null);//draws the player 
       g.drawString("MOVEMENT",10,200);//movement section
       g.drawImage(arrow,10,300,null);//arrow keys to display how you move character
       g.drawString("LIVES",10,525);//shows how many lives the player will have when game is loaded
       g.drawImage(live1,10,530,null);
       g.drawImage(live2,50,530,null);
       
       g.drawImage(enemyJet,270,200,null);//shows the 3 types of enemies
       g.drawImage(enemyDemon,350,200,null);
       g.drawImage(enemyTank,430,200,null);
       
       g.drawImage(enemyBullet,290,330,null);//draws a bullet for each enemy
       g.drawImage(enemyBullet,370,330,null);
       g.drawImage(enemyBullet,490,330,null);
       g.drawString(" Jet       Demon      Tank  ",250,180);//label for which enemy is which
       
       g.drawImage(boss,330,410,null);//draws the boss, missile,and missile(obviously these are shrunked ones since i cant get the original big versions of them to fit in 1 screen)
       g.drawImage(smallMiss,310,450,null);
       g.drawImage(smallMiss,450,450,null);
       g.drawImage(bBullet,380,500,null);
       
       
       g.drawString("Shooting",630,290);//shooting section
       g.drawImage(bullet,630,350,null);//draws bullets to show that player will shoot bullets
       g.drawImage(bullet,670,310,null);
       g.drawImage(bullet,700,300,null);
       g.drawImage(playerJet,630,380,null);
       g.drawImage(spaceB,630,480,null);//space bar to show how will the player shoot
       g.setColor(Color.RED);//setting color to red
       g.fillRect(10,450,100,20);//drawing a rectangle as wide as the amount of health it says it has
       g.drawString("(100)",120,470);//the health 
       g.drawString("HEALTH",10,430);
       
       g.setColor(Color.BLACK);
       g.drawString("Ammo: 80",600,560);
       
       //separate for the description text
       g.setFont(text);
       g.drawString("FYI:Do not let enemies cross",10,90);//basically what im saying here is that if the enemy gets close, dont avoid them as there are consequences
       //this is like the "Oh no!Player,how can you do this do us, you let an enemy through to destroy us, you were brought in shame,and for that a life has been sacrificed"part of the game
       g.drawString("Border line under player.",10,105);
       g.drawString("You will instantly lose a life",10,120);
       
       g.setColor(Color.RED);
       g.drawString("500 Health",470,410);
       
       g.setColor(Color.BLACK);
       g.drawString("Current Ammo Count",600,580);
       g.drawString("Use Keys for Basic Movement",10,375);
       g.drawString("How Much Health You Have",10,490);
       g.drawString("How Many Lives You Have",10,590);
       g.drawString("Press Space to Fire a Bullet",600,520);
       g.drawString("15 Damage",240,370);//damage numbers on player when enemy hits you (jet)
       g.drawString("10 Damage",360,370);//demon
       g.drawString("20 Damage",490,370);//tank
       g.drawString("35 Damage",260,510);//boss
       g.drawString("10 Damage",410,530);//also for boss
       
       g.drawString("Enemy Collisions are Brutal, Don't Get Rammed",260,140);//indicate a warning to quickly shoot enemy before they collide with you as it hurts
       g.drawString("Will Move ",490,450);
       g.drawString("Left to Right",490,470);
       g.drawString("Looks Can be Decieving,",430,570);
       g.drawString("Bigger Than Normal",430,590);//be Prepared: the size that is on the instruction page for the boss is much smaller than in reality when you fight the boss in the level
       g.setFont(fontSys);
       g.drawString("DISCLAIMER:",220,590);
       
       g.drawImage(logo,630,90,null);//draw my logo
      
     }
     //second page is all about the objectives and what to do
     if(screen==INSTRUC2){
        g.setColor(Color.WHITE);
        g.drawImage(menuBack,0,0,null);
        g.setFont(fontSys);
        g.setColor(Color.BLACK);
        g.drawString("HOW TO PLAY:OBJECTIVES AND PROGRESS",120,60);
        
        g.drawRect(backI.x,backI.y,backI.width,backI.height);//back button to go back to previous page
        g.drawString("BACK",12,55);
        if(backI.contains(mouse)){
          g.setColor(Color.BLUE);
          g.drawRect(backI.x,backI.y,backI.width,backI.height);
          g.drawString("BACK",12,55);
        }
        g.setColor(Color.BLACK);
        g.drawLine(300,80,300,600);
        g.drawLine(0,80,300,80);
        g.drawString("LEVEL ONE: WIN",0,110);//shows everything of how to win(enemies counter has to be 0)
        g.setColor(Color.RED);
        g.drawString("ENEMY: 20",10,160);
        g.drawImage(enemyJet,10,200,null);
        g.drawImage(enemyTank,80,200,null);
        g.drawImage(enemyDemon,210,200,null);
        
        g.setColor(Color.BLACK);
        g.drawString("LEVEL ONE: LOST",0,350);//how do you lose level 1(health and lives is 0, ammo reaches 0, etc.)
        g.drawImage(live1,10,370,null);
        g.drawImage(live1,55,370,null);
        g.drawImage(arrowR,90,370,null);
        g.drawString("00",150,400);
        g.drawString("AND",90,430);
        g.setColor(Color.RED);
        g.fillRect(0,450,100,20);
        g.drawImage(arrowR,105,440,null);
        g.drawString("00",160,470);
       
        
        g.setColor(Color.BLACK);
        g.drawString("AND/OR",80,500);
        g.drawString("AMMO:80",0,530);
        g.drawImage(arrowR,170,505,null);
        g.drawString("00",230,530);
        g.drawString("GAME OVER",50,590);
        
        
        //seperate for the description text for the Level One Section(for each category(won or lost))
        g.setFont(text);
        g.setColor(Color.BLACK);
        g.drawString("Eliminate All of The Enemies",10,180);
        g.drawString("If Lives",190,400);
        g.drawString("And Health",190,420);
        g.drawString("Reaches Zero",190,440);
        g.drawString("If Ammo Count Reaches Zero",10,550);
        //how do you win level 2(boss eliminated)
        g.setFont(fontSys);
        g.setColor(Color.BLACK);
        g.drawLine(510,80,510,700);
        g.drawLine(510,80,900,80);
        g.drawString("LEVEL TWO: WIN",520,110);
        
        g.setColor(Color.RED);
        g.fillRect(513,130,270,20);
        g.drawString("(500)",600,180);
        g.drawImage(smallBoss,530,200,null);
        
        g.setColor(Color.BLACK);
        g.drawString("Defeat The Boss to",520,400);
        g.drawString("Win",620,440);
        //how you lose level 2
        g.drawString("LEVEL TWO: LOST",512,470);
        
        g.setFont(text);
        g.drawString("Lose The Same Way As In Level One",512,500);
        g.drawString("But Will Be Harder,",512,520);
       
        
        g.setFont(fontSys);
        //some title and labels
        g.drawString("SIDE NOTE",325,100);
        g.drawString("VICTORY",325,320);
        g.drawString("Complete Both",305,360);
        g.drawString("Levels To Win",305,390);
        
        g.setFont(text);
        //a side note you need to remember
        g.drawString("Whatever Happened To The",305,140);
        g.drawString("Player At Level One Will",305,160);
        g.drawString("Be Brought On To The Second",305,180);
        g.drawString("Level (Ammo,Health,Lives,etc.)",305,200);
        g.drawString("So Be Sure You Are Cautious At",305,240);
        g.drawString("The Player's Stats",305,260);
        g.drawString("Dont Be Reckless",340,280);
        
        g.drawImage(logo,330,450,null);//my logo
        
       }
     if(screen==STORYONE){
      g.drawImage(nationBack,0,0,null);;
      g.setColor(Color.WHITE);
      g.setFont(fontSys);
      g.drawRect(levelO.x,levelO.y,levelO.width,levelO.height);
      
      g.drawString("LEVEL ONE",590,50);
      if(levelO.contains(mouse)){
        g.setColor(Color.BLUE);
        g.drawRect(levelO.x,levelO.y,levelO.width,levelO.height);
      
        g.drawString("LEVEL ONE",590,50);
        
      }
      g.setColor(Color.WHITE);
      g.setFont(bossText);
      //the story before you actually play first level
      g.drawString("TROUBLE INCOMING",50,75);
      g.setFont(fontSys);
      g.drawString("The last frontal assault on Alfenwerch is here! General",20,120);
      g.drawString("Romin has dispatched 20 of his top highly skilled and elite",20,160);
      g.drawString("fighter squadron to take over Alfenwerch and to ensure",20,200);
      g.drawString("Global Domination,The Sonic Wolves(TSW).Fortunately,",20,240);
      g.drawString("the people of Alfenwerch has their only hope left in their",20,280);
      g.drawString("arsenal,the brave and courageous leader Captain Shankroff.",20,320);
      g.drawString("Now with TSW only minutes from arriving at Alfenwerch,",20,360);
      g.drawString("Captain Shankroff must fend off the invasion before they",20,400);
      g.drawString("are doomed to fall into the hands of the mad tyrant.",20,440);
      g.drawImage(logo,350,480,null);
      
    }
     if(screen==STORYTWO){
      g.drawImage(nationBack,0,0,null);
      g.setColor(Color.WHITE);
      g.setFont(fontSys);
      g.drawRect(levelT.x,levelT.y,levelT.width,levelT.height);
      g.drawString("LEVEL TWO",590,50);
      if(levelT.contains(mouse)){
        g.setColor(Color.BLUE);
        g.drawRect(levelT.x,levelT.y,levelT.width,levelT.height);
      
        g.drawString("LEVEL TWO",590,50);
        
      }
      g.setColor(Color.WHITE);
      //story before level 2
      g.setFont(bossText);
      g.drawString("ROMIN HAVENTHORN",20,75);
      g.setFont(fontSys);
      g.drawString("With The Sonic Wolves(TSW) defeated, General Romin",20,120);
      g.drawString("gotten really angry with this news and couldn't bear to",20,160);
      g.drawString("handle the truth.If the people of Alfenwerch refuses",20,200);
      g.drawString("to surrender and accept their fate, then HavenThorn will deal",20,240);
      g.drawString("with the situation himself.HavenThorn has arrived within ",20,280);
      g.drawString("minutes, in his powerful and deadly chopper,ready to deal ",20,320);
      g.drawString("with Alfenwerch.Now the brave leader Captain Shankroff",20,360);
      g.drawString("has to deal with this problem even though he already took ",20,400);
      g.drawString("a beating from the last battle.Shankroff must battle",20,440);
      g.drawString("HavenThorn viciously to defend his home and his honour.But ",10,480);
      g.drawString("he should be wary as missiles from HavenThorn's Royal",20,520);
      g.drawString("Base are approaching.This will be the greatest battle in the ",20,560);
      g.drawString("history of Tyrium ",20,600);
     }
     if(screen==WIN){
       g.drawImage(wonBack,0,0,null);
       g.setColor(Color.BLUE);
       g.setFont(fontSys);
       g.drawString("EPILOGUE",300,50);
       g.setColor(Color.WHITE);
       //Epilogue text when you win
       g.drawString("With HavenThorn defeated,order and peace has been ",10,90);
       g.drawString("restored to the planet of Tyrium, and the five individual",10,120);
       g.drawString("nations were brought back into their normal lives, to live ",10,150);
       g.drawString("together in peace and harmony.Now all is next is what the ",10,180);
       g.drawString("future of Tyrium has in store for them ",10,210);
       g.drawString("Glory of Tyrium",290,320);
       //decorative images to make screen page of WIN more dramatic and nicer
       g.drawImage(tyrium,300,340,null);
       g.drawImage(logo,10,480,null);
       g.drawImage(logo,640,480,null);
       g.setColor(Color.BLUE);
       g.setFont(bossText);
       g.drawString("Congratulations! You Won!",100,270);
     }
     if(screen==LOST){
       g.drawImage(lostBack,0,0,null);
       
       g.setFont(fontSys);
       g.setColor(Color.BLUE);
       g.drawString("Battle Lost,Tyrium Conquered",200,50);
       g.setColor(Color.WHITE);
       //"epilogue"when you lose the game
       g.drawString("With Alfenwerch's only line of defence defeated,HavenThorn",10,100);
       g.drawString("has successfully conquered the planet of Tyrium after the fall ",10,140);
       g.drawString("of the last nation.HavenThorn have completed his dream of",10,180);
       g.drawString("Global Domination.Tyrium has met it's burden and now there",10,220);
       g.drawString("is no sign of hope if the people will ever see a future again",10,260);
       g.setColor(Color.BLUE);
       g.setFont(bossText);
       g.drawString("YOU LOST,GAME OVER",100,330);
       //these are including some decorative images on the LOST page to make it more dramatic 
       g.drawImage(explode,280,320,null);
       g.drawImage(explode,-70,320,null);
       g.drawImage(explode,600,320,null);
       g.drawImage(destroy,-200,500,null);
       g.drawImage(destroy,500,500,null);
       g.drawImage(firee,0,570,null);
       g.drawImage(logo,190,400,null);
       g.drawImage(logo,510,400,null);
     }
     
       
    }
    
   
}
