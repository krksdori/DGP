
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
        int windDirection = timeFrame.getInt("windDirection");
        JSONObject windJO = timeFrame.getJSONObject("windSpeed");
        int windSpeedValue = windJO.getInt("value");
        float windSpeedValueN = windJO.getFloat("normalized");

        timeFrames.add(new TimeFrame(date, precipitation, precipitationN, moonAge, moonVisible, moonPhase, windDirection, windSpeedValue, windSpeedValueN));
        
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
   
   TimeFrame(String date, float precipitation, float precipitationN, float moonAge, int moonVisible, String moonPhase, int windDirection, int windSpeed, float windSpeedN) {
     this.date = date;
     this.precipitation = precipitation;
     this.precipitationN = precipitationN;
     this.moonAge = moonAge;
     this.moonVisible = moonVisible;
     this.moonPhase = moonPhase;
     this.windDirection = windDirection;
     this.windSpeed = windSpeed;
     this.windSpeedN =windSpeedN;
   }
   
}
