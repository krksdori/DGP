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
  	int fontSize = 60;
  	
	LogScreen(int _width, int _height) {
      	blockw = _width;
      	blockh = _height;

      	font = createFont("data/font/SourceCodePro-Regular.ttf", fontSize);
		pg = createGraphics(_width, _height);

		for(int i = 0; i < 3; i++) {
			columns.add(new Column());
			columnsTarget.add(new Column());
		}
	}

	void setType(TimeFrame timeFrameSelected) {
		for(int i = 0; i < columnsTarget.size(); i++) {
			if(i == 0) {
				columnsTarget.get(i).type[0] = "moon age";
				columnsTarget.get(i).type[1] = showType(timeFrameSelected.moonAge+"", abs(sin(frameCount*0.01)) );
				columnsTarget.get(i).type[2] = "..";
			} else if(i == 1) {
				columnsTarget.get(i).type[0] = "moon visible" ;
				columnsTarget.get(i).type[1] = showType(timeFrameSelected.moonVisible+"", abs(sin(((2*PI)*0.1)+frameCount*0.01)) );
				columnsTarget.get(i).type[2] = "..";
			} else if(i == 2) {
				columnsTarget.get(i).type[0] = "moon phase";
				columnsTarget.get(i).type[1] = showType(timeFrameSelected.moonPhase+"", abs(sin(((2*PI)*0.2)+frameCount*0.01)) );
				columnsTarget.get(i).type[2] = "..";
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

	PGraphics draw(TimeFrame timeFrameSelected, int dayCount, int ticker) {

		setType(timeFrameSelected);
		updateType();

		pg.beginDraw();
		pg.background(0);
		pg.textFont(font);
		
		int lineHeight = 70;
		int spacingC1 = floor((blockw/3)*1);
		int spacingC2 = floor((blockw/3)*2);

		int i = 0;
		for(Column c : columns) {
			pg.pushMatrix();
			pg.translate(0, (i*lineHeight) + 200);
			pg.text(c.type[0], 0, 0);
			pg.text(c.type[1], spacingC1, 0);
			pg.text(c.type[2], spacingC2, 0);
			pg.popMatrix();
			i++;
		}
		
		pg.endDraw();
		return pg;		
	}


}














// pg.pushMatrix();
		// pg.translate(50, blockh-50);
		// pg.text("LOCATION", 0, 0);
		// pg.translate(spacingC1, 0);
		// pg.text("52°04’52.8”N 4°19’09.7”E", 0,0); // data
		// pg.translate(-spacingC1, 0);
 	// 	pg.popMatrix();

		
		// pg.translate(50, lineHeight);
		// pg.text("DATE " + timeFrameSelected.date, 0, 0);
		
 	// 	pg.translate(0, 150+lineHeight);
		// pg.text("ticker", 0, 0);
		// pg.translate(spacingC1, 0);
		// pg.text(ticker+"", 0,0); // data
		// pg.translate(-spacingC1, 0);
 	// 	pg.translate(spacingC2, 0);
 	// 	pg.text("+10%", 0, 0);
 	// 	pg.translate(-spacingC2, 0);

 	// 	pg.translate(0, lineHeight);
		// pg.text("dayspeed", 0, 0);
		// pg.translate(spacingC1, 0);
		// pg.text(daySpeed+"", 0,0);  // data
		// pg.translate(-spacingC1, 0);
		// pg.translate(spacingC2, 0);
		// pg.text("+10%", 0, 0);
		// pg.translate(-spacingC2, 0);

		// pg.translate(0, lineHeight);
		// pg.text("moonAge", 0, 0);
		// pg.translate(spacingC1, 0);
		// pg.text(timeFrameSelected.moonAge+"", 0,0);  // data
		// pg.translate(-spacingC1, 0);
		// pg.translate(spacingC2, 0);
		// pg.text("+10%", 0, 0);
		// pg.translate(-spacingC2, 0);

		// pg.translate(0, lineHeight);
		// pg.text("moonVisible", 0, 0);
		// pg.translate(spacingC1, 0);
		// pg.text(timeFrameSelected.moonVisible+"%", 0,0);  // data
		// pg.translate(-spacingC1, 0);
		// pg.translate(spacingC2, 0);
		// pg.text("+10%", 0, 0);
		// pg.translate(-spacingC2, 0);

		// pg.translate(0, lineHeight);
		// pg.text("moonPhase", 0, 0);
		// pg.translate(spacingC1, 0);
		// pg.text(timeFrameSelected.moonPhase+"", 0,0);  // data
		// pg.translate(-spacingC1, 0);
		// pg.translate(spacingC2, 0);
		// pg.text("+10%", 0, 0);
		// pg.translate(-spacingC2, 0);

		// pg.translate(0, lineHeight);
		// pg.text("windDirection", 0, 0);
		// pg.translate(spacingC1, 0);
		// pg.text(timeFrameSelected.windDirection+"", 0,0);  // data
		// pg.translate(-spacingC1, 0);
		// pg.translate(spacingC2, 0);
		// pg.text("+10%", 0, 0);
		// pg.translate(-spacingC2, 0);

		// pg.translate(0, lineHeight);
		// pg.text("windSpeed", 0, 0);
		// pg.translate(spacingC1, 0);
		// pg.text(timeFrameSelected.windSpeed+"", 0,0);  // data
		// pg.translate(-spacingC1, 0);
		// pg.translate(spacingC2, 0);
		// pg.text("+10%", 0, 0);
		// pg.translate(-spacingC2, 0);

		// pg.translate(0, lineHeight);
		// pg.text("windSpeedN", 0, 0);
		// pg.translate(spacingC1, 0);
		// pg.text(timeFrameSelected.windSpeedN+"", 0,0);  // data
		// pg.translate(-spacingC1, 0);
		// pg.translate(spacingC2, 0);
		// pg.text("+10%", 0, 0);
		// pg.translate(-spacingC2, 0);

		// pg.translate(0, lineHeight);
		// pg.text("tideMinN", 0, 0);
		// pg.translate(spacingC1, 0);
		// pg.text(timeFrameSelected.tideMinN+"", 0,0);  // data
		// pg.translate(-spacingC1, 0);
		// pg.translate(spacingC2, 0);
		// pg.text("+10%", 0, 0);
		// pg.translate(-spacingC2, 0);

		// pg.translate(0, lineHeight);
		// pg.text("tideMaxN", 0, 0);
		// pg.translate(spacingC1, 0);
		// pg.text(timeFrameSelected.tideMaxN+"", 0,0);  // data
		// pg.translate(-spacingC1, 0);
		// pg.translate(spacingC2, 0);
		// pg.text("+10%", 0, 0);
		// pg.translate(-spacingC2, 0);

		// pg.translate(0, lineHeight);
		// pg.text("cloudCoverN", 0, 0);
		// pg.translate(spacingC1, 0);
		// pg.text(timeFrameSelected.cloudCoverN+"", 0,0);  // data
		// pg.translate(-spacingC1, 0);
		// pg.translate(spacingC2, 0);
		// pg.text("+10%", 0, 0);
		// pg.translate(-spacingC2, 0);

		// pg.translate(0, lineHeight);
		// pg.text("temperature", 0, 0);
		// pg.translate(spacingC1, 0);
		// pg.text(timeFrameSelected.temperature+"", 0,0);  // data
		// pg.translate(-spacingC1, 0);
		// pg.translate(spacingC2, 0);
		// pg.text("+10%", 0, 0);
		// pg.translate(-spacingC2, 0);

		// pg.translate(0, lineHeight);
		// pg.text("temperatureN", 0, 0);  // data
		// pg.translate(spacingC1, 0);
		// pg.text(timeFrameSelected.temperatureN+"", 0,0);
		// pg.translate(-spacingC1, 0);
		// pg.translate(spacingC2, 0);
		// pg.text("+10%", 0, 0);
		// pg.translate(-spacingC2, 0);