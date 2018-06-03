import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import com.hamoid.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class voidmix extends PApplet {



VideoExport videoExport;

ParseJSON parseJSON;

MoonPhases moonPhases;
RainDrops rainDrops;
SunRise sunRise;
WindMap windMap;
PerlinCloud perlinCloud;
TideLines tideLines;

int dayCount = 0;
int daySpeed = 60*6;

boolean exportVideo = true;

public void setup() {
  
  
  // size(2560, 1440);
  
  parseJSON = new ParseJSON(); 
  
  moonPhases = new MoonPhases(width, height);
  rainDrops = new RainDrops(width, height);
  sunRise = new SunRise(width, height);
  windMap = new WindMap(width, height);
  perlinCloud = new PerlinCloud(width, height);
  tideLines = new TideLines(width, height);
  
  parseJSON.parse("result.json");

  if(exportVideo) {
    videoExport = new VideoExport(this, "export/options-"+hour()+""+minute()+""+second()+".mp4");
    videoExport.setQuality(70, 128);
    videoExport.setFrameRate(60);
    videoExport.setLoadPixels(true);
    videoExport.setDebugging(false);
    videoExport.startMovie();
  }

}

public void draw() {
 
  background(200);
  
  if(frameCount%(daySpeed) == 0) {
    dayCount = dayCount%365+1;
  }

  TimeFrame timeFrameSelected = parseJSON.timeFrames.get(dayCount%365);
  
  image(rainDrops.draw(timeFrameSelected.precipitationN), 0.0f, 0.0f);
  blend(perlinCloud.draw(timeFrameSelected.cloudCoverN), 0, 0, width, height, 0, 0, width, height, SCREEN);
  blend(moonPhases.draw(timeFrameSelected.moonAge), 0, 0, width, height, 0, 0, width, height, SCREEN);
  blend(windMap.draw(timeFrameSelected.windDirection, timeFrameSelected.windSpeedN), 0, 0, width, height, 0, 0, width, height, SCREEN);
  blend(tideLines.draw(timeFrameSelected.tideMinN, timeFrameSelected.tideMaxN), 0, 0, width, height, 0, 0, width, height, SCREEN);  
  blend(sunRise.draw(timeFrameSelected.cloudCoverN), 0, 0, width, height, 0, 0, width, height, SCREEN);

  
  
  // image(tideLines.draw(timeFrameSelected.tideMinN, timeFrameSelected.tideMaxN), 0, 0);
  // image(windMap.draw(timeFrameSelected.windDirection, timeFrameSelected.windSpeedN), 0.0, 0.0);
    
  // tint(255, 255, 255, 100);
  // image(moonPhases.draw(timeFrameSelected.moonAge), 0.0, 0.0);
  // image(sunRise.draw(), 0, 0);
  
 dayNightFade();
  
 fill(255);
 text("day " + dayCount + " ,date " + timeFrameSelected.date + ", rain " + timeFrameSelected.precipitationN, 50, 50);
 text(frameCount, 50, 70);
 text("dayspeed: " + daySpeed, 50, 90);
 text("moonAge: " + timeFrameSelected.moonAge, 50, 110);
 text("moonVisible: " + timeFrameSelected.moonVisible+"%", 50, 130);
 text("moonPhase: " + timeFrameSelected.moonPhase, 50, 150);

 text("windDirection: " + timeFrameSelected.windDirection, 50, 170);
 text("windSpeed: " + timeFrameSelected.windSpeed, 50, 190);
 text("windSpeedN: " + timeFrameSelected.windSpeedN, 50, 210);

 text("tideMinN: " + timeFrameSelected.tideMinN, 50, 230);
 text("tideMaxN: " + timeFrameSelected.tideMaxN, 50, 250);
 
 text("cloudCoverN: " + timeFrameSelected.cloudCoverN, 50, 270);
 
  
  if(exportVideo) {
    videoExport.saveFrame();
  }

}

public void dayNightFade() {
 
 int fadeTime = (int) (daySpeed/3.2f); // 7.2
  
 float BGColor = map(frameCount%daySpeed, 0, fadeTime, 255, 0);
 noStroke();
 fill(0, BGColor);
 rect(0, 0, width, height);
 BGColor = map(frameCount%daySpeed, (daySpeed)-fadeTime, (daySpeed), 0, 255);
 fill(0, BGColor);
 rect(0, 0, width, height); 
  
}

public void keyPressed() {
  if (key == 'q') {
    if(exportVideo) {
      videoExport.endMovie();
      exit();
    }
  }
}

class MoonPhases {
     
  int f = color(255);
  int bg = color(0,0,0);
  PShader blur;
  float theta;
  float t;
  float radius = 200;
  int frames = 600;
  PGraphics moonTexture; 
  PGraphics moonPhases;
  PGraphics moon;
  
  PGraphics pg;
  
  MoonPhases(int width, int height) {
      //pg = createGraphics(1080, 720);
      pg = createGraphics(width, height);

      moonTexture = createGraphics(width, height);
      moonPhases = createGraphics(width, height);
      moon = createGraphics(width, height);

      moonTexture.beginDraw();
      moonTexture.background(0);
      for(int i = 0; i< 24000; i++){
        float t = random(TWO_PI);
        
        moonTexture.strokeWeight(0.1f);
        moonTexture.stroke(255, 255, 255, 10);

        float x1 = width/2 + radius*0.5f * cos(t);
        float y1 = height/2 + radius*0.5f * sin(t);

        t = random(TWO_PI);

        float x2 = width/2 + radius*0.5f * cos(t);
        float y2 = height/2 + radius*0.5f * sin(t);
        moonTexture.line(x1, y1, x2, y2);
      }
    
      moonTexture.endDraw();
      //moonPhasesDraw(moonPhases);

  }

  public void moonPhasesDraw(PGraphics p, float moonAge) {
    p.beginDraw();
 
    float mapMoonData = map(moonAge, 0.0f, 29.53059f, 600.0f, 0.0f);

    t = ((mapMoonData+300)%frames)/(float)frames;
    p.background(bg);
    
    p.translate(width/2, height/2);

    float moonRotate = map(moonAge, 0.0f, 29.53059f, PI*0.0f, -PI*2.0f);

    p.rotate(moonRotate);
    p.translate(200, 0.0f);

    

    p.noStroke();
    p.rotate(PI/2);
    if (t < 0.5f) {
      float tt = map(t, 0, 0.5f, 0, 1); // normalizing the time of the first half of the cycle
      if (tt < .5f) {
        p.fill(f);
        float r = map(tt, 0, 0.5f, radius, 0);
        p.arc(0, 0, radius, r, 0, PI); // moon lower, shrinking
        p.arc(0, 0, radius, radius, PI, TWO_PI); // moon upper, stable
      } else {
        p.fill(f);
        p.arc(0, 0, radius, radius, PI, TWO_PI); // moon upper, stable
        float r = map(tt, 0.5f, 1.0f, 0, radius);
        p.fill(bg);
        p.arc(0, 0, radius, r, PI, TWO_PI); // bg upper, increasing
      }
    } else {
      float tt = map(t, 0.5f, 1, 0, 1); // normalizing the time of the second half of the cycle
      if (tt <.5f) {
        p.fill(bg);
        p.arc(0, 0, radius, radius, PI, TWO_PI); // bg upper, stable
        float r = map(tt, 0, 0.5f, radius, 0);
        p.fill(f);
        p.arc(0, 0, radius, radius, 0, PI); // moon lower, stable > partly hidden by the following arc
        p.fill(bg);
        p.arc(0, 0, radius, r, 0, PI); // bg lower, decreasing
      } else {
        p.fill(f);
        float r = map(tt, 0.5f, 1.0f, 0, radius);
        p.arc(0, 0, radius, radius, 0, PI); // moon lower, stable
        p.arc(0, 0, radius, r, PI, TWO_PI); // moon upper, increasing
      }
    }
    theta+=TWO_PI/frames;
    
    //p.filter(blur);
    //p.filter(BLUR, 8);

    p.endDraw();
}

public PGraphics rotateMoon(float moonAge) {
  moon.beginDraw();
  moon.pushMatrix();
  moon.translate(width/2,height/2);
  
  float moonRotate = map(moonAge, 0.0f, 29.53059f, PI*0.0f, -PI*2.0f);

  moon.rotate(moonRotate);
  moon.translate(200, 0.0f);

  moon.rotate( (float)(frameCount*0.02f*Math.sin(frameCount*0.005f)*0.01f) );
  // moon.fill(255, 0, 0);
  
  // moon.rect(0, 0, width,height);
  moon.image(moonTexture, -width/2, -height/2);
  moon.popMatrix();
  moon.endDraw();
  return moon;
}
  
  public PGraphics draw(float moonAge) {

    
    pg.beginDraw();
    
    moonPhasesDraw(moonPhases, moonAge);
    pg.image(moonPhases, 0, 0);
    pg.blend(rotateMoon(moonAge), 0, 0, width, height, 0, 0, width, height, DARKEST);
    pg.endDraw();
    
    
    return pg;
  }
  
}

class ParseJSON {
 
  ArrayList<TimeFrame> timeFrames;
  JSONArray values;
  
  public void parse(String file) {
    
    timeFrames = new ArrayList<TimeFrame>();
    
    values = loadJSONArray(file);
    
    for (int i = 0; i < values.size(); i++) {
        JSONObject timeFrame = values.getJSONObject(i);
        
        String date = timeFrame.getString("date");
        
        // PRECIPITATION
        JSONObject precipitationJO = timeFrame.getJSONObject("precipitation");
        float precipitation = precipitationJO.getFloat("value");
        float precipitationN = precipitationJO.getFloat("normalized");

        // MOON
        JSONObject moonJO = timeFrame.getJSONObject("moon");
        float moonAge = moonJO.getFloat("moonAge");
        int moonVisible = moonJO.getInt("visible");
        String moonPhase = moonJO.getString("phase");

        // WIND
        int windDirection = timeFrame.getInt("windDirection");
        JSONObject windJO = timeFrame.getJSONObject("windSpeed");
        int windSpeedValue = windJO.getInt("value");
        float windSpeedValueN = windJO.getFloat("normalized");

        // TIDE
        JSONObject tideJO = timeFrame.getJSONObject("tide");
        
        JSONObject tideJOMIN = tideJO.getJSONObject("min");
        JSONObject tideJOMINM = tideJOMIN.getJSONObject("measurement");
        int tideMin = tideJOMINM.getInt("value");
        float tideMinN = tideJOMINM.getFloat("normalized");
        
        JSONObject tideJOMAX = tideJO.getJSONObject("max");
        JSONObject tideJOMAXM = tideJOMAX.getJSONObject("measurement");
        int tideMax = tideJOMAXM.getInt("value");
        float tideMaxN = tideJOMAXM.getFloat("normalized");

        JSONObject cloudCoverJO = timeFrame.getJSONObject("cloudCover");
        float cloudCoverN = cloudCoverJO.getFloat("normalized");
        

        timeFrames.add(new TimeFrame(date, precipitation, precipitationN, moonAge, moonVisible, moonPhase, windDirection, windSpeedValue, windSpeedValueN, tideMin, tideMinN, tideMax, tideMaxN, cloudCoverN));
        
        println(timeFrames.size());
    }
  }
}

class TimeFrame {
  
   String date;
  
   float precipitation;
   float precipitationN;

   float moonAge;
   int moonVisible;
   String moonPhase;

   int windDirection;
   int windSpeed;
   float windSpeedN;

   int tideMin;
   float tideMinN;
   int tideMax;
   float tideMaxN;

   float cloudCoverN;
   
   TimeFrame(String date, float precipitation, float precipitationN, float moonAge, int moonVisible, String moonPhase, int windDirection, int windSpeed, float windSpeedN, int tideMin, float tideMinN, int tideMax, float tideMaxN, float cloudCoverN) {
     this.date = date;
     this.precipitation = precipitation;
     this.precipitationN = precipitationN;
     this.moonAge = moonAge;
     this.moonVisible = moonVisible;
     this.moonPhase = moonPhase;
     this.windDirection = windDirection;
     this.windSpeed = windSpeed;
     this.windSpeedN =windSpeedN;
     this.tideMin = tideMin;
     this.tideMinN = tideMinN;
     this.tideMax = tideMax;
     this.tideMaxN = tideMaxN;
     this.cloudCoverN = cloudCoverN;

   }
   
}

class PerlinCloud {

	PGraphics pg;
	float noiseScale = 0.01f;


	PerlinCloud(int width, int height) {
		pg = createGraphics(width, height);
	}

	public PGraphics draw(float cloudCoverN) {
		  pg.beginDraw();
		  pg.background(0);

		  colorMode(HSB, 360, 600, 100);

		  if(cloudCoverN < 0.6f) {
		  	cloudCoverN = 0;
		  }


		  float CC = map(cloudCoverN, 0, 1.0f, 0, 80);


		  
		  pg.loadPixels();
		  
		  noiseScale = frameCount*0.001f;
		  
		  for (int x=0; x < width; x++) {
		    for (int y=0; y < height; y++) {
		      float noiseVal = noise(x*0.01f+frameCount*0.01f, y*0.01f+frameCount*0.01f, noiseScale)*2;
		    
		      pg.pixels[x+y*width] = color(noiseVal*255, CC);
		      
		      
		    }
		  }
		  
		  pg.updatePixels();
		  pg.endDraw();

		  colorMode(RGB, 255);
		  
		  return pg;
	}



}
    
class RainDrops {
  
  ArrayList<Rain> rains = new ArrayList();
  PGraphics pg;
  
  RainDrops(int width, int height) {
    
    //pg = createGraphics(1080, 720);
    pg = createGraphics(width, height);
     
    for(int i = 0; i < 1000; i++){
     rains.add(new Rain());
    }
    
    
  }
  
  public PGraphics draw(float p) {
   
   pg.beginDraw();
   if(p < 0.025f) {
      pg.background(0, 0, map(p, 0.0f, 1.0f, 140, 100));
   } else {
    pg.background(map(p, 0.0f, 1.0f, 10, 50));
   } 
   
   if( p < 0.025f) {
      p = 0.0f; 
   }
   float treshHold = map(p, 0.0f, 1.0f, 0, 1000);
   
   for (int i = 0; i < rains.size(); i++) {
     if(i<treshHold) {  
        Rain rain = rains.get(i);
        rain.fall();
        rain.show(pg);
        
      }
   }
   
   pg.endDraw();
   return pg;
  }
}

class Rain {
  
  float x;
  float y;
  float z;
  float len;
  float yspeed;
  float mx;

  Rain() {
    x  = random(width);
    y  = random(-height, 0);
    z  = 2.0f;//random(-400, 25);
    len = random(10)+5.0f;
    yspeed  = random(5)+10;

  }

  public void fall() {
    y = y + (yspeed+yspeed*0.1f);
    x = x - 10;//(yspeed+yspeed*0.1);
    
    if (y > height) {
      y = 0;
      yspeed = random(5)+10;
    }
    
     if (x > width) {
      x = 0;
     }
     
     if (x < 0) {
      x = width;
     }
  }

  public void show(PGraphics pg) {
    pg.stroke(255, 150);
    pg.line(x, y, x-10, y+yspeed+len);
  }
}

class SunRise {
  
  Blob b;
  PGraphics pg;
 
  SunRise(int width, int height) {
     b = new Blob(0, 0);
     //pg = createGraphics(1080, 720);
     pg = createGraphics(width, height);


     
  }
  

  public PGraphics draw(float cloudCoverN) {

    float v = map(cloudCoverN, 0, 1.0f, 230, 150);
   if(cloudCoverN > 0.6f) {
     v = 100;
   } 


   pg.beginDraw();
   pg.loadPixels();
    for (int x = 0; x < pg.width; x++) {
      for (int y = 0; y < pg.height; y++) {
        int index = x + y * pg.width;
        float d = dist(x, y, b.pos.x, b.pos.y);
        float col = 350 * b.r / d;
        pg.pixels[index] = color(col, v);
      }
    }
  
  pg.updatePixels();
  pg.endDraw();
  b.update();
  
  return pg;
  }
  
  
  
}


class Blob {
  PVector pos;
  float r;
  PVector vel;
  
  Blob(float x, float y) {
    pos = new PVector (cos(x), sin(y));
    vel = new PVector(-1 , 1);
    r = 80;
    
    pos.x = 0;
    pos.y = 0;
  }
  
  float sliceRotation = (PI*1.0f)/(60.0f*6.0f);

  public void update() {
    pos.x = ((cos( ((-frameCount*sliceRotation)-PI*2.0f)%(PI*1.0f) ) )*width/2) + width/2 ; // + width/2 -100
    pos.y = ((sin( ((-frameCount*sliceRotation)-PI*2.0f)%(PI*1.0f) ) )*height*1.2f) + height*1.5f ; // + height+height/3 -100
    // pos.x = ((width+200.0) - ((frameCount*sliceRotation)%(width+400.0)));
    // pos.y = height/2;

  }

  

}

class TideLines {
	float noiseX;
	float noiseY;
	float noiseF;
	float f = 0.0f;
	float colorState;

	float movingSlider = 0.5f;

	PGraphics pg;

	TideLines(int width, int height) {
		pg = createGraphics(width, height);

		pg.beginDraw();
		pg.background(0);
		pg.endDraw();

		noiseX = random(100);
		noiseY = random(100);
		noiseF = random(100);
	}

	public PGraphics draw(float min, float max) {
		
		//float min = 0.1;
		float normal = 0.5f;
		// max = 1.0;

		
		if(frameCount%(60*6) > 0 && frameCount%(60*6) < (60*1.5f)) {
			movingSlider = (movingSlider*0.95f)+(normal*0.05f);
			//println("NORMAL START");
		} else 

		if(frameCount%(60*6) > (60*1.5f) && frameCount%(60*6) < (60*3)) {
			movingSlider = (movingSlider*0.99f)+(min*0.01f);
			println("MIN");
		
		} else if(frameCount%(60*6) > (60*3) && frameCount%(60*6) < (60*4.5f)) {
			movingSlider = (movingSlider*0.99f)+(max*0.01f);
			println("MAX");		
		} 

		else if(frameCount%(60*6) > (60*4.5f) && frameCount%(60*6) < (60*6)) {
			//movingSlider = (movingSlider*0.95)+(normal*0.05);
			//println("NORMAL END");
		
		}
		
		float slider = map(movingSlider, 0.0f, 1.0f, -height*0.5f, height*1.4f);



		pg.beginDraw();
		pg.strokeWeight(5);
		pg.fill(0, 50);
		pg.noStroke();
		pg.rect(0, 0, pg.width, pg.height);

		f += 0.005f;
  
		float waveH = 100; //100+sin(frameCount*0.1)*300; // map(width/2, 0, width, 100, 500);
		
		for (int h = 0; h < height; h += 10) {
		    
		    if(h < slider) {

		    pg.beginShape(); 
		    //pg.stroke(map(h, frameCount%height, height, 10, 20), map(h, frameCount%height, height, 0, 100), 255);
		    float colorShade = map(h, 0, height, 0, 255);
		    float colorShade2 = map(h, 0, height, 0, 100);
		    float colorShade3 = map(h, slider-(100*movingSlider), height, 0, 255);
		    if(colorShade3<0) {
		    	colorShade3 = 0;
		    }
			pg.stroke(colorShade3, colorShade2+colorShade3, colorShade+colorShade3);

		    float x = 0;
		    float y = h + waveH * noise(noiseX, noiseY + h * 0.1f, noiseF);

		    pg.curveVertex(x, y);
		    
		    for (int w = 0; w <= width+100; w += 50) {
		      x = w;
		      float n = noise(noiseX + w * 0.001f, noiseY + h * 0.01f, noiseF + f);
		      y = h + waveH * n;

		      pg.curveVertex(x, y);
		    }

		    x = width;
		    y = h + waveH * noise(noiseX + width, noiseY + h * 0.01f, noiseF + f);

		    pg.curveVertex(x, y);
		    pg.endShape();

		}



		}

		pg.endDraw();
		return pg;
	}



}

class WindMap {

	FlowField flowfield;
	ArrayList<Vehicle> vehicles;
	PGraphics pg;

	WindMap(int width, int height) {
		//pg = createGraphics(1080, 720);
		pg = createGraphics(width, height);

		flowfield = new FlowField(10);
		vehicles = new ArrayList<Vehicle>();

		for (int i = 0; i < 300; i++) {
		  vehicles.add(new Vehicle(new PVector(random(width), random(height)), random(2, 5), random(0.1f, 1.1f)));
		}
	}

	 public PGraphics draw(int windDirection, float windSpeedN) {

	 	pg.beginDraw();

	 	pg.background(0);
  		flowfield.display(pg);
  		
  		for (Vehicle v : vehicles) {
    		v.follow(flowfield);
    		v.run(pg, windSpeedN);
  		}
  
  		flowfield.init(windDirection);
  		pg.endDraw();	

  		return pg;
	 }


}


class Vehicle {

  // The usual stuff
  PVector position;
  PVector velocity;
  PVector acceleration;
  float r;
  float maxforce;    // Maximum steering force
  float maxspeed;    // Maximum speed
  float extraspeed;

    Vehicle(PVector l, float ms, float mf) {
    position = l.get();
    r = 3.0f;
    maxspeed = ms;
    maxforce = mf;
    extraspeed = random(0.0f, 1.0f);
    acceleration = new PVector(0,0);
    velocity = new PVector(0,0);
  }

  public void run(PGraphics pg, float windSpeedN) {
    update(windSpeedN);
    borders();
    display(pg);
  }

public void follow(FlowField flow) {
    PVector desired = flow.lookup(position);
    
    desired.mult(maxspeed);
    PVector steer = PVector.sub(desired, velocity);
    steer.limit(maxforce);  // Limit to maximum steering force
    applyForce(steer);
  }

  public void applyForce(PVector force) {
    acceleration.add(force);
  }

  public void update(float windSpeedN) {
    
    maxspeed = (windSpeedN*5.25f)+extraspeed*2.0f; // sin(frameCount*0.1)*
    velocity.add(acceleration);
    velocity.limit(maxspeed);
    position.add(velocity);
    acceleration.mult(0);
  }

  public void display(PGraphics pg) {
    pg.pushMatrix();

    pg.stroke(255,0,0);
    float arrowsize = 8;
    pg.translate(position.x,position.y);
    pg.stroke(255);
    pg.rotate(velocity.heading2D());
    float len = velocity.mag();
    float c = map(maxspeed, 2, 7, 0, 255);
    pg.stroke(c, 255-c, 255-c);
    pg.line(len,0,len-arrowsize*2.0f,0);
    pg.line(len,0,len-arrowsize,+arrowsize/2);
    pg.line(len,0,len-arrowsize,-arrowsize/2);
    pg.popMatrix();
  }

  public void borders() {
    if (position.x < -r) position.x = width+r;
    if (position.y < -r) position.y = height+r;
    if (position.x > width+r) position.x = -r;
    if (position.y > height+r) position.y = -r;
  }
}



class FlowField {

  PVector[][] field;
  int cols, rows; 
  int resolution; 

  FlowField(int r) {
    resolution = r;
    cols = width/resolution;
    rows = height/resolution;
    field = new PVector[cols][rows];
    init(0);
  }

  public void init(int windDirection) {
    noiseSeed((int)1000);
    float xoff = 0;

    float windDireciton = windDirection;
    float windDirecitonMapped = map(windDireciton, 0.0f, 360.0f, PI*0.0f, PI*2.0f);

    
    for (int i = 0; i < cols; i++) {
      float yoff = 0;
      for (int j = 0; j < rows; j++) {
        //float theta = map(sin(frameCount*0.01+noise(xoff,yoff)),0,2,0,TWO_PI);
        field[i][j] = new PVector(cos(windDirecitonMapped+noise(xoff,yoff)),sin(windDirecitonMapped+(noise(xoff,yoff)*0.5f) ));
        yoff += 0.1f;
      }
      xoff += 0.1f;
    }


  }

  public void display(PGraphics pg) {
    for (int i = 0; i < cols; i += 2) {
      for (int j = 0; j < rows; j += 2) {
        drawVector(field[i][j],i*resolution,j*resolution,resolution-2, pg);
      }
    }

  }

  public void drawVector(PVector v, float x, float y, float scayl, PGraphics pg) {
    pg.pushMatrix();
    float arrowsize = 4;
    pg.translate(x,y);
    pg.stroke(255, 100);
    pg.rotate(v.heading2D());
    float len = v.mag()*scayl;
    pg.line(-len/2,0,len/2,0);
    pg.colorMode(RGB, 255);
    pg.popMatrix();
  }

  public PVector lookup(PVector lookup) {
    int column = PApplet.parseInt(constrain(lookup.x/resolution,0,cols-1));
    int row = PApplet.parseInt(constrain(lookup.y/resolution,0,rows-1));
    return field[column][row].get();
  }


}
  public void settings() {  size(1080, 720); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#000000", "--hide-stop", "voidmix" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
