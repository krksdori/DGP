
class PerlinCloud {

  PGraphics pg;
  float noiseScale = 0.01;
  int blockw = 2560;
  int blockh = 1440;


  PerlinCloud(int _width, int _height) {
    pg = createGraphics(_width, _height);
    blockw = _width;
    blockh = _height;
  }

  PGraphics draw(float cloudCoverN) {
    pg.beginDraw();
    pg.background(0);

    colorMode(HSB, 360, 600, 100);

    if (cloudCoverN < 0.6) {
      cloudCoverN = 0;
    }


    float CC = map(cloudCoverN, 0, 1.0, 0, 255);



    pg.loadPixels();

    noiseScale = frameCount*0.001;

    for (int x=0; x < blockw; x++) {
      for (int y=0; y < blockh; y++) {
        float noiseVal = noise(x*0.01+frameCount*0.01, y*0.01+frameCount*0.01, noiseScale)*2;

        pg.pixels[x+y*blockw] = color(noiseVal*255, CC);
      }
    }

    pg.updatePixels();
    pg.endDraw();

    colorMode(RGB, 255);

    return pg;
  }
}
