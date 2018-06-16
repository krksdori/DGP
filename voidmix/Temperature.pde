
class Temperature {

	PGraphics pg;

  color warm = color(156, 142, 123);
  color cold = color(116, 137, 160);

	Temperature(int width, int height) {
		pg = createGraphics(width, height);
	}

	PGraphics draw(float temperature) {
		  pg.beginDraw();
		  pg.loadPixels();

      float finalR = red(cold)*(1.0-temperature) + red(warm)*(temperature); 
      float finalG = green(cold)*(1.0-temperature) + green(warm)*(temperature); 
      float finalB = blue(cold)*(1.0-temperature) + blue(warm)*(temperature); 

		  for (int i = 0; i < width*height; i++) {
  				pg.pixels[i] = color(finalR, finalG, finalB);
		  }
		  pg.updatePixels();
		  pg.endDraw();
		  
		  return pg;
	}

}
