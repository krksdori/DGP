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
VideoExport videoExportLogScreen;

ParseJSON parseJSON;
SmoothJson smoothJson;

MoonPhases moonPhases;
RainDrops rainDrops;
SunRise sunRise;
WindMap windMap;
PerlinCloud perlinCloud;
TideLines tideLines;
Temperature temperature;
LogScreen logScreen;

int dayCount = 0;
int dayCountMagic = 0;
int daySpeed = 60*4; // 60*4
int cols = 3;
int rows = 3;
int blockw = 1920;
int blockh = 1080;
int masterw = blockw*3;
int masterh = blockh*3;
boolean activated = false;
boolean firstAction = true;
int ticker = -1;

PGraphics main;
PGraphics master;

ArrayList<Frame> frames = new ArrayList();

boolean exportVideo = true;

public void setup() {
  
  //size(2560, 1440);
  
  //size(2560, 1440);
  //size(7680, 4320);
  println("init");

  main = createGraphics(blockw,blockh);
  master = createGraphics(masterw, masterh);
  
  parseJSON = new ParseJSON();
  
  moonPhases = new MoonPhases(master.width, master.height);
  sunRise = new SunRise(master.width, master.height);
  
  rainDrops = new RainDrops(blockw, blockh);
  
  
  windMap = new WindMap(blockw, blockh);
  perlinCloud = new PerlinCloud(blockw, blockh);
  tideLines = new TideLines(blockw, blockh);
  temperature = new Temperature(blockw, blockh);
  logScreen = new LogScreen(2560, 1440);
  
  parseJSON.parse("result.json");
  smoothJson = new SmoothJson(parseJSON.timeFrames.get(0));

  initVideo();
  
  // populate the frames
  // black input
  PGraphics blank = createGraphics(blockw, blockh);
  blank.beginDraw();
  blank.background(0);
  blank.endDraw();
  for(int i = 0; i< (30*7);i++) {
    frames.add(new Frame(blank));
  }
}

public void initVideo() {
  if(exportVideo) {
      videoExport = new VideoExport(this, "export/options-day-" + dayCount + ".mp4", master);
      videoExport.setQuality(70, 128);
      videoExport.setFrameRate(30);
      videoExport.setLoadPixels(true);
      videoExport.setDebugging(false);
      videoExport.startMovie();

      videoExportLogScreen = new VideoExport(this, "export-log/options-day-" + dayCount + ".mp4", logScreen.pg);
      videoExportLogScreen.setQuality(70, 128);
      videoExportLogScreen.setFrameRate(30);
      videoExportLogScreen.setLoadPixels(true);
      videoExportLogScreen.setDebugging(false);
      videoExportLogScreen.startMovie();
  }
}

