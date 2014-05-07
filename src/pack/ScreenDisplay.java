package pack;

import org.lwjgl.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;

public class ScreenDisplay {
	
	final double screenWidth = Display.getDesktopDisplayMode().getWidth();
	final double screenHeight = Display.getDesktopDisplayMode().getHeight();
	final int slices = 10;
	//Create objects
	CalcPrimes calc = new CalcPrimes();
	Thread t1 = new Thread(calc);

	// Create vars DON'T YET DEFINE VALUE
	int frameNumber;
	int FPS;
	double xZoom;
	double yZoom;
	
	int interval1;
	int interval2;
	int interval3;
	
	long time;

	final float[][] colorArray = new float[5][3];
	// [0]=red
	// [1]=green
	// [2]=blue
	// [3]=white
	// [4]=black
	
	ScreenDisplay(){ // CONSTRUCTOR
		t1.start();
		initGL((int)screenWidth, (int)screenHeight);
		load();
		startDisplayLoop();
	}


	private void initGL(int width, int height){
	
		try {
			//DisplayMode mode = new DisplayMode(width,height);
			//DisplayMode mode = Display.getDesktopDisplayMode(); Old code, for not-fullscreen stuff
			Display.setDisplayMode(Display.getDesktopDisplayMode());
			Display.setFullscreen(true);
			Display.create();
			Display.setVSyncEnabled(true);
		} catch (LWJGLException e) {
			System.out.println("Catched an error in intiGL");
			e.printStackTrace();
			System.exit(0);	
		}
 
		// init OpenGL
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, 0, height, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }
	
	private void load(){
		frameNumber = 61;
		FPS = 0;
		xZoom = 5;
		yZoom = 3;
		interval1 = 1000;
		interval2 = 10000;
		interval3 = 100;
		
		colorArray[0][0] = 1;
		colorArray[0][1] = 0;
		colorArray[0][2] = 0;
		
		colorArray[1][0] = 0.2f;
		colorArray[1][1] = 1;
		colorArray[1][2] = 0.2f;
		
		colorArray[2][0] = 0.3f;
		colorArray[2][1] = 0.3f;
		colorArray[2][2] = 1;
		
		colorArray[3][0] = 1;
		colorArray[3][1] = 1;
		colorArray[3][2] = 1;
		
		colorArray[4][0] = 0;
		colorArray[4][1] = 0;
		colorArray[4][2] = 0;
		
		/*loadTexture("tmpNaam", "TX_test.png");*/
	}
	
	private void startDisplayLoop(){
		while(!Display.isCloseRequested()){
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			checkInput();
						
			drawStuff();
			
			endThisLoop();
		}
		endProgram();
		Display.destroy();
	}
	
