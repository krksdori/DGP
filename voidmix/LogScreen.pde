class Column {
	String[] type = new String[4];
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
  	int fontSize = 10;
  PImage tempScreen;
  	
	LogScreen(int _width, int _height) {
      	blockw = _width;
      	blockh = _height;

      	font = createFont("data/font/SourceCodePro-Regular.ttf", fontSize);
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
				columnsTarget.get(i).type[0] = "moon age";
				columnsTarget.get(i).type[1] = timeFrameSelected.moonAge+"";
				columnsTarget.get(i).type[2] = timeFrameSelected.date+"";
				columnsTarget.get(i).type[3] = dayCount + "";
			} else if(i == 1) {
				columnsTarget.get(i).type[0] = "moon visible" ;
				columnsTarget.get(i).type[1] = timeFrameSelected.moonVisible+"";
				columnsTarget.get(i).type[2] = "..";
				columnsTarget.get(i).type[3] = "..";
			} else if(i == 2) {
				columnsTarget.get(i).type[0] = "moon phase";
				columnsTarget.get(i).type[1] = timeFrameSelected.moonPhase+"";
				columnsTarget.get(i).type[2] = "..";
				columnsTarget.get(i).type[3] = "..";
			} else if(i == 3) {
				columnsTarget.get(i).type[0] = "wind direction";
				columnsTarget.get(i).type[1] = timeFrameSelected.windDirection+"";
				columnsTarget.get(i).type[2] = "..";
				columnsTarget.get(i).type[3] = tfCurrent.windDirection+"";
			} else if(i == 4) {
				columnsTarget.get(i).type[0] = "wind speed";
				columnsTarget.get(i).type[1] = timeFrameSelected.windSpeed+"";
				columnsTarget.get(i).type[2] = timeFrameSelected.windSpeedN+"";
				columnsTarget.get(i).type[3] = tfCurrent.windSpeedN+"";
			} else if(i == 5) {
				columnsTarget.get(i).type[0] = "temperature";
				columnsTarget.get(i).type[1] = timeFrameSelected.temperature+"";
				columnsTarget.get(i).type[2] = timeFrameSelected.temperatureN+"";
				columnsTarget.get(i).type[3] = tfCurrent.temperatureN+"";
			} else if(i == 6) {
				columnsTarget.get(i).type[0] = "cloud cover";
				columnsTarget.get(i).type[1] = timeFrameSelected.cloudCover+"";
				columnsTarget.get(i).type[2] = timeFrameSelected.cloudCoverN+"";
				columnsTarget.get(i).type[3] = "..";
			} else if(i == 7) {
        columnsTarget.get(i).type[0] = "mosh";
        columnsTarget.get(i).type[1] = timeFrameSelected.mosh+"";
        columnsTarget.get(i).type[2] = tfCurrent.mosh+"";
        columnsTarget.get(i).type[3] = "..";
      }
 
		}
		columns = columnsTarget;
	}

	void updateType() {

	}

	String showType(String in, float slider) {
		String text = "";
		println(slider);
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
		pg.background(100);
    
    pg.image(tempScreen, 0.0, 0.0);

		pg.textFont(font);

    pg.noStroke();
    pg.fill(map(tfCurrent.mosh, 0.0, 1.0, 0.0, 255.0));
    pg.rect(0, pg.height-10, 10, 10);
		
		int lineHeight = 10;
		int spacingC1 = floor((blockw/4)*1);
		int spacingC2 = floor((blockw/4)*2);
		int spacingC3 = floor((blockw/4)*3);

		int i = 0;
		for(Column c : columns) {
      fill(0);
			pg.pushMatrix();
			pg.translate(0, (i*lineHeight) + 20);
			pg.text(c.type[0], 0, 0);
			pg.text(c.type[1], spacingC1, 0);
			pg.text(c.type[2], spacingC2, 0);
			pg.text(c.type[3], spacingC3, 0);
			pg.popMatrix();
			i++;
		}
		
		pg.endDraw();
		return pg;		
	}


}