public void draw() {
 
  background(0);
  
  if(ticker%(daySpeed) == 0) {
    if(firstAction == false) {
      dayCount = (dayCount%365)+1;
      
    } else {
      firstAction = false;
    }
    initVideo();
  }

  if(dayCount > 0) {
   if((ticker-80)%(daySpeed) == 0) {
    if(firstAction == false) {
      dayCountMagic = (dayCountMagic%365)+1;
    }
  }
  }

  TimeFrame timeFrameSelected = parseJSON.timeFrames.get(dayCount%365);
  TimeFrame timeFrameSelectedMagic = parseJSON.timeFrames.get(dayCountMagic%365);

  smoothJson.newTarget(parseJSON.timeFrames.get((dayCountMagic)%365));
  smoothJson.update();
  TimeFrame timeFrameSmooth = smoothJson.tfCurrent;

  main.beginDraw();



  //main.background(0);
  main.image(rainDrops.draw(timeFrameSelected.precipitationN, timeFrameSelected.windDirection, timeFrameSelected.windSpeedN), 0.0f, 0.0f, blockw, blockh);
  if(timeFrameSelected.windSpeedN > 0.5f) {
    main.blend(windMap.draw(timeFrameSelected.windDirection, timeFrameSelected.windSpeedN), 0, 0, blockw, blockh, 0, 0, blockw, blockh, SCREEN);
  }
  main.blend(perlinCloud.draw(timeFrameSelected.cloudCoverN), 0, 0, blockw, blockh, 0, 0, blockw, blockh, SCREEN);
  
  if(timeFrameSelected.moonPhase.equals("Waning Crescent") || timeFrameSelected.moonPhase.equals("New Moon") || timeFrameSelected.moonPhase.equals("Full Moon") || timeFrameSelected.moonPhase.equals("Waxing Crescent")) {
    main.blend(tideLines.draw(timeFrameSelected.tideMinN, timeFrameSelected.tideMaxN), 0, 0, blockw, blockh, 0, 0, blockw, blockh, SCREEN);  
  }
  
  main.blend(temperature.draw(timeFrameSelected.temperatureN), 0, 0, blockw, blockh, 0, 0, blockw, blockh, SCREEN);
  
  dayNightFade(main, ticker);
  main.endDraw();

  int times = 30;
  if(frames.size() >= 8*times) {
    frames.remove(0);
    activated = true;
  }
  
  frames.add(new Frame(main.get()));

  int x = 0;
  int y = 0;

 
  int index = 0;
  
  master.beginDraw();
  master.background(100);

  for(Frame f : frames) {    
    
    if(index == 7*times) {  // 7
        x = (masterw/3)*1;
        y = 0;
        master.image(f.img, x, y, masterw/cols, masterh/rows);
    } else if(index == 6*times) { // 6
        x = (masterw/3)*2;
        y = 0;
        master.image(f.img, x, y, masterw/cols, masterh/rows);
    } else if(index == 5*times) { // 5
        x = (masterw/3)*2;
        y = (masterh/3);
        master.image(f.img, x, y, masterw/cols, masterh/rows);
    } else if(index == 4*times) { // 4
        x = (masterw/3)*2;
        y = (masterh/3)*2;
        master.image(f.img, x, y, masterw/cols, masterh/rows);
    } else if(index == 3*times) {// 3
        x = (masterw/3)*1;
        y = (masterh/3)*2;
        master.image(f.img, x, y, masterw/cols, masterh/rows);
    } else if(index == 2*times) { // 2
        x = (masterw/3)*0;
        y = (masterh/3)*2;
        master.image(f.img, x, y, masterw/cols, masterh/rows);
    } else if(index == 1*times) { // 1
        x = (masterw/3)*0;
        y = (masterh/3)*1;
        master.image(f.img, x, y, masterw/cols, masterh/rows);
    } else if(index == 0*times) { // 0
        x = (masterw/3)*0;
        y = (masterh/3)*0;
        master.image(f.img, x, y, masterw/cols, masterh/rows);
    } 
    index++;
  }
    
 //image(main, 0, 0);

 if(activated) {
   master.image(logScreen.draw(timeFrameSelectedMagic, timeFrameSmooth, dayCount, ticker), masterw/cols, masterh/rows, masterw/cols, masterh/rows);
  //image(logScreen.draw(timeFrameSelected, dayCount, ticker), 0, 0);
 }
  
 master.blend(sunRise.draw(timeFrameSelectedMagic.cloudCoverN, ticker), 0, 0, blockw, blockh, 0, 0, master.width, master.height, SCREEN);
 master.blend(moonPhases.draw(timeFrameSmooth.moonAge, ticker, timeFrameSelectedMagic.cloudCoverN), 0, 0, master.width, master.height, 0, 0, master.width, master.height, SCREEN);
 
 master.noStroke();
 master.fill(map(timeFrameSmooth.mosh, 0.0f, 1.0f, 0.0f, 255.0f));
 //master.fill(255);
 master.rect(master.width/2, master.height/2 + blockh /2 - 10, 10, 10);
  
 master.endDraw();
 image(master, 0, 0, width, height);
 
 fill(255);
 text("day " + dayCount + " ,date " + timeFrameSelected.date, 50, 50);
 text("ticker: " + ticker, 50, 70);
 if(activated) {
   println(ticker);
  }
 text("dayspeed: " + daySpeed, 50, 90);
 //master.text("moonAge: " + timeFrameSelected.moonAge, 50, 110);
 //master.text("moonVisible: " + timeFrameSelected.moonVisible+"%", 50, 130);
 //master.text("moonPhase: " + timeFrameSelected.moonPhase, 50, 150);
 //master.text("windDirection: " + timeFrameSelected.windDirection, 50, 170);
 //master.text("windSpeed: " + timeFrameSelected.windSpeed, 50, 190);
 //master.text("windSpeedN: " + timeFrameSelected.windSpeedN, 50, 210);
 //master.text("tideMinN: " + timeFrameSelected.tideMinN, 50, 230);
 //master.text("tideMaxN: " + timeFrameSelected.tideMaxN, 50, 250);
 //master.text("cloudCoverN: " + timeFrameSelected.cloudCoverN, 50, 270);
 //master.text("temperature: " + timeFrameSelected.temperature, 50, 290);
 //master.text("temperatureN: " + timeFrameSelected.temperatureN, 50, 310);
  
 if(exportVideo) {
   if(activated) {
    videoExport.saveFrame();
    videoExportLogScreen.saveFrame();
   }
 }
  
 if(ticker%(daySpeed) == daySpeed-1) {
    if(exportVideo) {
     videoExport.endMovie();
     videoExportLogScreen.endMovie();
      // if(dayCount%365==0) {
      //   exit();
      // }
    }
   
 }
 
 if(activated == true) {
    ticker++;
  }
 
}

public void dayNightFade(PGraphics main, int counter) {
 int fadeTime = (int) (daySpeed/2.2f); // 7.2
 float BGColor = map(counter%daySpeed, 0, fadeTime, 255, 0);
 main.noStroke();
 main.fill(0, BGColor);
 main.rect(0, 0, blockw, blockh);
 //println(counter%daySpeed);
 BGColor = map(counter%daySpeed, (60*2.5f)-fadeTime, (60*2.5f), 0, 255);
 main.fill(0, BGColor);
 main.rect(0, 0, blockw, blockh); 
}

public void keyPressed() {
  if (key == 'q') {
    if(exportVideo) {
      videoExport.endMovie();
      videoExportLogScreen.endMovie();
      exit();
    }
  }
}

class Frame {
  
    PImage img = new PImage(blockw, blockh);
    
    Frame(PImage img) {
     this.img = img; 
    }
}
class Column {
	String[] type = new String[3];
	Column() {
	}
}

class LogScreen {

	PGraphics pg;
	PFont font;
	int blockw = 2560;
    int blockh = 1440;
  	int lastDayCount = -1;
  	float lineCounter = 0;
  	int lineCounterTarget = 0;
  	int removed = 0;
  	float lineHeight = 15;
  	ArrayList<Column> columns = new ArrayList<Column>();
  	ArrayList<Column> columnsTarget = new ArrayList<Column>();
  	int fontSize = 20;
    PImage tempScreen;
    float currentOffset = -100;
    float targetOffset = -100;
    HashMap<String, PImage> moons = new HashMap<String, PImage>();
    String[] moonTitles = {  "FirstQuarter", "NewMoon", "WaxingCrescent", "FullMoon", "WaningCrescent", "WaxingGibbous", "LastQuarter", "WaningGibbous" };
    
	LogScreen(int _width, int _height) {
      	blockw = _width;
      	blockh = _height;

      	font = createFont("data/font/SpaceMono-Regular.ttf", fontSize);
		pg = createGraphics(_width, _height);
		tempScreen = loadImage("log-temp.png");

		for(int i = 0; i < 8; i++) {
			columns.add(new Column());
			columnsTarget.add(new Column());
		}
		
		for(String i : moonTitles) {
			moons.put(i, loadImage("moon/"+i+".png") );
			println("moon/"+i+".png");
		}
	}

