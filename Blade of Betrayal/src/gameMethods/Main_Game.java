package gameMethods;

/*
 * Arya Vatan-Abadi
 * Martin O'Brien
 * CPT FINAL - Blade of Betrayal
 * January 18, 2016
 */

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Main_Game extends Applet
implements Runnable, KeyListener{

	private static final long serialVersionUID = 1L;
	int width, height;
	Image menu;
	Image controls;
	Image credits;
	Image levels;
	boolean mainMenuScreen = true;
	boolean options = false;
	boolean creditsScreen = false;
	boolean levelSelect = false;
	AudioClip mainTheme;
	int menuX, menuY, mx, my, velX, velY, by;
	//^^^menuX & menuY are used for navigating the various menus
	//^^^mx & my are the coordinates of the map
	//^^^velX and velY are the variables used for the velocity of the player
	//^^^by is used for navigating the battle menu
	Thread t = null;
	boolean running;
	AudioClip overworld;
	AudioClip battle;
	boolean up = false;
	boolean down = false;
	boolean left = false;
	boolean right = false;
	//^^^These booleans are used to determine which direction the player is moving
	boolean select = false;
	Image map;
	Image water;
	Image playerUP[] = new Image[3];
	Image playerDOWN[] = new Image[3];
	Image playerLEFT[] = new Image [3];
	Image playerRIGHT[] = new Image[3];
	//^^^These arrays are used to store the images required for the player animation
	Image guard, man, blacksmith, orc, healer, king, prince;
	Image story[] = new Image[19];
	//^^^This array holds all the story images (dialogue, prologue)
	Image arrow[] = new Image[8];
	//^^^This array holds images of arrows for the objective marker method
	Image dbImage;
	Graphics dbGraphics;
	int frame = 0;
	int playerSpeed = 9;
	int progress = 0;
	int checkpoint = -1;
	//^^^Checkpoint and progress are used to keep track of where the player is in the story...
	//...in order to make bosses harder and display the correct dialogue images 
	boolean dead = false;
	Image gameOver;
	boolean cutscene = false;
	Image battleGuard,battleKing,battlePrince,battleOrc,battleMonster;
	int hp = 100;
	int stamina = 5;
	int ehp = 100;
	int damage = 5;
	boolean inBattle = false;
	boolean randomEnemy = false;
	//^^^Used to determine if battle was triggered by a random enemy
	boolean battleMusic = false;
	boolean menuMusic = false;
	boolean worldMusic = false;
	//^^^Used to keep track of which song is currently playing and to change songs accordingly
	
	
	public void init(){
		width = 1280;
		height = 720;
		menuX = 250;
		menuY = 35;
		mx = 380;
		my = -2092;
		by = 530;
		velX = 0;
		velY = 0;
		setSize(width, height);
		setBackground(Color.black);
		
		mainTheme = getAudioClip(getDocumentBase(), "MTHEME.au");
		overworld = getAudioClip(getDocumentBase(), "Overworld.au");
		battle = getAudioClip(getDocumentBase(), "battle.au");
                
		menu = getImage(getDocumentBase(), "menu.jpg");
		controls = getImage(getDocumentBase(), "Controls.jpg");
		credits = getImage(getDocumentBase(), "Credits.jpg");
		levels = getImage(getDocumentBase(), "Continue.jpg");
		gameOver = getImage(getDocumentBase(), "GAMEOVER.jpg");
		
		map = getImage(getDocumentBase(), "map.jpg");
		water = getImage(getDocumentBase(), "Water.jpg");
		
		battleGuard = getImage(getDocumentBase(), "BattleGuard.jpg");
		battlePrince = getImage(getDocumentBase(), "BattlePrince.jpg");
		battleKing = getImage(getDocumentBase(), "BattleKing.jpg");
		battleMonster = getImage(getDocumentBase(), "BattleMonster.jpg");
		battleOrc = getImage(getDocumentBase(), "BattleOrc.jpg");
		
		for(int i = 0; i < 3; i++){
			playerUP[i] = getImage(getDocumentBase(), "Player-UP" + (i+1) + ".png");
			playerDOWN[i] = getImage(getDocumentBase(), "Player-DOWN" + (i+1) + ".png");
			playerLEFT[i] = getImage(getDocumentBase(), "Player-LEFT" + (i+1) + ".png");
			playerRIGHT[i] = getImage(getDocumentBase(), "Player-RIGHT" + (i+1) + ".png");
		}
		//^^^FOR loop used to quickly assign images to these arrays
		
		guard = getImage(getDocumentBase(), "Guard.png");
		man = getImage(getDocumentBase(), "Man.png");
		blacksmith = getImage(getDocumentBase(), "Blacksmith.png");
		orc = getImage(getDocumentBase(), "Orc.png");
		healer = getImage(getDocumentBase(), "Healer.png");
		king = getImage(getDocumentBase(), "King.png");
		prince = getImage(getDocumentBase(), "Prince.png");
		
		story[0] = getImage(getDocumentBase(), "Pregame.jpg");
		for(int i = 1; i < 19; i++){
			story[i] = getImage(getDocumentBase(), "Text" + i + ".jpg");
		}
		
		arrow[0] = getImage(getDocumentBase(), "up.png");
		arrow[1] = getImage(getDocumentBase(), "up right.png");
		arrow[2] = getImage(getDocumentBase(), "right.png");
		arrow[3] = getImage(getDocumentBase(), "down right.png");
		arrow[4] = getImage(getDocumentBase(), "down.png");
		arrow[5] = getImage(getDocumentBase(), "down left.png");
		arrow[6] = getImage(getDocumentBase(), "left.png");
		arrow[7] = getImage(getDocumentBase(), "up left.png");
		//I chose to not use a loop to populate this array because...
		//...I wanted to quickly be able to see which images were...
		//...assigned to which values of the array
		
		addKeyListener(this);
	}
	

	public void paint (Graphics g){
		
		if(cutscene == true){
			//draws all the story images (example: dialogue)
			if(dead){
				g.drawImage(gameOver, 0, 0, this);
			}
			else{
				g.drawImage(story[progress],0,0,this);
			}
		}
		else if(inBattle){
			//draws everything required for the battle screen
			if(randomEnemy){
				g.drawImage(battleMonster, 0, 0, this);
			}
			else if(progress == 4){
				g.drawImage(battleGuard, 0, 0, this);
			}
			else if(progress == 9){
				g.drawImage(battleOrc, 0, 0, this);
			}
			else if(progress == 16){
				g.drawImage(battleKing, 0, 0, this);
			}
			else if(progress == 17){
				g.drawImage(battlePrince, 0, 0, this);
			}
			g.setColor(Color.white);
			g.fillRect(225, by, 20, 20);
			g.setColor(Color.green);
			g.fillRect(910, 520, hp*3, 30);
			g.setColor(Color.cyan);
			g.fillRect(910, 560, stamina*10*3, 30);
			g.setColor(Color.red);
			g.fillRect(910, 665, ehp*3, 30);
		}
		else if(mainMenuScreen == false){
			//draws everything when the player is in free roam including NPCs...
			//depending on where the player is in the story
			g.drawImage(water, 0, 0, this);
			g.drawImage(map, mx, my, this);
			if(progress <= 3){
				g.drawImage(guard, mx+(width/2)-100, my+(height*4)-75, this);
			}
			else if(progress < 7){
				g.drawImage(man, mx+(width*2)+700, my+(height*2)-40, this);
			}
			else if(progress == 7){
				g.drawImage(blacksmith, mx+(width*4)-500, my+height+35, this);
			}
			else if(progress == 8){
				g.drawImage(guard, mx+(width/2), my+(height/2)-100, this);
				g.drawImage(orc, mx+(width/2)-80, my+(height/2)-100, this);
			}
			else if(progress == 10){
				g.drawImage(healer, mx+(width/2)-220, my+height-50, this);
				g.drawImage(guard, mx+(width/2)-100, my+height-50, this);
			}
			else if(progress == 11){
				g.drawImage(man, mx+(width*3)-550, my+(height*3)+150, this);
			}
			else if(progress <= 15){
				g.drawImage(king, mx+(width*2)+22, my+250, this);
				g.drawImage(prince, mx+(width*2)+45, my+375, this);
			}
			playerAnimation(g);
			objectiveMarker(g);
		}
		else{
			mainMenu(g);
		}
	}
	

	public void update(Graphics g) {
		//This entire method is for the double buffer so that...
		//...the game doesn't flicker
	    if (dbImage == null) {
	        dbImage = createImage(this.getWidth(),this.getHeight());
	        dbGraphics = dbImage.getGraphics();
	    }
	    dbGraphics.setColor(getBackground());
	    dbGraphics.fillRect(0,0,this.getWidth(),this.getHeight());
	    dbGraphics.setColor(getForeground());
	    paint(dbGraphics);
	    g.drawImage(dbImage,0,0,this);
	}
	

	public void mainMenu (Graphics g){
		//This method draws everything needed for the main menu
		g.setColor(Color.white);
		if(levelSelect){
			g.drawImage(levels, 0, 0, this);
			g.fillRect(650, menuY, 20, 20);
		}
		else if(options){
			g.drawImage(controls, 0, 0, this);
			g.fillRect(400, 54, 20, 20);
		}
		else if(creditsScreen){
			g.drawImage(credits, 0, 0, this);
			g.fillRect(950, 675, 20, 20);
		}
		else{
			g.drawImage(menu, 0, 0, 1280, 720, this);
			g.fillRect(menuX, menuY, 20, 20);
		}
	}
	

	public void playerAnimation(Graphics g){
		//This method draws the player models in quick succession in order to...
		//...make it appear as if the player is actually walking
		if(left){
			if(frame >= 0 && frame < 101){
				g.drawImage(playerLEFT[0], width/2, height/2, this);
				frame += playerSpeed;
			}
			else if(frame > 100 && frame < 201){
				g.drawImage(playerLEFT[1], width/2, height/2, this);
				frame += playerSpeed;
			}
			else if(frame > 200 && frame < 301){
				g.drawImage(playerLEFT[2], width/2, height/2, this);
				frame += playerSpeed;
			}
			if(frame > 300){
				frame = 0;
			}
		}
		else if(right){
			if(frame >= 0 && frame < 101){
				g.drawImage(playerRIGHT[0], width/2, height/2, this);
				frame += playerSpeed;
			}
			else if(frame > 100 && frame < 201){
				g.drawImage(playerRIGHT[1], width/2, height/2, this);
				frame += playerSpeed;
			}
			else if(frame > 200 && frame < 301){
				g.drawImage(playerRIGHT[2], width/2, height/2, this);
				frame += playerSpeed;
			}
			if(frame > 300){
				frame = 0;
			}
		}
		else if(up){
			if(frame >= 0 && frame < 101){
				g.drawImage(playerUP[0], width/2, height/2, this);
				frame += playerSpeed;
			}
			else if(frame > 100 && frame < 201){
				g.drawImage(playerUP[1], width/2, height/2, this);
				frame += playerSpeed;
			}
			else if(frame > 200 && frame < 301){
				g.drawImage(playerUP[2], width/2, height/2, this);
				frame += playerSpeed;
			}
			if(frame > 300){
				frame = 0;
			}
		}
		else if(down){
			if(frame >= 0 && frame < 101){
				g.drawImage(playerDOWN[0], width/2, height/2, this);
				frame += playerSpeed;
			}
			else if(frame > 100 && frame < 201){
				g.drawImage(playerDOWN[1], width/2, height/2, this);
				frame += playerSpeed;
			}
			else if(frame > 200 && frame < 301){
				g.drawImage(playerDOWN[2], width/2, height/2, this);
				frame += playerSpeed;
			}
			if(frame > 300){
				frame = 0;
			}
		}
		else {
			g.drawImage(playerDOWN[0], width/2, height/2, this);
			frame = 0;
			//^^^If the player is not moving, the default stance is facing down
		}
	}
	

	public void objectiveMarker(Graphics g){
		int objectiveX = 0;
		int objectiveY = 0;
		if(progress == 3){
			objectiveX = 124;
			objectiveY = -2476;
		}
		else if(progress == 5){
			objectiveX = -2580;
			objectiveY = -1040;
		}
		else if(progress == 7){
			objectiveX = -3980;
			objectiveY = -388;
		}
		else if(progress == 8){
			objectiveX = 44;
			objectiveY = 108;
		}
		else if(progress == 10){
			objectiveX = 168;
			objectiveY = -300;
		}
		else if(progress == 11){
			objectiveX = -2640;
			objectiveY = -1948;
		}
		else if(progress >= 12){
			objectiveX = -1944;
			objectiveY = 140;
		}
		else{
			objectiveX = mx;
			objectiveY = my;
		}
	
		boolean north = false;
		boolean south = false;
		boolean west = false;
		boolean east = false;
		
		if(mx - objectiveX > 0){
			west = false;
			east = true;
		}
		else if(mx - objectiveX < 0){
			east = false;
			west = true;
		}
		else{
			east = false;
			west = false;
		}
		if(my - objectiveY > 0){
			north = false;
			south= true;
		}
		else if(my - objectiveY < 0){
			south = false;
			north = true;
		}
		else{
			south = false;
			north = false;
		}
		
		if(north){
			if(west){
				g.drawImage(arrow[7], 0, 0, this);
			}
			else if(east){
				g.drawImage(arrow[1], 0, 0, this);
			}
			else{
				g.drawImage(arrow[0], 0, 0, this);
			}
		}
		else if(south){
			if(west){
				g.drawImage(arrow[5], 0, 0, this);
			}
			else if(east){
				g.drawImage(arrow[3], 0, 0, this);
			}
			else{
				g.drawImage(arrow[4], 0, 0, this);
			}
		}
	}
	

	public void collision(){
		//THIS METHOD HANDLES ALL OF THE COLLISION BY SETTING THE VELOCITY TO ZERO...
		//...WHEN THE PLAYER REACHES A WALL OR AN UNPASSABLE OBJECT
		
		//MAIN MAP BORDERS
		if(left && mx >= width/2-65){
			velX = 0;
		}
		if(right && mx <= (-3.4 * width)+40){
			velX = 0;
		}
		if(up && my >= height/2-55){
			velY = 0;
		}
		if(down && my <= (-3.5*height)+45){
			velY = 0;
		}
		
		
		//THE CAVES
		if(up){
			//cave at the far left of the map
			if(mx <= 496 && mx >= 444 && my >= 308){
				mx = -4292;
				my = 276;
			}
			//cave at the far right of the map
			if(mx <= -4268 && my >= 308){
				mx = 468;
				my = 276;
			}
		}
				
		
		//PRISION BUILDING
		if(left){
			if(my < -1968 && my > -2400 && mx == -576){
				velX = 0;
			}
			else if (my <= -2092 && my >= -2240 && mx == 484){
				velX = 0;
			}
			else if (my <= -2240 && my >= -2284 && mx == 520){
				velX = 0;
			}
			else if (my <= -2188 && my >= -2236 && mx == 300){
				velX = 0;
			}
		}
		if(right){
			if(my <= -2244 && my >= -2280 && mx == 248){
				velX = 0;
			}
			else if(my < -1968 && my > -2400 && mx == 208){
				velX = 0;
			}
			else if (my <= -2160 && my >= -2244 && mx == 284){
				velX = 0;
			}
			else if (my <= -2092 && my >= -2160 && mx == 248){
				velX = 0;
			}
			else if (my <= -2188 && my >= -2236 && mx == 468){
				velX = 0;
			}
		}
		if(down){
			if(mx < 580 && mx > -576 && my == -1968){
				velY = 0;
			}
			else if (mx <= 284 && mx >= 248 && my == -2160){
				velY = 0;
			}
			else if (mx <= 464 && mx >= 304 && my == -2188){
				velY = 0;
			}
		}
		if(up){
			if(mx < 208 && mx > -576 && my == -2400){
				velY = 0;
			}
			else if(mx <= 248 && mx >= 208 && my == -2284){
				velY = 0;
			}
			else if(mx <= 284 && mx >= 248 && my == -2244){
				velY = 0;
			}
			else if (mx <= 484 && mx >= 248 && my == -2092){
				velY = 0;
			}
			else if(mx <= 520 && mx >= 484 && my == -2240){
				velY = 0;
			}
			else if(mx > 520 && my == -2284){
				velY = 0;
			}
			else if (mx <= 464 && mx >= 304 && my == -2236){
				velY = 0;
			}
			
		}//END OF BUILDING
			
			
		//LITTLE BUILDING DIRECTLY NORTH OF PRISION
		if(up){
			if(mx <= -224 && mx >= -612 && my == -1892){
				velY = 0;
			}
		}
		if(down){
			if(mx <= -540 && mx >= -612 && my == -1568){
				velY = 0;
			}
			else if(mx <= -428 && mx >= -540 && my == -1752){
				velY = 0;
			}
			else if(mx <= -384 && mx >= -428 && my == -1704){
				velY = 0;
			}
			else if(mx <= -336 && mx >= -384 && my == -1660){
				velY = 0;
			}
			else if(mx <= -296 && mx >= -336 && my == -1616){
				velY = 0;
			}
			else if(mx <= -224 && mx >= -296 && my == -1568){
				velY = 0;
			}
		}
		if(left){
			if(my <= -1568 && my >= -1892 && mx == -616){
				velX = 0;
			}
			else if(my <= -1704 && my >= -1752 && mx == -428){
				velX = 0;
			}
			else if(my <= -1660 && my >= -1704 && mx == -384){
				velX = 0;
			}
			else if(my <= -1616 && my >= -1660 && mx == -336){
				velX = 0;
			}
			else if(my <= -1572 && my >= -1616 && mx == -296){
				velX = 0;
			}
		}
		if(right){
			if(my <= -1568 && my >= -1752 && mx == -540){
				velX = 0;
			}
			else if(my <= -1568 && my >= -1892 && mx == -220){
				velX = 0;
			}
		}//END OF BUILDING
		
		
		//HEALER'S HOUSE/SHOP
		if(up){
			if(mx <= -140 && mx >= -200 && my == -596){
				velY = 0;
			}
			else if(mx <= 336 && mx >= 148 && my == -596){
				velY = 0;
			}
			else if(mx <= -112 && mx >= -136 && my == -504){
				velY = 0;
			}
			else if (mx <= -120 && mx >= -136 && my == -212){
				velY = 0;
			}
			else if (mx <= 56 && mx >= -116 && my == -256){
				velY = 0;
			}
			else if (mx <= 112 && mx >= 56 && my == -292){
				velY = 0;
			}
			else if (mx <= 220 && mx >= 112 && my == -256){
				velY = 0;
			}
			else if (mx <= 260 && mx >= 220 && my == -296){
				velY = 0;
			}
			else if (mx <= 112 && mx >= 16 && my == -552){
				velY = 0;
			}
			else if (mx <= 184 && mx >= 112 && my == -512){
				velY = 0;
			}
		}
		if(down){
			if(mx <= 332 && mx >= -200 && my == -76){
				velY = 0;
			}
			else if(mx <= -112 && mx >= -136 && my == -408){
				velY = 0;
			}
			else if(mx <= 260 && mx >= 52 && my == -328){
				velY = 0;
			}
			else if(mx <= 48 && mx >= 12 && my == -412){
				velY =0;
			}
			else if(mx <= 184 && mx >= 148 && my == -544){
				velY = 0;
			}
			
		}
		if(right){
			if(my <= -80 && my >= -596 && mx == 336){
				velX = 0;
			}
			else if(my <= -504 && my >= -596 && mx == -136){
				velX = 0;
			}
			else if(my <= -412 && my >= -500 && mx == -108){
				velX = 0;
			}
			else if (my <= -212 && my >= -408 && mx == -136){
				velX = 0;
			}
			else if (my <= -256 && my >= -292 && mx == 112){
				velX = 0;
			}
			else if (my <= -512 && my >= -552 && mx == 112){
				velX = 0;
			}
		}
		if(left){
			if(my <= -80 && my >= -596 && mx == -204){
				velX = 0;
			}
			else if(my <= -212 && my >= -252 && mx == -120){
				velX = 0;
			}
			else if(my <= -256 && my >= -292 && mx == 56){
				velX = 0;
			}
			else if(my <= -256 && my >= -292 && mx == 220){
				velX = 0;
			}
			else if(my <= -296 && my >= -328 && mx == 260){
				velX = 0;
			}
			else if(my <= -328 && my >= -412 && mx == 48){
				velX = 0;
			}
			else if(my <= -412 && my >= -548 && mx == 16){
				velX = 0;
			}
			else if(my <= -512 && my >= -544 && mx == 184){
				velX = 0;
			}
			else if (my <= -544 && my >= -596 && mx == 148){
				velX = 0;
			}
		}//END OF BUILDING
		
		
		//ABANDONED HOUSE TO THE LEFT OF THE POND
		if(up){
			if(mx <= -776 &&  mx >= -844 && my == -820){
				velY = 0;
			}
			else if(mx <= -844 && mx >= -1108 && my == -780){
				velY = 0;
			}
			else if(mx <= -1112 && mx >= -1152 && my == -828){
				velY = 0;
			}
			else if(mx <= -1152 && mx >= -1224 && my == -968){
				velY = 0;
			}
		}
		if(down){
			if(mx <= -780 && mx >= -1224 && my == -588){
				velY = 0;
			}
		}
		if(right){
			if(my <= -592 && my >= -816 && mx == -776){
				velX = 0;
			}
			else if(my <= -780 && my >= -824 && mx == -1108){
				velX = 0;
			}
			else if(my <= -828 && my >= -968 && mx == -1152){
				velX = 0;
			}
		}
		if(left){
			if(my <= -592 && my >= -968 && mx == -1224){
				velX = 0;
			}
			else if(my <= -780 && my >= -816 && mx == -848){
				velX = 0;
			}
		}//END OF BUILDING
		
		
		//POND,RIVER,BRIDGE,WELL,HAYPILE
		if(up){
			if(mx <= -1456 && mx >= -1752 && my == -1492){
				velY = 0;
			}
			else if(mx <= -1820 && mx >= -2036 && my == -1060){
				velY = 0;
			}
			else if(mx <= -1768 && mx >= -1820 && my == -984){
				velY = 0;
			}
		}
		if(down){
			if(mx <= -1456 && mx >= -1752 && my == -908){
				velY = 0;
			}
			else if(mx <= -1456 && mx >= -1752 && my == -1560){
				velY = 0;
			}
			else if(mx <= -1768 && mx >= -2036 && my == -916){
				velY = 0;
			}
		}
		if(right){
			if(my <= -912 && my >= -1492 && mx == -1452){
				velX = 0;
			}
			else if(my <= -1560 && mx == -1452){
				velX = 0;
			}
			else if(my <= -916 & my >= -984 && mx == -1764){
				velX = 0;
			}
			else if(my <= -984 && my >= -1060 && mx == -1820){
				velX = 0;
			}
		}
		if(left){
			if(my <= -912 && my >= -1492 && mx == -1756){
				velX = 0;
			}
			else if(my <= -1560 && mx == -1756){
				velX = 0;
			}
			else if(my <= -916 && my >= -1060 && mx == -2036){
				velX = 0;
			}
		}//END OF BUILDING
		
		
		//THE BATTLEFIELD
		if(up){
			if(mx <= -2600 &&  mx >= -2680 && my == -1876){
				velY = 0;
			}
			else if(mx <= -2600 &&  mx >= -2680 && my == -2460){
				velY = 0;
			}
			else if(mx <= -4020 && mx >= -4108 && my == -1876){
				velY = 0;
			}
			else if(mx <= -4020 && mx >= -4108 && my == -2468){
				velY = 0;
			}
		}
		if(down){
			if(mx <= -2600 &&  mx >= -2680 && my == -1384){
				velY = 0;
			}
			else if(mx <= -2600 &&  mx >= -2680 && my == -1984){
				velY = 0;
			}
			else if(mx <= -4020 && mx >= -4108 && my == -1388){
				velY = 0;
			}
			else if(mx <= -4020 && mx >= -4108 && my == -1992){
				velY = 0;
			}
		}
		if(right){
			if(my <= -1388  && my >= -1872 && mx == -2596){
				velX = 0;
			}
			else if(my <= -1988 && my >= -2460 && mx == -2596){
				velX = 0;
			}
			else if(my <= -1392 && my >= -1872 && mx == -4016){
				velX = 0;
			}
			else if(my <= -1992 && my >= -2468 && mx == -4016){
				velX = 0;
			}
		}
		if(left){
			if(my <= -1388 && my >= -1872 && mx == -2684){
				velX = 0;
			}
			else if(my <= -1988 && my >= -2460 && mx == -2684){
				velX = 0;
			}
			else if(my <= -1392 && my >= -1872 && mx == -4112){
				velX = 0;
			}
			else if(my <= -1992 && my >= -2468 && mx == -4112){
				velX = 0;
			}
		}//END OF BATTLEFIELD
		
		
		//THE INN
		if(up){
			if(mx <= -3124 && mx >= -3192 && my == -1156){
				velY = 0;
			}
			else if(mx <= -2132 && mx >= -2428 && my == -1156){
				velY = 0;
			}
			else if(mx <= -2432 && mx >= -2720 && my == -1008){
				velY = 0;
			}
			else if(mx <= -2544 && mx >= -2804 && my == -1156){
				velY = 0;
			}
			else if(mx <= -2808 && mx >= -2900 && my == -1108){
				velY = 0;
			}
			else if(mx <= -2808 && mx >= -2852 && my == -912){
				velY = 0;
			}
			else if(mx <= -2856 && mx >= -2876 && my == -868){
				velY = 0;
			}
			else if(mx <= -2880 && mx >= -3120 && my == -912){
				velY = 0;
			}
			else if(mx <= -2988 && mx >= -3096 && my == -1100){
				velY = 0;
			}
			else if(mx <= -2932 && mx >= -2984 && my == -1060){
				velY = 0;
			}
		}
		if(down){
			if(mx <= -2132 && mx >= -3192 && my == -712){
				velY = 0;
			}
			else if(mx <= -2544 && mx >= -2720 && my == -1048){
				velY  = 0;
			}
			else if(mx <= -2856 && mx >= -2900 && my == -1048){
				velY = 0;
			}
			else if(mx <= -2808 && mx >= -2852 && my ==  -996){
				velY = 0;
			}
			else if(mx <= -2932 && mx >= -3096 && my == -956){
				velY = 0;
			}
		}
		if(left){
			if(my <= -716 && my >= -1152 && mx == -3196){
				velX = 0;
			}
			else if(my <= -1008 && my >= -1152 && mx == -2432){
				velX = 0;
			}
			else if(my <= -1108 && my >= -1152 && mx == -2808){
				velX = 0;
			}
			else if(my <= -1052 && my >= -1104 && mx == -2904){
				velX = 0;
			}
			else if(my <= -1000 && my >= -1048 && mx ==  -2856){
				velX = 0;
			}
			else if(my <= -912 && my >= -996 && mx == -2808){
				velX = 0;
			}
			else if(my <= -868 && my >= -908 && mx == -2856){
				velX = 0;
			}
			else if(my <= -960 && my >= -1096 && mx == -3100){
				velX = 0;
			}
		}
		if(right){
			if(my <= -716 && my >= -1152 && mx == -2128){
				velX = 0;
			}
			else if(my <= -1008 && my >= -1048 && mx == -2720){
				velX = 0;
			}
			else if(my <= -1052 && my >= -1152 && mx == -2540){
				velX = 0;
			}
			else if(my <= -868 && my >= -908 && mx == -2876){
				velX = 0;
			}
			else if(my <= -912 && my >= -1152 && mx == -3120){
				velX = 0;
			}
			else if(my <= -960 && my >= -1056 && mx == -2928){
				velX = 0;
			}
			else if(my <= -1060 && my >= -1096 && mx == -2984){
				velX = 0;
			}
		}//END OF BUILDING
		
		
		//BLACKSMITH'S HOUSE
		if(up){
			if(mx <= -3544 && mx >= -3612 && my == -648){
				velY = 0;
			}
			else if(mx <= -4068 && mx >= -4140 && my == -648){
				velY = 0;
			}
			else if(mx <= -3616 && mx >= -4072 && my == -28){
				velY = 0;
			}
			else if(mx <= -3808 && mx >= -3876 && my == -472){
				velY = 0;
			}
			else if(mx <= -3660 && mx >= -3804 && my == -384){
				velY = 0;
			}
			else if(mx <= -3616 && mx >= -3656 && my == -428){
				velY = 0;
			}
			else if(mx <= -3900 && mx >= -4056 && my == -200){
				velY = 0;
			}
		}
		if(down){
			if(mx <= -3544 && mx >= -4140 && my == 160){
				velY = 0;
			}
			else if(mx <= -3616 && mx >= -3788 && my == -60){
				velY = 0;
			}
			else if(mx <= -3792 && mx >= -3876 && my == -240){
				velY = 0;
			}
			else if(mx <= -3900 && mx >= -4056 && my == -64){
				velY = 0;
			}
		}
		if(left){
			if(my <= 156 && my >= -644 && mx == -4144){
				velX = 0;
			}
			else if(my <= -28 && my >= -60 && mx == -3616){
				velX = 0;
			}
			else if(my <= -64 && my >= -240 && mx == -3792){
				velX = 0;
			}
			else if(my <= -244 && my >= -468 && mx == -3880){
				velX = 0;
			}
			else if(my <= -384 && my >= -424 && mx == -3660){
				velX = 0;
			}
			else if(my <= -428 && my >= -644 && mx == -3616){
				velX = 0;
			}
			else if(my <= -68 && my >= -196 && mx == -4060){
				velX = 0;
			}
		}
		if(right){
			if(my <= 156 && my >= -644 && mx == -3540){
				velX = 0;
			}
			else if(my <= -28 && my >= -644 && mx == -4072){
				velX = 0;
			}
			else if(my <= -384 && my >= -468 && mx == -3804){
				velX = 0;
			}
			else if(my <= -68 && my >= -196 && mx == -3896){
				velX = 0;
			}
		}//END OF BLACKSMITH'S HOUSE
		
		
		//THE CASTLE
		if(up){
			if(mx <= -2964 && mx >= -3060 && my == 4){
				velY = 0;
			}
			else if(mx <= -2836 && mx >= -2960 && my == -28){
				velY = 0;
			}
			else if(mx <= -2572 && mx >= -2832 && my == 200){
				velY = 0;
			}
			else if(mx <= -2372 && mx >= -2568&& my == -28){
				velY = 0;
			}
			else if(mx <= -772 && mx >= -872 && my == -28){
				velY = 0;
			}
			else if(mx <= -876 && mx >= -1004 && my == -64){
				velY = 0;
			}
			else if(mx <= -1008 && mx >= -1296 && my == 184){
				velY = 0;
			}
			else if(mx <= -1300 && mx >= -1512 && my == -64){
				velY = 0;
			}
			else if(mx <= -1516 && mx >= -1856 && my == -412){
				velY = 0;
			}
			else if(mx <= -1860 && mx >= -1908 && my == -472){
				velY = 0;
			}
			else if(mx <= -1912 && mx >= -1972 && my == 140){
				velY = 0;
			}
			else if(mx <= -1976 && mx >= -2028 && my == -472){
				velY = 0;
			}
			else if(mx <= -2032 && mx >= -2368 && my == -412){
				velY = 0;
			}
		}
		if(down){
			
		}
		if(left){
			if(my >= 8 && mx == -3064){
				velX = 0;
			}
			else if(my <= 4 && my >= -24 && mx == -2964){
				velX = 0;
			}
			else if(my <= 200 && my >= -24 && mx == -2572){
				velX = 0;
			}
			else if(my <= -28 && my >= -408 && mx == -2372){
				velX = 0;
			}
			else if(my <= 184 && my >= -60 && mx == -1008){
				velX = 0;
			}
			else if(my <= 140 && my >= -468 && mx == -1912){
				velX = 0;
			}
			else if(my <= -412 && my >= -468 && mx == -2032){
				velX = 0;
			}
		}
		if(right){
			if(my <= 200 && my >= -24 && mx == -2832){
				velX = 0;
			}
			else if(my >= -24 && mx == -768){
				velX = 0;
			}
			else if(my <= -28 && my >= -60 && mx == -872){
				velX = 0;
			}
			else if(my <= 184 && my >= -60 && mx == -1296){
				velX = 0;
			}
			else if(my <= -64 && my >= -408 && mx == -1512){
				velX = 0;
			}
			else if(my <= -412 && my >= -468 && mx == -1856){
				velX = 0;
			}
			else if(my <= 140 && my >= -468 && mx == -1972){
				velX = 0;
			}
		}//END OF CASTLE
		
	}//END OF COLLISION METHOD
	
	public void storyUpdate(){
		//THIS METHOD KEEPS TRACK OF THE STORY AT ALL TIMES
		if(mainMenuScreen == false){
			if(progress - checkpoint > 0){
				cutscene = true;
				checkpoint = progress;
			}
			if(progress < 3 && cutscene == false){
				progress++;
			}
			else if(progress == 3 && my <= -2420){
				//Battle with the prison guard
				inBattle = true;
				progress++;
			}
			else if(progress == 4 && inBattle == false){
				//Defeating the prison guard
				progress++;
			}
			else if(progress < 7){
				//Player now goes to the Inn
				if(mx <= -2432 && mx >= -2720 && my <= -1008 && my >= -1068 && cutscene == false){
					progress++;
				}
			}
			else if(progress == 7){
				//Player recruits blacksmith
				if(mx <= -3880 && mx >= -4072 && my <= -368 && my >= -508){
					progress++;
				}
			}
			else if(progress < 10){
				//Player saves/recruits the guard
				if(progress == 8 && mx <= 140 && mx >= -80 && my <= 160 && my >= 40){
					progress++;
					inBattle = true;
				}
				else if(progress == 9 && inBattle == false){
					progress++;
				}
			}
			else if(progress == 10){
				//Player recruits the Healer
				if(mx <= 240 && mx >= -136 && my <= -256 && my >= -328){
					progress++;
				}
			}
			else if(progress == 11){
				//Player reports back to Mysterious Man
				if(mx <= -2352 && mx >= -2624 && my <= -1876 && my >= -1984){
					progress++;
				}
			}
			else if(progress <= 14){
				//THE FINAL SCENE - Player confronts King and makes final decision
				if(mx <= -1912 && mx >= -1972 && my >= -12 && cutscene == false){
					progress++;
				}
			}
			
			else if(progress <= 17 && checkpoint==progress){
				//PLAYER SIDES WITH PRINCE ATOL
				if(mx <= -1912 && mx >= -1972 && my >= 60 && select){
					progress = 16;
					checkpoint = 16;
					inBattle = true;
				}
				//PLAYER SIDES WITH KING GALLO
				else if (mx <= -1912 && mx >= -1972 && my >= -60 && select){
					progress = 17;
					checkpoint = 17;
					inBattle = true;
				}
				if(progress <= 17 && inBattle==false && checkpoint >=16){
					cutscene=true;	
				}
			}
			else if(progress == 18){
				//THE END
				if(cutscene == false){
					mainMenuScreen = true;
					creditsScreen = true;
				}
			}
		}
	}

	public void randomEncounter(){
		//WE INITIALLY WANTED TO HAVE RANDOM ENCOUNTERS IN OUR GAME BUT...
		//...EVEN THOUGH IT WORKED, WE DISCOVERED THAT IT MADE THE GAME...
		//...VERY BUGGY. SO IN THE END WE DECIDED IT WAS BETTER TO HAVE...
		//...A SMOOTHER EXPERIENCE (RELATIVELY) THAN AN EXTRA FEATURE
		
		//HOWEVER WE LEFT THE CODE HERE BECAUSE WE WANTED YOU TO SEE THAT...
		//...WE TRIED AT LEAST :'(
		int chance = 0 + (int)(Math.random() * ((800 - 0) + 1));
		if(up||down||left||right){
			if(chance == 1){
				randomEnemy = true;
				ehp = 100;
				inBattle = true;
			}
		}
	}
	
	public void reset(){
		//This method is simply used to reset some key variables back to their default settings
		mx = 380;
		my = -2092;
		progress = 0;
		checkpoint = -1;
		if(dead == true && cutscene == false){
			cutscene = false;
			dead = false;
		}
	}
	
	public void chapterSelection(int chapter){
		//THIS METHOD IS USED TO IMMEDIETLY TELEPORT THE PLAYER TO ANY POINT IN...
		//...THE GAME VIA THE "CONTINUE" OPTION IN THE MAIN MENU
		if(chapter > 0){
			levelSelect = false;
			menuY = 35;
		}
		switch(chapter){
		case 1:
			//MEETING THE REBEL LEADER
			mx = -2488;
			my = -1216;
			progress = 5;
			checkpoint = 5;
			mainMenuScreen = false;
			break;
		case 2:
			//RECRUITING THE BLACKSMITH
			mx = -3732;
			my = -644;
			progress = 7;
			checkpoint = 7;
			mainMenuScreen = false;
			break;
		case 3:
			//RESCUING THE GUARD
			mx = 468;
			my = 236;
			progress = 8;
			checkpoint = 8;
			mainMenuScreen = false;
			break;
		case 4: 
			//RECRUITING THE HEALER
			mx = 36;
			my = -636;
			progress = 10;
			checkpoint = 10;
			mainMenuScreen = false;
			break;
		case 5:
			//CONFRONTING THE KING
			mx = -1944;
			my = -520;
			progress = 12;
			checkpoint = 11;
			mainMenuScreen = false;
			break;
		default:
            mx = mx;
            my= my;
            progress = progress;
            checkpoint = checkpoint;
            mainMenuScreen = false;
			break;
		}
	}
	
	public void soundCheck(){
		//This method just makes sure the correct music is playing at any given time
		if(mainMenuScreen == true && menuMusic == false){
			mainTheme.loop();
			overworld.stop();
			battle.stop();
			menuMusic = true;
			battleMusic = false;
			worldMusic = false;
		}
		else if(inBattle == true && battleMusic == false){
			battle.loop();
			mainTheme.stop();
			overworld.stop();
			battleMusic = true;
			menuMusic = false;
			worldMusic = false;
		}
		else if(mainMenuScreen == false && inBattle == false && worldMusic == false){
			overworld.loop();
			battle.stop();
			mainTheme.stop();
			worldMusic = true;
			battleMusic = false;
			menuMusic = false;
		}
	}
	
	public void keyPressed(KeyEvent e) {
		char c = e.getKeyChar();
		if(mainMenuScreen == false && inBattle == false && cutscene == false){
			//These inputs are for regular game play
			if (c=='w'){
				up = true;
				velY = 4;
			}
			else if (c=='s'){
				down = true;
				velY = -4;
			}
			else if (c=='a'){
				left = true;
				velX = 4;
			}
			else if (c=='d'){
				right = true;
				velX = -4;
			}
			else if (c==' '){
				select = true;
			}
		}
		
		
		
		else if(cutscene == true){
			//These inputs are used during cutscene's
			if (c==' '){
				if(cutscene){
					cutscene = false;
				}
				if(progress == 16 || progress == 17){
					progress = 18;
					checkpoint = 18;
					cutscene = true;
				}
			}
		}
		
		else if(levelSelect){
			//The rest of the inputs from here on down are used for all the different...
			//...sub-menus in the main menu
			if(c == 's'){
				if(menuY == 70){
					menuY += 195;
				}
				else if(menuY < (70+195+65+65+65+65)){
					menuY += 65;
				}
			}
			else if(c == 'w'){
				if(menuY == (70+195)){
					menuY -= 195;
				}
				else if(menuY > (70+195)){
					menuY -= 65;
				}
			}
			else if(c == ' '){
				int chapter = 0;
				if(menuY == 70){
					levelSelect = false;
					menuY = 135;
				}
				else if(menuY == 70+195){
					chapter = 1;
					chapterSelection(chapter);
				}
				else if(menuY == 70+195+65){
					chapter = 2;
					chapterSelection(chapter);
				}
				else if(menuY == 70+195+65+65){
					chapter = 3;
					chapterSelection(chapter);
				}
				else if(menuY == 70+195+65+65+65){
					chapter = 4;
					chapterSelection(chapter);
				}
				else if(menuY == 70+195+65+65+65+65){
					chapter = 5;
					chapterSelection(chapter);
				}
			}
		}
		
		
		else {
			
			if(levelSelect == false && options == false && creditsScreen == false){
				if (c=='s'){
					if(menuY<(35+100+100+100)){
						menuY+=100;
					}
				}
				else if(c=='w'){
					if(menuY>35){
						menuY-=100;
					}
				}
			}
			if(menuY == 35 && c==' '){
				mainMenuScreen = false;
			}
			
			if(options == true && c==' '){
					options = false;
				}
			else if(creditsScreen == true && c == ' '){
				creditsScreen = false;
			}
			else if (c==' '){
				if(menuY == (35+100)){
					levelSelect = true;
					menuY = 70;
				}
				else if(menuY == (35+100+100)){
					options = true;
				}
				else if(menuY == (35+100+100+100)){
					creditsScreen = true;
				}
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		char c = e.getKeyChar();
		if(mainMenuScreen == false) {
			if (c=='w'){
				up = false;
				velY = 0;
			}
			else if (c=='s'){
				down = false;
				velY = 0;
			}
			else if (c=='a'){
				left= false;
				velX = 0;
			}
			else if (c=='d'){
				right = false;
				velX = 0;
			}
			else if (c==' '){
				select = false;
			}
		}
	}

	public void keyTyped(KeyEvent e) {
		//The key typed inputs are used entirely for the battle screen
		char c = e.getKeyChar();
		boolean fight = false;
		boolean potion = false;
		boolean defend = false;
		if(inBattle == true){
			if(c =='w'){
				if(by >= 530+72){
					by -= 72;
				}
			}
			else if (c=='s'){
				if(by <= 530+72){
					by += 72;
				}
			}
			else if(c==' '){
				if(by == 530){
					fight = true;
					potion = false;
					defend = false;
					
					System.out.println("fight");
				}
				else if(by == 602){
					fight = false;
					potion = true;
					defend = false;
					System.out.println("potion");
				}
				else if(by == 674){
					fight = false;
					potion = false;
					defend = true;
					System.out.println("defend");
				}
			}
		}
		battle(fight,potion,defend);
		//^^^The option that the player selected is now sent to the battle...
		//...method to execute
		repaint();
	}
	
	public void battle (boolean fight, boolean potion, boolean defend){
		//This method handles the logic behind the battle screens (example: Player...
		//...Health, Player Stamina, Enemy Health, Damage)
		if(inBattle == true){
			if(fight){
				if(stamina > 0){
					ehp -= damage*stamina;
					stamina -= 1;
					hp -= damage*2;
				}
			}
			else if(potion){
				if(hp <= 80){
					hp += 20;
					ehp += 10;
				}
			}
			else if(defend){
				if(stamina < 9){
					hp -= damage;
					stamina += 1;
				}
			}
			
			if(ehp <= 0){
				System.out.println("Victory");
				inBattle = false;
			}
			else if(hp <= 0){
				System.out.println("Defeat");
				dead = true;
				inBattle = false;
				cutscene = true;
			}
		}
		else {
			hp = 100;
			stamina = 5;
			damage = 5;
			randomEnemy = false;
			if(progress == 8){
				ehp = 200;
				//^^^This enemy (which encountered when the player progress...
				//...has reaches stage 8) has higher health
			}
			else if(progress == 15){
				ehp = 300;
				//^^^This final boss (which encountered when the player progress...
				//...has reaches stage 15) has higher health
			}
			else {
				ehp = 100;
				//^^^Regular enemies have standard heath
			}
		}
	}
	
	public void start(){
		t = new Thread(this);
		t.start();
		running = true;
		System.out.println("STARTED");
	}
	
	public void run() {
		//MOST OF THE CODE IN THIS METHOD IS USED TO...
		//...CAP THE GAME AT 60 UPDATES PER SECOND IN ORDER...
		//...TO ENSURE A CONSISTENT EXPERIENCE OVER A RANGE...
		//..OF DIFFERENT SYSTEMS
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		double ns = 1000000000.0 / 60.0;
		double delta = 0;
		int frames = 0;
		int updates = 0;
		while (running)
        {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >=  1){
				//FROM HERE ON DOWN IS THE GAME CODE
				soundCheck();
				if(mainMenuScreen == false){
					storyUpdate();
					collision();
		        	mx += velX;
		        	my += velY;
		        	//randomEncounter();
		        	//^^^RANDOM ENCOUNTER CAUSED OUR GAME TO BE TOO BUGGY BUT FEEL...
		        	//...FREE TO UNCOMMENT IT AND TRY IT OUT
		        	if(dead){
		        		mainMenuScreen = true;
		        		options = false;
		        		creditsScreen = true;
		        	}
				}
				else {
					reset();
				}
				//^^^THIS IS WHERE THE GAME CODE ENDS
	        	repaint();
	        	updates++;
	        	delta--;
	        	showStatus(null);
			}
			frames++;
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				System.out.println(updates + " ups | " + frames + " fps");
				updates = 0;
				frames = 0;
			}
        }
	}
		
	public void stop(){
		running = false;
	}	

}
