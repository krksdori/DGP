
class TideLines {
	float noiseX;
	float noiseY;
	float noiseF;
	float f = 0.0;
	float colorState;

	PGraphics pg;

	TideLines(int width, int height) {
		pg = createGraphics(width, height);

		noiseX = random(100);
		noiseY = random(100);
		noiseF = random(100);
	}

	PGraphics draw() {
		pg.beginDraw();
		//pg.background(0);
		pg.stroke(255, 150);
		pg.strokeWeight(1);
		pg.noFill();

		pg.fill(0, 30);
		pg.rect(0, 0, pg.width, pg.height);

		
		f += 0.0015;
  
		float waveH = map(width/2, 0, width, 100, 500);
		for (int h = 0; h < height + 100; h += 3) {
		    pg.beginShape(); 
		    pg.stroke(map(h, frameCount%height, height, 10, 20), map(h, frameCount%height, height, 0, 100), 205);
		    float x = 0;
		    float y = h + waveH * noise(noiseX, noiseY + h * 0.01, noiseF + f);
		    pg.curveVertex(x, y);
		    for (int w = 0; w <= width; w += 20) {
		      x = w;
		      y = h + waveH * noise(noiseX + w * 0.001, noiseY + h * 0.01, noiseF + f);
		      pg.curveVertex(x, y);
		    }

		    x = width;
		    y = h + waveH * noise(noiseX + width, noiseY + h * 0.01, noiseF + f);
		    pg.curveVertex(x, y);
		    pg.endShape();
		}

		pg.endDraw();
		
		return pg;

	}



}