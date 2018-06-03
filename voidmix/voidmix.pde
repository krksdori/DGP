import com.hamoid.*;

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

void setup() {
  
  size(1080, 720);
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

void draw() {
 
  background(200);
  
  if(frameCount%(daySpeed) == 0) {
    dayCount = dayCount%365+1;
  }

  TimeFrame timeFrameSelected = parseJSON.timeFrames.get(dayCount%365);
  
  image(rainDrops.draw(timeFrameSelected.precipitationN), 0.0, 0.0);
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

void dayNightFade() {
 
 int fadeTime = (int) (daySpeed/3.2); // 7.2
  
 float BGColor = map(frameCount%daySpeed, 0, fadeTime, 255, 0);
 noStroke();
 fill(0, BGColor);
 rect(0, 0, width, height);
 BGColor = map(frameCount%daySpeed, (daySpeed)-fadeTime, (daySpeed), 0, 255);
 fill(0, BGColor);
 rect(0, 0, width, height); 
  
}

void keyPressed() {
  if (key == 'q') {
    if(exportVideo) {
      videoExport.endMovie();
      exit();
    }
  }
}
