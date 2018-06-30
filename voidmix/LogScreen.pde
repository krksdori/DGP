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
  	int fontSize = 20;
    PImage tempScreen;
    float currentOffset = -100;
    float targetOffset = -100;
    HashMap<String, PImage> moons = new HashMap<String, PImage>();
    String[] moonTitles = {  "FirstQuarter", "NewMoon", "WaxingCrescent", "FullMoon", "WaningCrescent", "WaxingGibbous", "LastQuarter", "WaningGibbous" };
    
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
		
		for(String i : moonTitles) {
			moons.put(i, loadImage("moon/"+i+".png") );
			//println("moon/"+i+".png");
		}
	}

	void setType(TimeFrame timeFrameSelected, TimeFrame tfCurrent, int dayCount) {
		for(int i = 0; i < columnsTarget.size(); i++) {
			if(i == 0) {
				columnsTarget.get(i).type[0] = "Moon Phase";
				columnsTarget.get(i).type[1] = timeFrameSelected.moonPhase+"";
				columnsTarget.get(i).type[2] = "";
				columnsTarget.get(i).type[3] = "false";
				
			} else if(i == 1) {
				columnsTarget.get(i).type[0] = "Wind Direction" ;
				columnsTarget.get(i).type[1] = timeFrameSelected.windDirectionTitle+"";
				columnsTarget.get(i).type[2] = timeFrameSelected.windDirectionArraw+"";
				columnsTarget.get(i).type[3] = "false";
				
				
			} else if(i == 2) {
				columnsTarget.get(i).type[0] = "Wind Speed";
				columnsTarget.get(i).type[1] = nf(timeFrameSelected.windSpeed,2, 2)+" m/s";
				columnsTarget.get(i).type[3] = "false";

				if(timeFrameSelected.windSpeedN > 0.5) {
					columnsTarget.get(i).type[2] = nf(dist(0.5, 0.0,timeFrameSelected.windSpeedN, 0.0)*100.0, 2, 2) + " % ↑";
					if(timeFrameSelected.windSpeedN > 0.9) {
						columnsTarget.get(i).type[3] = "true";
					}
				
				} else if(timeFrameSelected.windSpeed < 0.5) {
					columnsTarget.get(i).type[2] = "-" + nf(dist(0.5, 0.0,timeFrameSelected.windSpeedN, 0.0)*100.0, 2, 2) + " % ↓";
				} else {
					columnsTarget.get(i).type[2] = 	"00.00 %";
				}
				
			} else if(i == 3) {
				columnsTarget.get(i).type[0] = "Cloud Cover";
				columnsTarget.get(i).type[1] = nf(timeFrameSelected.cloudCover, 2, 2) +" %";
				columnsTarget.get(i).type[3] = "false";

				
				if(timeFrameSelected.cloudCoverN > 0.5) {
					columnsTarget.get(i).type[2] = nf(dist(0.5, 0.0,timeFrameSelected.cloudCoverN, 0.0)*100.0, 2, 2) + " % ↑";
					if(timeFrameSelected.cloudCoverN > 0.9) {
						columnsTarget.get(i).type[3] = "true";
					}
				
				} else if(timeFrameSelected.cloudCoverN < 0.5) {
					columnsTarget.get(i).type[2] = "-" + nf(dist(0.5, 0.0,timeFrameSelected.cloudCoverN, 0.0)*100.0, 2, 2) + " % ↓";
				} else {
					columnsTarget.get(i).type[2] = 	"00.00 %";
				}
				
			} else if(i == 4) {
				columnsTarget.get(i).type[0] = "Precipitation";
				columnsTarget.get(i).type[1] = nf(timeFrameSelected.precipitation, 2, 2)+" mm";
				columnsTarget.get(i).type[3] = "false";
				
				if(timeFrameSelected.precipitationN > 0.5) {
					columnsTarget.get(i).type[2] = nf(dist(0.5, 0.0,timeFrameSelected.precipitationN, 0.0)*100.0, 2, 2) + " % ↑";
					if(timeFrameSelected.precipitationN > 0.9) {
						columnsTarget.get(i).type[3] = "true";
					}
				
				} else if(timeFrameSelected.precipitationN < 0.5) {
					columnsTarget.get(i).type[2] = "-" + nf(dist(0.5, 0.0,timeFrameSelected.precipitationN, 0.0)*100.0, 2, 2) + " % ↓";
				} else {
					columnsTarget.get(i).type[2] = 	"00.00 %";
				}
				
			} else if(i == 5) {
				columnsTarget.get(i).type[0] = "Temperature";
				columnsTarget.get(i).type[1] = nf(timeFrameSelected.temperature, 2, 2)+" °C";
				columnsTarget.get(i).type[3] = "false";

				
				if(timeFrameSelected.temperatureN > 0.5) {
					columnsTarget.get(i).type[2] = nf(dist(0.5, 0.0,timeFrameSelected.temperatureN, 0.0)*100.0, 2, 2) + " % ↑";
					if(timeFrameSelected.temperatureN > 0.9) {
						columnsTarget.get(i).type[3] = "true";
					}
				
				} else if(timeFrameSelected.temperatureN < 0.5) {
					columnsTarget.get(i).type[2] = "-" + nf(dist(0.5, 0.0,timeFrameSelected.temperatureN, 0.0)*100.0, 2, 2) + " % ↓";
				} else {
					columnsTarget.get(i).type[2] = 	"00.00 %";
				}
				
			} else if(i == 6) {
				columnsTarget.get(i).type[0] = "Low Tide";
				columnsTarget.get(i).type[1] = nf(timeFrameSelected.tideMin, 2, 2)+" cm";
				columnsTarget.get(i).type[3] = "false";
				
				if(timeFrameSelected.tideMinN > 0.5) {
					columnsTarget.get(i).type[2] = nf(dist(0.5, 0.0,timeFrameSelected.tideMinN, 0.0)*100.0, 2, 2) + " % ↑";
					if(timeFrameSelected.tideMinN > 0.9) {
						columnsTarget.get(i).type[3] = "true";
					}
				
				} else if(timeFrameSelected.tideMinN < 0.5) {
					columnsTarget.get(i).type[2] = "-" + nf(dist(0.5, 0.0,timeFrameSelected.tideMinN, 0.0)*100.0, 2, 2) + " % ↓";
				} else {
					columnsTarget.get(i).type[2] = 	"00.00 %";
				}

				
			} else if(i == 7) {
		        columnsTarget.get(i).type[0] = "High Tide";
		        columnsTarget.get(i).type[1] = nf(timeFrameSelected.tideMax, 2, 2)+" cm";
		        columnsTarget.get(i).type[3] = "false";
		        
		        if(timeFrameSelected.tideMaxN > 0.5) {
					columnsTarget.get(i).type[2] = nf(dist(0.5, 0.0,timeFrameSelected.tideMaxN, 0.0)*100.0, 2, 2) + " % ↑";
					if(timeFrameSelected.tideMaxN > 0.9) {
						columnsTarget.get(i).type[3] = "true";
					}
				
				} else if(timeFrameSelected.tideMaxN < 0.5) {
					columnsTarget.get(i).type[2] = "-" + nf(dist(0.5, 0.0,timeFrameSelected.tideMaxN, 0.0)*100.0, 2, 2) + " % ↓";
				} else {
					columnsTarget.get(i).type[2] = 	"00.00 %";
				}

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
    	pg.text("Location     52°04’52.8”N 4°19’09.7”E", pg.width-472, 30);
    	pg.translate(0, pg.height-50);
    	pg.text("Data Source     KNMI", 20, 30);
    	pg.textSize(15);
		pg.text("(KNMI — Royal Netherlands Meteorological Institute)", pg.width-487, 30);
    	pg.textSize(20);
		
    	pg.popMatrix();	

    	
    	pg.pushMatrix();
    	
    	pg.translate(678, 500);		

    	pg.stroke(255);
    	//float offset = Math.abs(sin(ticker*0.01)*400.0);

    	if((ticker-80)%(daySpeed) == 0) {
    		targetOffset = 450.0;
      	}

      	if((ticker-80)%(daySpeed) == 100) {
    		targetOffset = -100.0;
      	}

    	currentOffset = currentOffset*0.95 + targetOffset *0.05;
    	//pg.line(0.0, currentOffset, 1200.0, currentOffset);

    	float distM = ((0) + 20) - currentOffset;
		float mapDistM = map(distM, 100, 0, 0, 255.0);
		if(distM<0) {
			mapDistM = 255.0;
		}

    	pg.tint(255, mapDistM);
    	pg.image(moons.get(timeFrameSelected.moonImageName), 1040.0, 5.0, 20, 20);
    	pg.tint(255, 255);
    	

		int lineHeight = 40;
		int spacingC1 = 520;
		int spacingC2 = 520*2;
		int spacingC3 = 520*3;

		int i = 0;
		for(Column c : columns) {
      		pg.pushMatrix();

      		float dist = ((i*lineHeight) + 20) - currentOffset;
			pg.translate(0, (i*lineHeight) + 20);
			
			float mapDist = map(dist, 100, 0, 0, 110.0);
			if(dist<0) {
				mapDist = 110.0;
			}

			float mapDistSin = abs(sin(ticker*0.25))*110.0;

			pg.fill(mapDist);

			if(mapDist == 110.0) {
				if(c.type[3] == "true") {
					pg.fill(mapDistSin);				
				}
			}

			if(c.type[0].length() > 0) {
				if(str(c.type[0].charAt(0)).equals("-")) {
					pg.text(c.type[0], -12, 0);
				} else {
					pg.text(c.type[0], 0, 0);
				}
			}

			if(c.type[1].length() > 0) {
				if(str(c.type[1].charAt(0)).equals("-")) {
					pg.text(c.type[1], spacingC1-12, 0);
				} else {
					pg.text(c.type[1], spacingC1, 0);
				}
			}

			if(c.type[2].length() > 0) {
				if(str(c.type[2].charAt(0)).equals("-")) {
					pg.text(c.type[2], spacingC2-12, 0);
				} else {
					pg.text(c.type[2], spacingC2, 0);
				}
			}
			
			pg.fill(mapDist);
			//pg.text(c.type[1], spacingC1, 0);
			//pg.text(c.type[2], spacingC2, 0);
			pg.popMatrix();
			i++;
		}

    pg.popMatrix();
		
		pg.endDraw();
		return pg;		
	}


}
