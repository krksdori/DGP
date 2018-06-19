
class Temperature {

	PGraphics pg;

  color warm = color(255, 100, 0);
  color cold = color(0, 100, 255);
  int blockw = 2560;
  int blockh = 1440;

	Temperature(int _width, int _height) {
		pg = createGraphics(_width, _height);
    blockw = _width;
    blockh = _height;
	}

	PGraphics draw(float temperature) {
		  pg.beginDraw();
		  pg.loadPixels();

      	//float finalR = red(cold)*(1.0-temperature) + red(warm)*(temperature); 
      	//float finalG = green(cold)*(1.0-temperature) + green(warm)*(temperature); 
      	//float finalB = blue(cold)*(1.0-temperature) + blue(warm)*(temperature); 

      	  color sc = lerpColor(cold, warm, temperature );
		  for (int i = 0; i < blockw*blockh; i++) {
  				pg.pixels[i] = color(red(sc), green(sc), blue(sc), 130);
		  }



		  pg.updatePixels();


		  pg.endDraw();


		  return pg;
	}

}