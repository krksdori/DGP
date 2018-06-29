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

public class logscreenviewer extends PApplet {



VideoExport videoExport;

ParseJSON parseJSON;
LogScreen logScreen;
SmoothJson smoothJson;

PGraphics master;
boolean activated = true;
int ticker = -1;

boolean exportVideo = true;
boolean firstAction = true;

int dayCount = 0;
int dayCountMagic = 0;
int daySpeed = 60*4; // 60*4

public void setup() {
    
   
   parseJSON = new ParseJSON();
   
   parseJSON.parse("result.json");
   smoothJson = new SmoothJson(parseJSON.timeFrames.get(0));
   
   master = createGraphics(2560, 1440);
   logScreen = new LogScreen(2560, 1440);
   
   initVideo();
}

public void initVideo() {
  if(exportVideo) {
      videoExport = new VideoExport(this, "export/options-day-" + dayCount + ".mp4", master);
      videoExport.setQuality(70, 128);
      videoExport.setFrameRate(30);
      videoExport.setLoadPixels(true);
      videoExport.setDebugging(false);
      videoExport.startMovie();
  }
}


public void draw() {
  
  TimeFrame timeFrameSelected = parseJSON.timeFrames.get(dayCount%365);
  TimeFrame timeFrameSelectedMagic = parseJSON.timeFrames.get(dayCountMagic%365);

   if(ticker%(daySpeed) == 0) {
    if(firstAction == false) {
      dayCount = (dayCount%365)+1;
    } else {
      firstAction = false;
    }
    //initVideo();
  }
  if(dayCount > 0) {
   if((ticker-80)%(daySpeed) == 0) {
      if(firstAction == false) {
        dayCountMagic = (dayCountMagic%365)+1;
      }
    }
  }
  
  smoothJson.newTarget(parseJSON.timeFrames.get((dayCountMagic)%365));
  smoothJson.update();
  TimeFrame timeFrameSmooth = smoothJson.tfCurrent;

  master.beginDraw();
  master.image(logScreen.draw(timeFrameSelectedMagic, timeFrameSmooth, dayCount, ticker), 0.0f, 0.0f);
  master.endDraw();
  
  image(master, 0, 0, width, height);
  
  if(activated == true) {
    videoExport.saveFrame();
    ticker++;
  }
}

public void keyPressed() {
  if (key == 'q') {
    if(exportVideo) {
      videoExport.endMovie();
      exit();
    }
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
    	pg.text("Location     52°04’52.8”N 4°19’09.7”E", width-472, 30);
    	pg.translate(0, height-50);
    	pg.text("Data Source     KNMI", 20, 30);
    	pg.textSize(15);
		pg.text("(KNMI — Royal Netherlands Meteorological Institute)", width-487, 30);
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
  public void settings() {  size(2560, 1440); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#000000", "--hide-stop", "logscreenviewer" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