	public void setType(TimeFrame timeFrameSelected, TimeFrame tfCurrent, int dayCount) {
		for(int i = 0; i < columnsTarget.size(); i++) {
			if(i == 0) {
				columnsTarget.get(i).type[0] = "Moon Phase";
				columnsTarget.get(i).type[1] = timeFrameSelected.moonPhase+"";
				columnsTarget.get(i).type[2] = "";
				
			} else if(i == 1) {
				columnsTarget.get(i).type[0] = "Wind Direction" ;
				columnsTarget.get(i).type[1] = timeFrameSelected.windDirectionTitle+"";
				columnsTarget.get(i).type[2] = timeFrameSelected.windDirectionArraw+"";
				
			} else if(i == 2) {
				columnsTarget.get(i).type[0] = "Wind Speed";
				columnsTarget.get(i).type[1] = nf(timeFrameSelected.windSpeed,2, 2)+" m/s";
				
				if(timeFrameSelected.windSpeedN > 0.5f) {
					columnsTarget.get(i).type[2] = nf(dist(0.5f, 0.0f,timeFrameSelected.windSpeedN, 0.0f)*100.0f, 2, 2) + " % ↑";
				} else if(timeFrameSelected.windSpeed < 0.5f) {
					columnsTarget.get(i).type[2] = "-" + nf(dist(0.5f, 0.0f,timeFrameSelected.windSpeedN, 0.0f)*100.0f, 2, 2) + " % ↓";
				} else {
					columnsTarget.get(i).type[2] = 	"00.00 %";
				}
				
			} else if(i == 3) {
				columnsTarget.get(i).type[0] = "Cloud Cover";
				columnsTarget.get(i).type[1] = nf(timeFrameSelected.cloudCover, 2, 2) +" %";
				
				if(timeFrameSelected.cloudCoverN > 0.5f) {
					columnsTarget.get(i).type[2] = nf(dist(0.5f, 0.0f,timeFrameSelected.cloudCoverN, 0.0f)*100.0f, 2, 2) + " % ↑";
				} else if(timeFrameSelected.cloudCoverN < 0.5f) {
					columnsTarget.get(i).type[2] = "-" + nf(dist(0.5f, 0.0f,timeFrameSelected.cloudCoverN, 0.0f)*100.0f, 2, 2) + " % ↓";
				} else {
					columnsTarget.get(i).type[2] = 	"00.00 %";
				}
				
			} else if(i == 4) {
				columnsTarget.get(i).type[0] = "Precipitation";
				columnsTarget.get(i).type[1] = nf(timeFrameSelected.precipitation, 2, 2)+" mm";
				
				if(timeFrameSelected.precipitationN > 0.5f) {
					columnsTarget.get(i).type[2] = nf(dist(0.5f, 0.0f,timeFrameSelected.precipitationN, 0.0f)*100.0f, 2, 2) + " % ↑";
				} else if(timeFrameSelected.precipitationN < 0.5f) {
					columnsTarget.get(i).type[2] = "-" + nf(dist(0.5f, 0.0f,timeFrameSelected.precipitationN, 0.0f)*100.0f, 2, 2) + " % ↓";
				} else {
					columnsTarget.get(i).type[2] = 	"00.00 %";
				}
				
			} else if(i == 5) {
				columnsTarget.get(i).type[0] = "Temperature";
				columnsTarget.get(i).type[1] = nf(timeFrameSelected.temperature, 2, 2)+" °C";
				
				if(timeFrameSelected.temperatureN > 0.5f) {
					columnsTarget.get(i).type[2] = nf(dist(0.5f, 0.0f,timeFrameSelected.temperatureN, 0.0f)*100.0f, 2, 2) + " % ↑";
				} else if(timeFrameSelected.temperatureN < 0.5f) {
					columnsTarget.get(i).type[2] = "-" + nf(dist(0.5f, 0.0f,timeFrameSelected.temperatureN, 0.0f)*100.0f, 2, 2) + " % ↓";
				} else {
					columnsTarget.get(i).type[2] = 	"00.00 %";
				}
				
			} else if(i == 6) {
				columnsTarget.get(i).type[0] = "Low Tide";
				columnsTarget.get(i).type[1] = nf(timeFrameSelected.tideMin, 2, 2)+" cm";
				
				if(timeFrameSelected.tideMinN > 0.5f) {
					columnsTarget.get(i).type[2] = nf(dist(0.5f, 0.0f,timeFrameSelected.tideMinN, 0.0f)*100.0f, 2, 2) + " % ↑";
				} else if(timeFrameSelected.tideMinN < 0.5f) {
					columnsTarget.get(i).type[2] = "-" + nf(dist(0.5f, 0.0f,timeFrameSelected.tideMinN, 0.0f)*100.0f, 2, 2) + " % ↓";
				} else {
					columnsTarget.get(i).type[2] = 	"00.00 %";
				}

				
			} else if(i == 7) {
		        columnsTarget.get(i).type[0] = "High Tide";
		        columnsTarget.get(i).type[1] = nf(timeFrameSelected.tideMax, 2, 2)+" cm";
		        
		        if(timeFrameSelected.tideMaxN > 0.5f) {
					columnsTarget.get(i).type[2] = nf(dist(0.5f, 0.0f,timeFrameSelected.tideMaxN, 0.0f)*100.0f, 2, 2) + " % ↑";
				} else if(timeFrameSelected.tideMaxN < 0.5f) {
					columnsTarget.get(i).type[2] = "-" + nf(dist(0.5f, 0.0f,timeFrameSelected.tideMaxN, 0.0f)*100.0f, 2, 2) + " % ↓";
				} else {
					columnsTarget.get(i).type[2] = 	"00.00 %";
				}

      		}
 
		}
		columns = columnsTarget;
	}

	public void updateType() {

	}

	public String showType(String in, float slider) {
		String text = "";
		for(int s = 0; s < floor(slider*in.length()); s++) {
			if(s < floor(slider*in.length())/2) {
				text += in.charAt(floor(random(0, in.length())));
			} else {
				text += in.charAt(s);
			}
		}
		return text;
	}

