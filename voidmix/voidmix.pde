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
int daySpeed = 60*4; // 60*6
int cols = 3;
int rows = 3;
int blockw = 640;
int blockh = 480;
int masterw = blockw*3;
int masterh = blockh*3;
boolean activated = false;
boolean firstAction = true;
int ticker = -1;

PGraphics main;
PGraphics master;

ArrayList<Frame> frames = new ArrayList();

boolean exportVideo = true;

void setup() {
  
  size(1080, 720);
  //size(512, 288);
  //size(2560, 1440);
  //size(7680, 4320);


  main = createGraphics(blockw,blockh);
  master = createGraphics(masterw, masterh);
  
  parseJSON = new ParseJSON(); 
  
  moonPhases = new MoonPhases(blockw, blockh);
  rainDrops = new RainDrops(blockw, blockh);
  sunRise = new SunRise(blockw, blockh);
  windMap = new WindMap(blockw, blockh);
  perlinCloud = new PerlinCloud(blockw, blockh);
  tideLines = new TideLines(blockw, blockh);
  temperature = new Temperature(blockw, blockh);
  
  parseJSON.parse("result.json");

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

void initVideo() {
  if(exportVideo) {
      videoExport = new VideoExport(this, "export/options-day-" + dayCount + ".mp4", master);
      videoExport.setQuality(70, 128);
      videoExport.setFrameRate(30);
      videoExport.setLoadPixels(true);
      videoExport.setDebugging(false);
      videoExport.startMovie();
  }
}

void draw() {
 
  background(200);
  
  
  
  if(ticker%(daySpeed) == 0) {
    if(firstAction == false) {
      dayCount = (dayCount%365)+1;
    } else {
      firstAction = false;
    }
    initVideo();
    
  }

 
  TimeFrame timeFrameSelected = parseJSON.timeFrames.get(dayCount%365);
  main.beginDraw();
  //main.background(0);
  main.image(rainDrops.draw(timeFrameSelected.precipitationN, timeFrameSelected.windDirection, timeFrameSelected.windSpeedN), 0.0, 0.0, blockw, blockh);
  main.blend(windMap.draw(timeFrameSelected.windDirection, timeFrameSelected.windSpeedN), 0, 0, blockw, blockh, 0, 0, blockw, blockh, SCREEN);
  main.blend(perlinCloud.draw(timeFrameSelected.cloudCoverN), 0, 0, blockw, blockh, 0, 0, blockw, blockh, SCREEN);
  //main.blend(moonPhases.draw(timeFrameSelected.moonAge), 0, 0, blockw, blockh, 0, 0, blockw, blockh, SCREEN);
  main.blend(tideLines.draw(timeFrameSelected.tideMinN, timeFrameSelected.tideMaxN), 0, 0, blockw, blockh, 0, 0, blockw, blockh, SCREEN);  
  //main.blend(sunRise.draw(timeFrameSelected.cloudCoverN), 0, 0, blockw, blockh, 0, 0, blockw, blockh, SCREEN);
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
    
    if(index == 7*times) {
        x = (masterw/3)*1;
        y = 0;
        master.image(f.img, x, y, masterw/cols, masterh/rows);
    } else if(index == 6*times) {
        x = (masterw/3)*2;
        y = 0;
        master.image(f.img, x, y, masterw/cols, masterh/rows);
    } else if(index == 5*times) {
        x = (masterw/3)*2;
        y = (masterh/3);
        master.image(f.img, x, y, masterw/cols, masterh/rows);
    } else if(index == 4*times) {
        x = (masterw/3)*2;
        y = (masterh/3)*2;
        master.image(f.img, x, y, masterw/cols, masterh/rows);
    } else if(index == 3*times) {
        x = (masterw/3)*1;
        y = (masterh/3)*2;
        master.image(f.img, x, y, masterw/cols, masterh/rows);
    } else if(index == 2*times) {
        x = (masterw/3)*0;
        y = (masterh/3)*2;
        master.image(f.img, x, y, masterw/cols, masterh/rows);
    } else if(index == 1*times) {
        x = (masterw/3)*0;
        y = (masterh/3)*1;
        master.image(f.img, x, y, masterw/cols, masterh/rows);
    } else if(index == 0*times) {
        x = (masterw/3)*0;
        y = (masterh/3)*0;
        master.image(f.img, x, y, masterw/cols, masterh/rows);
    } 
    index++;
  }
  
    
 // image(main, 0, 0);
  
 master.blend(sunRise.draw(timeFrameSelected.cloudCoverN, ticker), 0, 0, blockw, blockh, 0, 0, master.width, master.height, SCREEN);
  
 master.endDraw();
 image(master, 0, 0, width, height);
 
 fill(255);
 text("day " + dayCount + " ,date " + timeFrameSelected.date, 50, 50);
 text("ticker: " + ticker, 50, 70);
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
   }
 }
  
 if(ticker%(daySpeed) == daySpeed-1) {
    if(exportVideo) {
     videoExport.endMovie();
    }
 }
 
 if(activated == true) {
    ticker++;
  }
 
}

void dayNightFade(PGraphics main, int counter) {
 int fadeTime = (int) (daySpeed/2.2); // 7.2
 float BGColor = map(counter%daySpeed, 0, fadeTime, 255, 0);
 main.noStroke();
 main.fill(0, BGColor);
 main.rect(0, 0, blockw, blockh);
 println(counter%daySpeed);
 BGColor = map(counter%daySpeed, (60*2.5)-fadeTime, (60*2.5), 0, 255);
 main.fill(0, BGColor);
 main.rect(0, 0, blockw, blockh); 
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
  
    PImage img = new PImage(blockw, blockh);
    
    Frame(PImage img) {
     this.img = img; 
    }
}
