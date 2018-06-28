
class Temperature {

  PGraphics pg;

  //color warm = color(255, 100, 0);
  //color cold = color(0, 100, 255);



  color c1 = color(13,74,139); // cold
  color c2 = color(103, 168, 222);
  color c3 = color(198, 223, 223);
  color c4 = color(208, 223, 223); // warm
	
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

		  float c = temperature;//map(mouseX, 0, width, 0, 1.0);// temperature;
      //println(temperature);

		  color sc = color(0.0); 
		  color sc2 = color(0.0);
		  if(c<0.5) {
			  sc = lerpColor(c1, c2, map(c, 0.0, 0.5, 0.0, 1.0) );
		  }
		  if(c > 0.5) {
			  sc2 = lerpColor(c3, c4, map(c, 0.5, 1.0, 0.0, 1.0) );
		 }

      if(c > 0.85) {
        sc = color(255, 228, 196);
        sc2 = color(0, 0, 0);
      }

      	//float finalR = red(cold)*(1.0-temperature) + red(warm)*(temperature); 
      	//float finalG = green(cold)*(1.0-temperature) + green(warm)*(temperature); 
      	//float finalB = blue(cold)*(1.0-temperature) + blue(warm)*(temperature); 

      	  //color sc = lerpColor(cold, warm, map(mouseX, 0, width, 0, 1));
		  for (int i = 0; i < blockw*blockh; i++) {
  				pg.pixels[i] = color(sc+sc2, 200);
		  }



		  pg.updatePixels();


		  pg.endDraw();


		  return pg;
	}

}