	public PGraphics draw(TimeFrame timeFrameSelected, TimeFrame tfCurrent, int dayCount, int ticker) {

		setType(timeFrameSelected, tfCurrent, dayCount);
		updateType();

		pg.beginDraw();
		pg.background(0);
    
    	//pg.image(tempScreen, 0.0, 0.0);

		pg.textFont(font);

		// pg.fill(0,255,0);
		// pg.noStroke();
		// pg.rect(0,0,width, 20);
		// pg.rect(0,0,20,height);
		// pg.rect(width-20,0,20,height);
		// //pg.fill(0,255,255);
		// pg.rect(0,height-20,width, 20);

    	//pg.noStroke();
    	//pg.fill(255, 0, 0);
    	//pg.rect(0, pg.height-10, 10, 10);
    	
    	pg.pushMatrix();
    	pg.text("Date     " + timeFrameSelected.date, 20, 30);
    	pg.text("Location     52°04’52.8”N 4°19’09.7”E", pg.width-472, 30);
    	pg.translate(0, pg.height-50);
    	pg.text("Data Source     KNMI", 20, 30);
    	pg.textSize(15);
		pg.text("(KNMI — Royal Netherlands Meteorological Institute)", pg.width-487, 30);
    	pg.textSize(20);
		
    	pg.popMatrix();	

    	
    	pg.pushMatrix();
    	
    	pg.translate(678, 500);		

    	pg.stroke(255);
    	//float offset = Math.abs(sin(ticker*0.01)*400.0);

    	if((ticker-80)%(daySpeed) == 0) {
    		targetOffset = 450.0f;
      	}

      	if((ticker-80)%(daySpeed) == 100) {
    		targetOffset = -100.0f;
      	}

    	currentOffset = currentOffset*0.95f + targetOffset *0.05f;
    	//pg.line(0.0, currentOffset, 1200.0, currentOffset);

    	float distM = ((0) + 20) - currentOffset;
		float mapDistM = map(distM, 100, 0, 0, 255.0f);
		if(distM<0) {
			mapDistM = 255.0f;
		}

    	pg.tint(255, mapDistM);
    	pg.image(moons.get(timeFrameSelected.moonImageName), 1040.0f, 5.0f, 20, 20);
    	pg.tint(255, 255);
    	

		int lineHeight = 40;
		int spacingC1 = 520;
		int spacingC2 = 520*2;
		int spacingC3 = 520*3;

		int i = 0;
		for(Column c : columns) {
      		pg.pushMatrix();

      		float dist = ((i*lineHeight) + 20) - currentOffset;
			pg.translate(0, (i*lineHeight) + 20);
			
			float mapDist = map(dist, 100, 0, 0, 110.0f);
			if(dist<0) {
				mapDist = 110.0f;
			}

			pg.fill(mapDist);

			if(c.type[0].length() > 0) {
				if(str(c.type[0].charAt(0)).equals("-")) {
					pg.text(c.type[0], -12, 0);
				} else {
					pg.text(c.type[0], 0, 0);
				}
			}

			if(c.type[1].length() > 0) {
				if(str(c.type[1].charAt(0)).equals("-")) {
					pg.text(c.type[1], spacingC1-12, 0);
				} else {
					pg.text(c.type[1], spacingC1, 0);
				}
			}

			if(c.type[2].length() > 0) {
				if(str(c.type[2].charAt(0)).equals("-")) {
					pg.text(c.type[2], spacingC2-12, 0);
				} else {
					pg.text(c.type[2], spacingC2, 0);
				}
			}
			

			//pg.text(c.type[1], spacingC1, 0);
			//pg.text(c.type[2], spacingC2, 0);
			pg.popMatrix();
			i++;
		}

    pg.popMatrix();
		
		pg.endDraw();
		return pg;		
	}


}

class MoonPhases {
     
  float currentf = 255;
  int f = color(255);
  int bg = color(0,0,0);
  PShader blur;
  float theta;
  float t;
  float radius = 500;
  int frames = 600;
  PGraphics moonTexture; 
  PGraphics moonPhases;
  PGraphics moon;
  int blockw = 2560;
  int blockh = 1440;
  PGraphics pg;
  
  MoonPhases(int _width, int _height) {
      blockw = _width;
      blockh = _height;
      //pg = createGraphics(1080, 720);
      pg = createGraphics(_width, _height);

      moonTexture = createGraphics(blockw, blockh);
      moonPhases = createGraphics(blockw, blockh);
      moon = createGraphics(blockw, blockh);

      moonTexture.beginDraw();
      moonTexture.background(255);
      for(int i = 0; i< 24000; i++){
        float t = random(TWO_PI);
        
        moonTexture.strokeWeight(0.1f);
        moonTexture.stroke(0, 0, 0, 10);

        float x1 = blockw/2 + radius*0.5f * cos(t);
        float y1 = blockh/2 + radius*0.5f * sin(t);

        t = random(TWO_PI);

        float x2 = blockw/2 + radius*0.5f * cos(t);
        float y2 = blockh/2 + radius*0.5f * sin(t);
        moonTexture.line(x1, y1, x2, y2);
      }
    
      moonTexture.endDraw();
      //moonPhasesDraw(moonPhases, 0.0);

  }

