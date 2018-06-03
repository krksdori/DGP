
class TideLines {
	float noiseX;
	float noiseY;
	float noiseF;
	float f = 0.0;
	float colorState;

	float movingSlider = 0.5;

	PGraphics pg;

	TideLines(int width, int height) {
		pg = createGraphics(width, height);

		pg.beginDraw();
		pg.background(0);
		pg.endDraw();

		noiseX = random(100);
		noiseY = random(100);
		noiseF = random(100);
	}

	PGraphics draw(float min, float max) {
		
		//float min = 0.1;
		float normal = 0.5;
		// max = 1.0;

		
		if(frameCount%(60*6) > 0 && frameCount%(60*6) < (60*1.5)) {
			movingSlider = (movingSlider*0.95)+(normal*0.05);
			//println("NORMAL START");
		} else 

		if(frameCount%(60*6) > (60*1.5) && frameCount%(60*6) < (60*3)) {
			movingSlider = (movingSlider*0.99)+(min*0.01);
			println("MIN");
		
		} else if(frameCount%(60*6) > (60*3) && frameCount%(60*6) < (60*4.5)) {
			movingSlider = (movingSlider*0.99)+(max*0.01);
			println("MAX");		
		} 

		else if(frameCount%(60*6) > (60*4.5) && frameCount%(60*6) < (60*6)) {
			//movingSlider = (movingSlider*0.95)+(normal*0.05);
			//println("NORMAL END");
		
		}
		
		float slider = map(movingSlider, 0.0, 1.0, -height*0.5, height*1.4);



		pg.beginDraw();
		pg.strokeWeight(5);
		pg.fill(0, 50);
		pg.noStroke();
		pg.rect(0, 0, pg.width, pg.height);

		f += 0.005;
  
		float waveH = 100; //100+sin(frameCount*0.1)*300; // map(width/2, 0, width, 100, 500);
		
		for (int h = 0; h < height; h += 10) {
		    
		    if(h < slider) {

		    pg.beginShape(); 
		    //pg.stroke(map(h, frameCount%height, height, 10, 20), map(h, frameCount%height, height, 0, 100), 255);
		    float colorShade = map(h, 0, height, 0, 255);
		    float colorShade2 = map(h, 0, height, 0, 100);
		    float colorShade3 = map(h, slider-(100*movingSlider), height, 0, 255);
		    if(colorShade3<0) {
		    	colorShade3 = 0;
		    }
			pg.stroke(colorShade3, colorShade2+colorShade3, colorShade+colorShade3);

		    float x = 0;
		    float y = h + waveH * noise(noiseX, noiseY + h * 0.1, noiseF);

		    pg.curveVertex(x, y);
		    
		    for (int w = 0; w <= width+100; w += 50) {
		      x = w;
		      float n = noise(noiseX + w * 0.001, noiseY + h * 0.01, noiseF + f);
		      y = h + waveH * n;

		      pg.curveVertex(x, y);
		    }

		    x = width;
		    y = h + waveH * noise(noiseX + width, noiseY + h * 0.01, noiseF + f);

		    pg.curveVertex(x, y);
		    pg.endShape();

		}



		}

		pg.endDraw();
		return pg;
	}



}