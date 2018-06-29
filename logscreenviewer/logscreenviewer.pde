import com.hamoid.*;

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

void setup() {
   size(2560, 1440); 
   
   parseJSON = new ParseJSON();
   
   parseJSON.parse("result.json");
   smoothJson = new SmoothJson(parseJSON.timeFrames.get(0));
   
   master = createGraphics(2560, 1440);
   logScreen = new LogScreen(2560, 1440);
   
   initVideo();
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
  master.image(logScreen.draw(timeFrameSelectedMagic, timeFrameSmooth, dayCount, ticker), 0.0, 0.0);
  master.endDraw();
  
  image(master, 0, 0, width, height);
  
  if(activated == true) {
    videoExport.saveFrame();
    ticker++;
  }
}

void keyPressed() {
  if (key == 'q') {
    if(exportVideo) {
      videoExport.endMovie();
      exit();
    }
  }
}
