
import com.hamoid.*;

VideoExport videoExport;

ParseJSON parseJSON;

int dayCount = 0;
int daySpeed = 6*60;

boolean exportVideo = false;

ArrayList<String> lines;
ArrayList<Float> colors;

TimeFrame lastTimeFrameSelected = null;
TimeFrame currentTimeFrameHolder = new TimeFrame("", 0.0, 0.0, 0.0, 1, "", 1, 1, 1.0, 1, 1.0, 1, 1.0, 1.0);

   
void setup() {
  
  size(1080, 720);
  // size(2560, 1440);
  
  lines = new ArrayList<String>();
  colors = new ArrayList<Float>();
  
  parseJSON = new ParseJSON(); 
  
  parseJSON.parse("result.json");

  if(exportVideo) {
    videoExport = new VideoExport(this, "options-"+hour()+""+minute()+""+second()+".mp4");
    videoExport.setQuality(70, 128);
    videoExport.setFrameRate(60);
    videoExport.setLoadPixels(true);
    videoExport.setDebugging(false);
    videoExport.startMovie();
  }

}

void draw() {
  
  background(0);
  
  float lineHeight = 15;
  
  if(frameCount%(daySpeed) == 0) {
    dayCount = dayCount%365+1;
  }

  TimeFrame timeFrameSelected = parseJSON.timeFrames.get(dayCount%365);
  
  if(timeFrameSelected != lastTimeFrameSelected) {
    
    println("new targets");
    
    lastTimeFrameSelected = timeFrameSelected; 
  }
  
  currentTimeFrameHolder.moonAge = currentTimeFrameHolder.moonAge * 0.99 + timeFrameSelected.moonAge * 0.01;
  currentTimeFrameHolder.windSpeed = (int)(currentTimeFrameHolder.windSpeed * 0.99 + timeFrameSelected.windSpeed * 0.01);
  
  
  int amount = (int)abs(sin(frameCount*0.1)*4.0);
  
  if(frameCount %1 == 0) {
    
    if(frameCount < 50) {
      lines.add(frameCount+"");
    } else if(frameCount > 50) {
      lines.add(
      timeFrameSelected.date +
      " dayCount:" + dayCount + 
      " Frame:" + frameCount + 
      " moonAge:" + currentTimeFrameHolder.moonAge + 
      " moonPhase:" + timeFrameSelected.moonPhase + 
      " windDir:" + timeFrameSelected.windDirection + 
      " windSpeed:" + currentTimeFrameHolder.windSpeed +
      " tideMin:" + timeFrameSelected.tideMinN +
      " tideMax:" + timeFrameSelected.tideMaxN +
      " cloudCover:" + Math.floor(timeFrameSelected.cloudCoverN)
      );
    }
    
    if(lines.size() > height/lineHeight) {
      lines.remove(0);
    }
    
  }
    
  int index = 0;
  for(String l : lines) {
      
    text(l, 15, index*lineHeight+5);
    index++;
  }
  
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
