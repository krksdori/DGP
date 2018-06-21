class ColumHolder {
	String val1 = "";
	String val2 = "";
	String val3 = "";
	String val4 = "";
	
	ColumHolder(String _val1, String _val2, String _val3, String _val4) {
		val1 = _val1;
		val2 = _val2;
		val3 = _val3;
		val4 = _val4;
	}

}

class LogScreen {

	PGraphics pg;
	int blockw = 2560;
  	int blockh = 1440;
  	ArrayList<ColumHolder> lines;
  	int lastDayCount = -1;
  	float lineCounter = 0;
  	int lineCounterTarget = 0;
  	int removed = 0;
  	float lineHeight = 15;

	LogScreen(int _width, int _height) {
      	blockw = _width;
      	blockh = _height;

		pg = createGraphics(_width, _height);
		lines = new ArrayList<ColumHolder>();

		for(int i = 0;i < floor(blockh/lineHeight);i++) {
			lines.add(new ColumHolder("-", "", "", ""));
		}
	}

	PGraphics draw(TimeFrame timeFrameSelected, int dayCount, int ticker) {

		
		if(lastDayCount != dayCount) {

			lines.add(new ColumHolder("-", "-", "-", "-"));
			lines.add(new ColumHolder("day", dayCount+"", "--", "--"));
			lines.add(new ColumHolder("Moon Age", timeFrameSelected.moonAge+"", "--", "--"));
			lines.add(new ColumHolder("moonVisible", ""+timeFrameSelected.moonVisible+"%", "--", "--"));
 			lines.add(new ColumHolder("Moon Phase", ""+timeFrameSelected.moonPhase, "--", "--"));
			lines.add(new ColumHolder("windDirection", ""+timeFrameSelected.windDirection, "--", "--"));
 			lines.add(new ColumHolder("windSpeed", ""+timeFrameSelected.windSpeed, "--", "--"));
 			lines.add(new ColumHolder("windSpeedN", ""+timeFrameSelected.windSpeedN, "--", "--"));
 			lines.add(new ColumHolder("tideMinN", ""+timeFrameSelected.tideMinN, "--", "--"));
 			lines.add(new ColumHolder("tideMaxN", ""+timeFrameSelected.tideMaxN, "--", "--"));
 			lines.add(new ColumHolder("cloudCoverN", ""+timeFrameSelected.cloudCoverN, "--", "--"));
 			lines.add(new ColumHolder("temperature", ""+timeFrameSelected.temperature, "--", "--"));
 			lines.add(new ColumHolder("temperatureN", ""+timeFrameSelected.temperatureN, "--", "--"));

 			//lineCounterTarget += 13;

			lastDayCount = dayCount;
		}

		//lineCounter = lineCounter*0.99 + lineCounterTarget*0.01;

		if(lines.size() > (((blockh)/lineHeight)) ) {
			//if(ticker%5==0) {
				lines.remove(0);
			//}
    	}

		pg.beginDraw();

		//println(lineCounter + " " + lineCounterTarget);

		int index = 0;
		pg.background(0, 0, 0);
		for(ColumHolder l : lines) {
			
			pg.text(l.val1, 15, index*lineHeight+5);
			pg.text(l.val2, 130, index*lineHeight+5);
			pg.text(l.val3, 250, index*lineHeight+5);
			pg.text(l.val4, 320, index*lineHeight+5);

			index++;
		}



		
		pg.endDraw();
		return pg;		
	}


}