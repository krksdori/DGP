
class PerlinCloud {

	PGraphics pg;
	float noiseScale = 0.01;


	PerlinCloud(int width, int height) {
		pg = createGraphics(width, height);
	}

	PGraphics draw() {
		  pg.beginDraw();
		  pg.background(0);

		  colorMode(HSB, 360, 600, 100);
		  
		  pg.loadPixels();
		  
		  noiseScale = frameCount*0.001;
		  
		  for (int x=0; x < width; x++) {
		    for (int y=0; y < height; y++) {
		      float noiseVal = noise(x*0.01+frameCount*0.01, y*0.01+frameCount*0.01, noiseScale)*2;
		    
		      pg.pixels[x+y*width] = color(noiseVal*255, 20);
		      
		      
		    }
		  }
		  
		  pg.updatePixels();
		  pg.endDraw();

		  colorMode(RGB, 255);
		  
		  return pg;
	}



}