  public void moonPhasesDraw(PGraphics p, float moonAge, float cloudCoverN) {
    p.beginDraw();
    float mapMoonData = map(moonAge, 0.0f, 29.53059f, 600.0f, 0.0f);

//println(moonAge);
    t = ((mapMoonData+300)%frames)/(float)frames;
    p.background(0);
    p.translate(blockw/2, blockh/2);

    float newf = map(cloudCoverN, 0, 1, 255, 200);
    //float newf = map(mouseX, 0, width, 120, 255);

    currentf = currentf*0.9f + newf * 0.1f;
    f = color(currentf);
    //f = color(255, map(cloudCoverN, 0, 1, 10, 200));
    //f = color(100);
    //float moonRotate = map(moonAge, 0.0, 29.53059, PI*0.0, -PI*2.0);

    //float rX = cos(moonRotate)*blockw/3;
    //float rY = sin(moonRotate)*blockh/3;

    //p.rotate(moonRotate);
    //p.translate(floor(rX), floor(rY));
    //p.rotate(frameCount*0.1);


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

public PGraphics rotateMoon(float moonAge, float x, float y, float cloudCoverN) {
  moon.beginDraw();
  moon.pushMatrix();
  moon.translate(blockw/2,blockh/2);

  float moonRotate = map(moonAge, 0.0f, 29.53059f, PI*0.0f, -PI*2.0f);

  //moon.rotate(moonRotate);

  float rX = cos(moonRotate)*blockw/3;
  float rY = sin(moonRotate)*blockh/3;
  
 // moon.translate(floor(rX), floor(rY));
    
  //moon.rotate( (float)(frameCount*0.02*Math.sin(frameCount*0.005)*0.01) );
  //moon.fill(255, 0, 0);

  
  // moon.rect(0, 0, width,height);
  moon.translate(x, y);
  //moon.tint(25, 255);//map(cloudCoverN, 0.0, 1.0, 255, 10));
  moon.image(moonTexture, -blockw/2, -blockh/2);
  //moon.fill(255, 0, 0);
  //moon.rect(0, 0, width, height);
  moon.popMatrix();
  moon.endDraw();
  return moon;
}
  
  public PGraphics draw(float moonAge, int ticker, float cloudCoverN) {

    
    pg.beginDraw();
    pg.background(0);

    //float moonRotate = map(moonAge, 0.0, 29.53059, PI*0.0, -PI*2.0);

    float sliceRotation = ((PI*2.0f))/( ((60.0f*4.0f)*29.53059f) );

    float x = ((cos( ((ticker*-sliceRotation-PI*0.15f))%(PI*2.0f) ) )*blockw/3.3f) ;//+ blockw/2 ; // + width/2 -100
    float y = ((sin( ((ticker*-sliceRotation-PI*0.15f))%(PI*2.0f) ) )*blockw/4.3f) ; //+blockh*1.5 ; // + height+height/3 -100
    

    //float rX = cos(moonRotate)*blockw/3;
    //float rY = sin(moonRotate)*blockh/3;
    //pg.translate(floor(rX), floor(rY));
    pg.translate(x, y);
    pg.pushMatrix();

    moonPhasesDraw(moonPhases, moonAge, cloudCoverN);
    

    pg.image(moonPhases, 0, 0);
    pg.pushMatrix();

    pg.blend(rotateMoon(moonAge, x, y, cloudCoverN), 0, 0, blockw, blockh, 0, 0, blockw, blockh, SUBTRACT);
    pg.popMatrix();
    pg.popMatrix();

    pg.endDraw();
    
    
    return pg;
  }
  
}
class TimeFrame {
  
   String date;
  
   float precipitation;
   float precipitationN;

   float moonAge;
   int moonVisible;
   String moonPhase;
   String moonImageName;

   float windDirection;
   String windDirectionTitle;
   String windDirectionArraw;
   float windSpeed;
   float windSpeedN;

   int tideMin;
   float tideMinN;
   int tideMax;
   float tideMaxN;

   float cloudCover;
   float cloudCoverN;
   
   float temperature;
   float temperatureN;
   
   float mosh;

   TimeFrame() {

   }
   
   TimeFrame(String date, float precipitation, float precipitationN, float moonAge, int moonVisible, String moonPhase, String moonImageName, float windDirection, String windDirectionTitle, String windDirectionArraw, float windSpeed, float windSpeedN, int tideMin, float tideMinN, int tideMax, float tideMaxN, float cloudCover, float cloudCoverN, float temperature, float temperatureN, float mosh) {
     this.date = date;
     this.precipitation = precipitation;
     this.precipitationN = precipitationN;
     this.moonAge = moonAge;
     this.moonVisible = moonVisible;
     this.moonPhase = moonPhase;
     this.moonImageName = moonImageName;
     this.windDirection = windDirection;
     this.windSpeed = windSpeed;
     this.windSpeedN =windSpeedN;
     this.tideMin = tideMin;
     this.tideMinN = tideMinN;
     this.tideMax = tideMax;
     this.tideMaxN = tideMaxN;
     this.cloudCover = cloudCover;
     this.cloudCoverN = cloudCoverN;
     this.windDirectionTitle = windDirectionTitle;
     this.windDirectionArraw = windDirectionArraw;
     this.temperature = temperature;
     this.temperatureN = temperatureN;
     this.mosh = mosh;
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
        String moonImageName = trim(moonPhase.replace(" ", ""));
        println(moonImageName);
        // WIND
        float windDirection = timeFrame.getInt("windDirection")*1.0f;
        JSONObject windJO = timeFrame.getJSONObject("windSpeed");
        float windSpeedValue = PApplet.parseFloat( nf(windJO.getInt("value")*0.1f, 1, 2));
        float windSpeedValueN = windJO.getFloat("normalized");
        //360=north, 90=east, 180=south, 270=west, 0=calm/variable //45
        String[] windTitleOptions = { "North", "Northeast", "East", "Southeast", "South", "Southwest", "West", "Northwest" };
        String[] windArrowOptions = { "↑", "↗", "→", "↘", "↓", "↙", "←", "↖" };
        String windDirectionTitle = windTitleOptions[0];
        String windDirectionArraw = windArrowOptions[0];

        if(windDirection > 360-(45/2) && windDirection < 45/2) {
            windDirectionTitle = windTitleOptions[0];
            windDirectionArraw = windArrowOptions[0];
        }
        if(windDirection > (45/2) && windDirection < (45/2) + 45) {
            windDirectionTitle = windTitleOptions[1];
            windDirectionArraw = windArrowOptions[1];
        }
        if(windDirection > (45/2) + 45 && windDirection < (45/2) + 45 * 2) {
            windDirectionTitle = windTitleOptions[2];
            windDirectionArraw = windArrowOptions[2];
        }
        if(windDirection > (45/2) + 45 * 2 && windDirection < (45/2) + 45 * 3) {
            windDirectionTitle = windTitleOptions[3];
            windDirectionArraw = windArrowOptions[3];
        }
        if(windDirection > (45/2) + 45 * 3 && windDirection < (45/2) + 45 * 4) {
            windDirectionTitle = windTitleOptions[4];
            windDirectionArraw = windArrowOptions[4];
        }
        if(windDirection > (45/2) + 45 * 4 && windDirection < (45/2) + 45 * 5) {
            windDirectionTitle = windTitleOptions[5];
            windDirectionArraw = windArrowOptions[5];
        }
        if(windDirection > (45/2) + 45 * 5 && windDirection < (45/2) + 45 * 6) {
            windDirectionTitle = windTitleOptions[6];
            windDirectionArraw = windArrowOptions[6];
        }
        if(windDirection > (45/2) + 45 * 6 && windDirection < (45/2) + 45 * 7) {
            windDirectionTitle = windTitleOptions[7];
            windDirectionArraw = windArrowOptions[7];
        }

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

        // CLOUD
        JSONObject cloudCoverJO = timeFrame.getJSONObject("cloudCover");
        float cloudCover = PApplet.parseFloat(nf(map(cloudCoverJO.getFloat("value"), 0, 8, 0, 99), 2, 2));
        float cloudCoverN = cloudCoverJO.getFloat("normalized");
        
        // TEMPERATURE
        JSONObject temperatureJO = timeFrame.getJSONObject("temperature");
        float temperature = PApplet.parseFloat(nf(temperatureJO.getFloat("value")*0.1f, 1, 2));
        float temperatureN = temperatureJO.getFloat("normalized");
        
        // MOSH
        float mosh = timeFrame.getFloat("mosh");

        timeFrames.add(new TimeFrame(date, precipitation, precipitationN, moonAge, moonVisible, moonPhase, moonImageName, windDirection, windDirectionTitle, windDirectionArraw, windSpeedValue, windSpeedValueN, tideMin, tideMinN, tideMax, tideMaxN, cloudCover, cloudCoverN, temperature, temperatureN, mosh));
       // println(timeFrames.size());
    }
  }
}