	private void endProgram() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		System.exit(0);
	}


	private void checkInput(){
		if(Mouse.isButtonDown(0)){
			leftHold(Mouse.getX(), Mouse.getY());
		}
		while(Mouse.next()){
			if(Mouse.getEventButtonState() && Mouse.getEventButton() == 0){
				leftClick(Mouse.getX(), Mouse.getY());
			}
			
			if(Mouse.getEventButtonState() && Mouse.getEventButton() == 1){
				rightClick();
			}			
		}
		while(Keyboard.next()){
			if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE){
				System.exit(0);
			}
			
			if(Keyboard.getEventKey() == Keyboard.KEY_UP){
				yZoom+=0.3;
			}
			if(Keyboard.getEventKey() == Keyboard.KEY_DOWN){
				yZoom-=0.3;
			}	
			if(Keyboard.getEventKey() == Keyboard.KEY_LEFT){
				xZoom-=0.3;
			}	
			if(Keyboard.getEventKey() == Keyboard.KEY_RIGHT){
				xZoom+=0.3;
			}
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_Q)){
			interval1*=1.1;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			interval1*=0.9;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			interval2*=1.1;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			interval2*=0.9;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_E)){
			interval3*=1.1;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			interval3*=0.9;
		}
	}
	private void leftHold(int x, int y){

	}
	
	private void leftClick(int x, int y) {

	}
	
	private void rightClick(){

	}

	private void drawStuff() {
		if(!Keyboard.isKeyDown(Keyboard.KEY_C)){
			drawGraph(calc.getHist(interval3), interval3, colorArray[0], 0.05f);
		}
		if(!Keyboard.isKeyDown(Keyboard.KEY_X)){
			drawGraph(calc.getHist(interval2), interval2, colorArray[1], 0.5f);
		}
		if(!Keyboard.isKeyDown(Keyboard.KEY_Z)){
			drawGraph(calc.getHist(interval1), interval1, colorArray[3], 1);
		}
		
		float perc = (float) ((calc.totalPrimes*100)/calc.numbersProcessed);
		GL11.glColor4f(1, 1, 1, 1);
		SimpleText.drawString("PRIMES FOUND: "+calc.totalPrimes+" IN TOTAL OF "+(int)calc.numbersProcessed+" NUMBERS.  THAT IS "+perc+" PROCENT      FPS "+FPS, 4, 4);
		SimpleText.drawString("CONTROLES ARE ARROW KEYS. QAZ. WSX. EDC.", 3, (int)screenHeight - 20);
		SimpleText.drawString("PRIMES PER SECOND "+calc.primesPerSecond, (int)(screenWidth/2), 4);
	}
	
	private void drawGraph(int[] c, double interval, float[] f, float alpha){
		for(int i = 0; i < c.length; i++){
			if(i+1 < c.length){
				if(c[i+1] != 0){
					int x1 = (int)(i*interval/screenWidth*(xZoom/2))+1;
					int y1 = (int)((c[i]*yZoom*500)/interval);
					int x2 = (int)((i+1)*interval/screenWidth*(xZoom/2))+1;
					int y2 = (int)((c[i+1]*yZoom*500)/interval);
	
					drawLine(x1, y1, x2, y2, f, alpha);
					drawCircle(x1, y1, 2, f, alpha/2);
					drawCircle(x1, y1, 5, f, alpha/10);
					
					if(i+3 < c.length){
						if(c[i+3] == 0 && c[i+2] != 0 && Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
							drawLine(0, y2, (int)screenWidth, y2, f, 0.5f);
						}
					}
				}else{
					break;
				}
			}	
		}
	}
	
	private void endThisLoop(){
		//FPS meter
		if (time <= System.currentTimeMillis() - 1000) {			
			Display.setTitle("Calculating priems @ " + frameNumber + " FPS"); //Normal
			FPS = frameNumber;
			frameNumber = 0;
			time = System.currentTimeMillis();
		} else {
			frameNumber++;
		}
		Display.update();
		Display.sync(60);
	}
	
	private void drawLine(int X1, int Y1, int X2, int Y2, float color[], float alpha){
		GL11.glColor4f(color[0], color[1], color[2], alpha);
		GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex2f(X1, Y1);
			GL11.glVertex2f(X2, Y2);
		GL11.glEnd();
		
		GL11.glColor4f(color[0], color[1], color[2], alpha/2);
		GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex2f(X1, Y1+1);
			GL11.glVertex2f(X2, Y2+1);
		GL11.glEnd();
	
		GL11.glColor4f(color[0], color[1], color[2], alpha/2);
		GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex2f(X1, Y1-1);
			GL11.glVertex2f(X2, Y2-1);
		GL11.glEnd();
	}
	
	private void drawCircle(double x, double y, int radius, float color[], float alpha){		
		GL11.glColor4f(color[0], color[1], color[2], alpha);
	
		float incr = (float) (2 * Math.PI / slices);
		/*xCoord = xCoord + radius;
		yCoord = yCoord + radius;*/
	
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		for(int i = 0; i < slices; i++){
			float angle = incr * i;

			float Xc = (float) (x +  Math.cos(angle) * radius);
			float Yc = (float) (y +  Math.sin(angle) * radius);

          GL11.glVertex2f(Xc, Yc);
		}
		GL11.glEnd();	
	}
}