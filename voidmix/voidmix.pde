import com.hamoid.*;

VideoExport videoExport;

ParseJSON parseJSON;

MoonPhases moonPhases;
RainDrops rainDrops;
SunRise sunRise;
WindMap windMap;
PerlinCloud perlinCloud;
TideLines tideLines;
Temperature temperature;

int dayCount = 0;
int daySpeed = 60*10; // 60*6
int cols = 3;
int rows = 3;

PGraphics main;

ArrayList<Frame> frames = new ArrayList();

boolean exportVideo = true;

void setup() {
  
  size(1080, 720);
  //size(2560, 1440);
//  size(7680, 4320);


  main = createGraphics(width, height);
  
  parseJSON = new ParseJSON(); 
  
  moonPhases = new MoonPhases(width, height);
  rainDrops = new RainDrops(width, height);
  sunRise = new SunRise(width, height);
  windMap = new WindMap(width, height);
  perlinCloud = new PerlinCloud(width, height);
  tideLines = new TideLines(width, height);
  temperature = new Temperature(width, height);
  
  parseJSON.parse("result.json");

  initVideo();

}

void initVideo() {
  if(exportVideo) {
      videoExport = new VideoExport(this, "export/options-day-" + dayCount + ".mp4");
      videoExport.setQuality(70, 128);
      videoExport.setFrameRate(60);
      videoExport.setLoadPixels(true);
      videoExport.setDebugging(false);
      videoExport.startMovie();
  }
}

void draw() {
 
  background(200);
  if(frameCount%(daySpeed) == 0) {
    dayCount = dayCount%365+1;
    //initVideo();
  }

 
  TimeFrame timeFrameSelected = parseJSON.timeFrames.get(dayCount%365);
  main.beginDraw();
  //main.background(0);
  main.image(rainDrops.draw(timeFrameSelected.precipitationN, timeFrameSelected.windDirection, timeFrameSelected.windSpeedN), 0.0, 0.0);
  main.blend(windMap.draw(timeFrameSelected.windDirection, timeFrameSelected.windSpeedN), 0, 0, width, height, 0, 0, width, height, SCREEN);
  main.blend(perlinCloud.draw(timeFrameSelected.cloudCoverN), 0, 0, width, height, 0, 0, width, height, SCREEN);
  main.blend(moonPhases.draw(timeFrameSelected.moonAge), 0, 0, width, height, 0, 0, width, height, SCREEN);
  main.blend(tideLines.draw(timeFrameSelected.tideMinN, timeFrameSelected.tideMaxN), 0, 0, width, height, 0, 0, width, height, SCREEN);  
  main.blend(sunRise.draw(timeFrameSelected.cloudCoverN), 0, 0, width, height, 0, 0, width, height, SCREEN);
  main.blend(temperature.draw(timeFrameSelected.temperatureN), 0, 0, width, height, 0, 0, width, height, SCREEN);
  
  dayNightFade(main);
  main.endDraw();

  int times = 40;
  if(frames.size() >= 8*times) {
    frames.remove(0);
  }
  frames.add(new Frame(main.get()));

  int x = 0;
  int y = 0;

 
  int index = 0;

  for(Frame f : frames) {
    
    if(index == 7*times) {
        x = (width/3)*1;
        y = 0;
         image(f.img, x, y, width/cols, height/rows);
    } else if(index == 6*times) {
        x = (width/3)*2;
        y = 0;
         image(f.img, x, y, width/cols, height/rows);
    } else if(index == 5*times) {
        x = (width/3)*2;
        y = (height/3);
         image(f.img, x, y, width/cols, height/rows);
    } else if(index == 4*times) {
        x = (width/3)*2;
        y = (height/3)*2;
         image(f.img, x, y, width/cols, height/rows);
    } else if(index == 3*times) {
        x = (width/3)*1;
        y = (height/3)*2;
         image(f.img, x, y, width/cols, height/rows);
    } else if(index == 2*times) {
        x = (width/3)*0;
        y = (height/3)*2;
         image(f.img, x, y, width/cols, height/rows);
    } else if(index == 1*times) {
        x = (width/3)*0;
        y = (height/3)*1;
         image(f.img, x, y, width/cols, height/rows);
    } else if(index == 0*times) {
        x = (width/3)*0;
        y = (height/3)*0;
         image(f.img, x, y, width/cols, height/rows);
    } 
    index++;
  }

 // image(main, 0, 0);
  
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
 text("temperature: " + timeFrameSelected.temperature, 50, 290);
 text("temperatureN: " + timeFrameSelected.temperatureN, 50, 310);
 

  
  if(exportVideo) {
    videoExport.saveFrame();
  }
  
  if(frameCount%(daySpeed) == daySpeed-1) {
    if(exportVideo) {
      //videoExport.endMovie();
    }
  }


}

void dayNightFade(PGraphics main) {
 int fadeTime = (int) (daySpeed/4.2); // 7.2
 float BGColor = map(frameCount%daySpeed, 0, fadeTime, 255, 0);
 main.noStroke();
 main.fill(0, BGColor);
 main.rect(0, 0, width, height);
 BGColor = map(frameCount%daySpeed, (60*6)-fadeTime, (60*6), 0, 255);
 main.fill(0, BGColor);
 main.rect(0, 0, width, height); 
}

void keyPressed() {
  if (key == 'q') {
    if(exportVideo) {
      videoExport.endMovie();
      exit();
    }
  }
}

class Frame {
  
    PImage img = new PImage(width/3, height/3);
    
    Frame(PImage img) {
     this.img = img; 
    }
}