class SmoothJson {

  TimeFrame tfCurrent;
  TimeFrame tfTarget;

  SmoothJson(TimeFrame first) {
     TimeFrame newTf = new TimeFrame();
     newTf.date = first.date;
     newTf.precipitation = first.precipitation;
     newTf.precipitationN = first.precipitationN;
     newTf.moonAge = first.moonAge;
     newTf.moonVisible = first.moonVisible;
     newTf.moonPhase = first.moonPhase;
     newTf.windDirection = first.windDirection;
     newTf.windSpeed = first.windSpeed;
     newTf.windSpeedN = first.windSpeedN;
     newTf.tideMin = first.tideMin;
     newTf.tideMinN = first.tideMinN;
     newTf.tideMax = first.tideMax;
     newTf.tideMaxN = first.tideMaxN;
     newTf.cloudCover = first.cloudCover;
     newTf.cloudCoverN = first.cloudCoverN;
     newTf.temperature = first.temperature;
     newTf.temperatureN = first.temperatureN;
     newTf.mosh = first.mosh;
     this.tfCurrent = newTf;
  }

  public void newTarget(TimeFrame target) {
    TimeFrame newTf = new TimeFrame();
     newTf.date = target.date;
     newTf.precipitation = target.precipitation;
     newTf.precipitationN = target.precipitationN;
     newTf.moonAge = target.moonAge;
     newTf.moonVisible = target.moonVisible;
     newTf.moonPhase = target.moonPhase;
     newTf.windDirection = target.windDirection;
     newTf.windSpeed = target.windSpeed;
     newTf.windSpeedN = target.windSpeedN;
     newTf.tideMin = target.tideMin;
     newTf.tideMinN = target.tideMinN;
     newTf.tideMax = target.tideMax;
     newTf.tideMaxN = target.tideMaxN;
     newTf.cloudCover = target.cloudCover;
     newTf.cloudCoverN = target.cloudCoverN;
     newTf.temperature = target.temperature;
     newTf.temperatureN = target.temperatureN;
     newTf.mosh = target.mosh;
     tfTarget = newTf;
  }

  public void update() {
      tfCurrent.mosh = tfCurrent.mosh*0.9f + tfTarget.mosh*0.1f;
      
      //tfCurrent.windSpeedN = tfCurrent.windSpeedN*0.9 + tfTarget.windSpeedN*0.1;
      //tfCurrent.windDirection = tfCurrent.windDirection*0.99 + tfTarget.windDirection*0.01;
      tfCurrent.temperatureN = tfCurrent.temperatureN*0.99f + tfTarget.temperatureN*0.01f;
      tfCurrent.moonAge = tfCurrent.moonAge*0.99f + tfTarget.moonAge*0.01f;
  }

}

class PerlinCloud {

  PGraphics pg;
  float noiseScale = 0.01f;
  int blockw = 2560;
  int blockh = 1440;


  PerlinCloud(int _width, int _height) {
    pg = createGraphics(_width, _height);
    blockw = _width;
    blockh = _height;
  }

