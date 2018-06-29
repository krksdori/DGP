class TimeFrame {
  
   String date;
  
   float precipitation;
   float precipitationN;

   float moonAge;
   int moonVisible;
   String moonPhase;

   float windDirection;
   String windDirectionTitle;
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
   
   TimeFrame(String date, float precipitation, float precipitationN, float moonAge, int moonVisible, String moonPhase, float windDirection, String windDirectionTitle, float windSpeed, float windSpeedN, int tideMin, float tideMinN, int tideMax, float tideMaxN, float cloudCover, float cloudCoverN, float temperature, float temperatureN, float mosh) {
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
     this.cloudCover = cloudCover;
     this.cloudCoverN = cloudCoverN;
     this.windDirectionTitle = windDirectionTitle;
     this.temperature = temperature;
     this.temperatureN = temperatureN;
     this.mosh = mosh;
   }
   
}
class ParseJSON {
 
  ArrayList<TimeFrame> timeFrames;
  JSONArray values;
  
  void parse(String file) {
    
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
        float windDirection = timeFrame.getInt("windDirection")*1.0;
        JSONObject windJO = timeFrame.getJSONObject("windSpeed");
        float windSpeedValue = float( nf(windJO.getInt("value")*0.1, 1, 2));
        float windSpeedValueN = windJO.getFloat("normalized");
        //360=north, 90=east, 180=south, 270=west, 0=calm/variable //45
        String[] windTitleOptions = { "North", "Northeast", "East", "Southeast", "South", "Southwest", "West", "Northwest" };
        String windDirectionTitle = windTitleOptions[0];


        if(windDirection > 360-(45/2) && windDirection < 45/2) {
            windDirectionTitle = windTitleOptions[0];
        }
        if(windDirection > (45/2) && windDirection < (45/2) + 45) {
            windDirectionTitle = windTitleOptions[1];
        }
        if(windDirection > (45/2) + 45 && windDirection < (45/2) + 45 * 2) {
            windDirectionTitle = windTitleOptions[2];
        }
        if(windDirection > (45/2) + 45 * 2 && windDirection < (45/2) + 45 * 3) {
            windDirectionTitle = windTitleOptions[3];
        }
        if(windDirection > (45/2) + 45 * 3 && windDirection < (45/2) + 45 * 4) {
            windDirectionTitle = windTitleOptions[4];
        }
        if(windDirection > (45/2) + 45 * 4 && windDirection < (45/2) + 45 * 5) {
            windDirectionTitle = windTitleOptions[5];
        }
        if(windDirection > (45/2) + 45 * 5 && windDirection < (45/2) + 45 * 6) {
            windDirectionTitle = windTitleOptions[6];
        }
        if(windDirection > (45/2) + 45 * 6 && windDirection < (45/2) + 45 * 7) {
            windDirectionTitle = windTitleOptions[7];
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
        float cloudCover = float(nf(map(cloudCoverJO.getFloat("value"), 0, 8, 0, 100), 1, 2));
        float cloudCoverN = cloudCoverJO.getFloat("normalized");
        
        // TEMPERATURE
        JSONObject temperatureJO = timeFrame.getJSONObject("temperature");
        float temperature = float(nf(temperatureJO.getFloat("value")*0.1, 1, 2));
        float temperatureN = temperatureJO.getFloat("normalized");
        
        // MOSH
        float mosh = timeFrame.getFloat("mosh");

        timeFrames.add(new TimeFrame(date, precipitation, precipitationN, moonAge, moonVisible, moonPhase, windDirection, windDirectionTitle, windSpeedValue, windSpeedValueN, tideMin, tideMinN, tideMax, tideMaxN, cloudCover, cloudCoverN, temperature, temperatureN, mosh));
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

  void newTarget(TimeFrame target) {
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

  void update() {
      tfCurrent.mosh = tfCurrent.mosh*0.9 + tfTarget.mosh*0.1;
      
      //tfCurrent.windSpeedN = tfCurrent.windSpeedN*0.9 + tfTarget.windSpeedN*0.1;
      //tfCurrent.windDirection = tfCurrent.windDirection*0.99 + tfTarget.windDirection*0.01;
      tfCurrent.temperatureN = tfCurrent.temperatureN*0.99 + tfTarget.temperatureN*0.01;
      tfCurrent.moonAge = tfCurrent.moonAge*0.99 + tfTarget.moonAge*0.01;
  }

}
