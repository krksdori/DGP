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
	}

	void setType(TimeFrame timeFrameSelected, TimeFrame tfCurrent, int dayCount) {
		for(int i = 0; i < columnsTarget.size(); i++) {
			if(i == 0) {
				columnsTarget.get(i).type[0] = "Moon Phase";
				columnsTarget.get(i).type[1] = timeFrameSelected.moonPhase+"";
				columnsTarget.get(i).type[2] = "";
				
			} else if(i == 1) {
				columnsTarget.get(i).type[0] = "Wind Direction" ;
				columnsTarget.get(i).type[1] = timeFrameSelected.windDirectionTitle+"";
				columnsTarget.get(i).type[2] = "..";
				
			} else if(i == 2) {
				columnsTarget.get(i).type[0] = "Wind Speed";
				columnsTarget.get(i).type[1] = timeFrameSelected.windSpeed+" m/s";
				columnsTarget.get(i).type[2] = "..";
				
			} else if(i == 3) {
				columnsTarget.get(i).type[0] = "Cloud Cover";
				columnsTarget.get(i).type[1] = timeFrameSelected.cloudCover+" %";
				columnsTarget.get(i).type[2] = "..";
				
			} else if(i == 4) {
				columnsTarget.get(i).type[0] = "Precipitation";
				columnsTarget.get(i).type[1] = timeFrameSelected.precipitation+" mm";
				columnsTarget.get(i).type[2] = "";
				
			} else if(i == 5) {
				columnsTarget.get(i).type[0] = "Temperature";
				columnsTarget.get(i).type[1] = timeFrameSelected.temperature+" °C";
				columnsTarget.get(i).type[2] = "";
				
			} else if(i == 6) {
				columnsTarget.get(i).type[0] = "Low Tide";
				columnsTarget.get(i).type[1] = timeFrameSelected.tideMin+" cm";
				columnsTarget.get(i).type[2] = "";
				
			} else if(i == 7) {
		        columnsTarget.get(i).type[0] = "High Tide";
		        columnsTarget.get(i).type[1] = timeFrameSelected.tideMax+" cm";
		        columnsTarget.get(i).type[2] = "";
      		}
 
		}
		columns = columnsTarget;
	}

	void updateType() {

	}

	String showType(String in, float slider) {
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

	PGraphics draw(TimeFrame timeFrameSelected, TimeFrame tfCurrent, int dayCount, int ticker) {

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

		int lineHeight = 40;
		int spacingC1 = 520;
		int spacingC2 = 520*2;
		int spacingC3 = 520*3;

		int i = 0;
		for(Column c : columns) {
      pg.fill(255, 0, 0);
			pg.pushMatrix();
			pg.translate(0, (i*lineHeight) + 20);
			pg.text(c.type[0], 0, 0);
			pg.text(c.type[1], spacingC1, 0);
			pg.text(c.type[2], spacingC2, 0);
			pg.popMatrix();
			i++;
		}

    pg.popMatrix();
		
		pg.endDraw();
		return pg;		
	}


}