  public PGraphics draw(float cloudCoverN) {
    pg.beginDraw();
    pg.background(0);

    colorMode(HSB, 360, 600, 100);

    if (cloudCoverN < 0.6f) {
      cloudCoverN = 0;
    }


    float CC = map(cloudCoverN, 0, 1.0f, 0, 150);



    pg.loadPixels();

    noiseScale = frameCount*0.001f;

    for (int x=0; x < blockw; x++) {
      for (int y=0; y < blockh; y++) {
        float noiseVal = noise(x*0.01f+frameCount*0.01f, y*0.01f+frameCount*0.01f, noiseScale)*2;

        pg.pixels[x+y*blockw] = color(noiseVal*255, CC);
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
  int blockw = 2560;
  int blockh = 1440;
  float rainOffset = 0.0f;
  float rainOffsetCurrent = 0.0f;
  float rainOffsetTarget = 0.0f;
  
  RainDrops(int _width, int _height) {
    
    //pg = createGraphics(1080, 720);
    pg = createGraphics(_width, _height);
    
    blockw = _width;
    blockh = _height;
     
    for(int i = 0; i < 1000; i++){
     rains.add(new Rain());
    }
    
    
  }
  
  public PGraphics draw(float p, float windDirection, float windSpeed) {
   
   pg.beginDraw();
   pg.background(100);


   if(p < 0.025f) {
     pg.background(0, 0, map(p, 0.0f, 1.0f, 140, 100));
   //    pg.background(map(p, 0.0, 1.0, 10, 50));
   
     //pg.background(0, 0, 255);
   } else {
     //pg.background(255, 0, 0);
     pg.background(map(p, 0.0f, 1.0f, 10, 50));
   } 
   
   if( p < 0.025f) {
      p = 0.0f; 
   }
   float treshHold = map(p, 0.0f, 1.0f, 0, 1000);
   
   rainOffset = 0.0f;
   if(windDirection > 300 || windDirection < 60) {
      rainOffset = -40*windSpeed;
      rainOffsetTarget = map(windDirection, 300, 60, -40*windSpeed, -0*windSpeed);
      //println("right");
   } else if(windDirection > 120 && windDirection < 240) {
      rainOffset = 40*windSpeed;
      rainOffsetTarget = map(windDirection, 120, 240, 40*windSpeed, 0*windSpeed);
      //println("left");
   }


   //rainOffsetCurrent = rainOffsetCurrent*0.9 + rainOffsetTarget*0.1;

  // rainOffset = rainOffsetCurrent;

   for (int i = 0; i < rains.size(); i++) {
     if(i<treshHold) {   //treshHold
        Rain rain = rains.get(i);
        rain.fall(rainOffset);
        rain.show(pg, rainOffset);
        
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
    x  = random(blockw);
    y  = random(-blockh, 0);
    z  = 2.0f;//random(-400, 25);
    len = random(10)+5.0f;
    yspeed  = random(5)+10;

  }

  public void fall(float rainOffset) {
    y = y + (yspeed+yspeed*0.1f);
    x = x - rainOffset;//(yspeed+yspeed*0.1);
    
    if (y > blockh) {
      y = 0;
      yspeed = random(5)+10;
    }
    
     if (x > blockw) {
      x = 0;
     }
     
     if (x < 0) {
      x = blockw;
     }
  }

  public void show(PGraphics pg, float rainOffset) {
    pg.stroke(255, 150);
    pg.line(x, y, x-rainOffset, y+yspeed+len);
  }
}

class SunRise {
  
  Blob b;
  PGraphics pg;
  int blockw = 2560;
  int blockh = 1440;
  float vCurrent = 0.0f;
  
  SunRise(int _width, int _height) {
     b = new Blob(0, 0);
     //pg = createGraphics(1080, 720);
     pg = createGraphics(_width, _height);
     blockw = _width;
     blockh = _height;


     
  }
  

  public PGraphics draw(float cloudCoverN, int ticker) {

   float v = map(cloudCoverN, 0, 1.0f, 230, 150);
   if(cloudCoverN > 0.6f) {
     v = 100;
   } 

   vCurrent = vCurrent*0.9f + v * 0.1f;


   pg.beginDraw();
   pg.loadPixels();
    for (int x = 0; x < pg.width; x++) {
      for (int y = 0; y < pg.height; y++) {
        int index = x + y * pg.width;
        float d = dist(x, y, b.pos.x, b.pos.y);
        float col = 350 * b.r / d;
        pg.pixels[index] = color(col, vCurrent);
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
    r = 50;
    
    pos.x = 0;
    pos.y = 0;
  }
  
  float sliceRotation = ((PI*2.0f))/(60.0f*4.0f);
  
  // 5 = 100
  // 3 = 60
  // 

  public void update() { //-PI*1.25
    pos.x = ((cos( ((ticker*sliceRotation)-PI*1.25f)%(PI*2.0f) ) )*blockw/3.3f) + blockw/2 ;//+ blockw/2 ; // + width/2 -100
    pos.y = ((sin( ((ticker*sliceRotation)-PI*1.25f)%(PI*2.0f) ) )*blockw/4.3f) + blockh/2 ; //+blockh*1.5 ; // + height+height/3 -100
    // pos.x = ((width+200.0) - ((frameCount*sliceRotation)%(width+400.0)));
    // pos.y = height/2;

  }

  

}

class Temperature {

  PGraphics pg;

  //color warm = color(255, 100, 0);
  //color cold = color(0, 100, 255);



  int c1 = color(13,74,139); // cold
  int c2 = color(103, 168, 222);
  int c3 = color(198, 223, 223);
  int c4 = color(208, 223, 223); // warm
	
  int blockw = 2560;
  int blockh = 1440;

	Temperature(int _width, int _height) {
		pg = createGraphics(_width, _height);
    	blockw = _width;
    	blockh = _height;
	}

	public PGraphics draw(float temperature) {
		  pg.beginDraw();
		  pg.loadPixels();

		  float c = temperature;//map(mouseX, 0, width, 0, 1.0);// temperature;
      //println(temperature);

		  int sc = color(0.0f); 
		  int sc2 = color(0.0f);
		  if(c<0.5f) {
			  sc = lerpColor(c1, c2, map(c, 0.0f, 0.5f, 0.0f, 1.0f) );
		  }
		  if(c > 0.5f) {
			  sc2 = lerpColor(c3, c4, map(c, 0.5f, 1.0f, 0.0f, 1.0f) );
		 }

      if(c > 0.85f) {
        sc = color(255, 228, 196);
        sc2 = color(0, 0, 0);
      }

      	//float finalR = red(cold)*(1.0-temperature) + red(warm)*(temperature); 
      	//float finalG = green(cold)*(1.0-temperature) + green(warm)*(temperature); 
      	//float finalB = blue(cold)*(1.0-temperature) + blue(warm)*(temperature); 

      	  //color sc = lerpColor(cold, warm, map(mouseX, 0, width, 0, 1));
		  for (int i = 0; i < blockw*blockh; i++) {
  				pg.pixels[i] = color(sc+sc2, 200);
		  }



		  pg.updatePixels();


		  pg.endDraw();


		  return pg;
	}

}
class TideLines {
	float noiseX;
	float noiseY;
	float noiseF;
	float f = 0.0f;
	float colorState;
  	float movingSlider = 0.5f;
  	int blockw = 2560;
  	int blockh = 1440;

	PGraphics pg;

	TideLines(int _width, int _height) {
		pg = createGraphics(_width, _height);
    
    	blockw = _width;
    	blockh = _height;

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
			//println("MIN");
		
		} else if(frameCount%(60*6) > (60*3) && frameCount%(60*6) < (60*4.5f)) {
			movingSlider = (movingSlider*0.99f)+(max*0.01f);
			//println("MAX");		
		} 

		else if(frameCount%(60*6) > (60*4.5f) && frameCount%(60*6) < (60*6)) {
			//movingSlider = (movingSlider*0.95)+(normal*0.05);
			//println("NORMAL END");
		
		}
		
		float slider = map(movingSlider, 0.0f, 1.0f, -blockw*0.5f, blockh*1.4f);



		pg.beginDraw();
		pg.strokeWeight(5);
		pg.fill(0, 50);
		pg.noStroke();
		pg.rect(0, 0, pg.width, pg.height);

		f += 0.005f;
  
		float waveH = 100; //100+sin(frameCount*0.1)*300; // map(width/2, 0, width, 100, 500);
		
		for (int h = 0; h < blockh; h += 10) {
		    
		    if(h < slider) {

		    pg.beginShape(); 
		    //pg.stroke(map(h, frameCount%height, height, 10, 20), map(h, frameCount%height, height, 0, 100), 255);
		    float colorShade = map(h, 0, blockh, 0, 255);
		    float colorShade2 = map(h, 0, blockh, 0, 100);
		    float colorShade3 = map(h, slider-(100*movingSlider), blockh, 0, 255);
		    if(colorShade3<0) {
		    	colorShade3 = 0;
		    }
			pg.stroke(colorShade3, colorShade2+colorShade3, colorShade+colorShade3*2.5f);

		    float x = 0;
		    float y = h + waveH * noise(noiseX, noiseY + h * 0.1f, noiseF);

		    pg.curveVertex(x, y);
		    
		    for (int w = 0; w <= blockw+100; w += 50) {
		      x = w;
		      float n = noise(noiseX + w * 0.001f, noiseY + h * 0.01f, noiseF + f);
		      y = h + waveH * n;

		      pg.curveVertex(x, y);
		    }

		    x = blockw;
		    y = h + waveH * noise(noiseX + blockw, noiseY + h * 0.01f, noiseF + f);

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
  int blockw = 2560;
  int blockh = 1440;

	WindMap(int _width, int _height) {
		//pg = createGraphics(1080, 720);
		pg = createGraphics(_width, _height);

    blockw = _width;
    blockh = _height;

		flowfield = new FlowField(10);
		vehicles = new ArrayList<Vehicle>();

		for (int i = 0; i < 300; i++) {
		  vehicles.add(new Vehicle(new PVector(random(blockw), random(blockh)), random(2, 5), random(0.1f, 1.1f)));
		}
	}

	 public PGraphics draw(float windDirection, float windSpeedN) {

	 	pg.beginDraw();
    pg.background(0);
	 	pg.smooth();
    
		flowfield.display(pg);
		
		for (Vehicle v : vehicles) {
  		v.follow(flowfield);
  		v.run(pg, windSpeedN);
		}

		flowfield.init(windDirection);
    println("new wind dir " + windDirection);
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
    //pg.strokeWeight(5);
    pg.rotate(velocity.heading2D());
    float len = velocity.mag();
    float c = map(maxspeed, 2, 7, 0, 1.0f);
    //float c = map(mouseX, 0.0, width, 0, 1.0);
    
    int c1 = color(100,149,237);
    int c2 = color(0, 255, 0);
    int c3 = color(255, 255, 0);
    int c4 = color(255, 0, 0);

    int sc = color(0.0f); 
    int sc2 = color(0.0f);
    if(c<0.5f) {
      sc = lerpColor(c1, c2, map(c, 0.0f, 0.5f, 0.0f, 1.0f) );
    }
    if(c > 0.5f) {
      sc2 = lerpColor(c3, c4, map(c, 0.5f, 1.0f, 0.0f, 1.0f) );
    }
    
    pg.strokeWeight(2);
    pg.stroke(sc+sc2);
    pg.line(len,0,len-arrowsize*2.0f,0);
    pg.line(len,0,len-arrowsize,+arrowsize/2);
    pg.line(len,0,len-arrowsize,-arrowsize/2);
    pg.popMatrix();
    pg.strokeWeight(1);
  }

  public void borders() {
    if (position.x < -r) position.x = blockw+r;
    if (position.y < -r) position.y = blockh+r;
    if (position.x > blockw+r) position.x = -r;
    if (position.y > blockh+r) position.y = -r;
  }
}



class FlowField {

  PVector[][] field;
  int cols, rows; 
  int resolution; 

  FlowField(int r) {
    resolution = r;
    cols = blockw/resolution;
    rows = blockh/resolution;
    field = new PVector[cols][rows];
    init(0);
  }

  public void init(float windDirection) {
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
  public void settings() {  size(960, 540); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#000000", "--hide-stop", "voidmix" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
