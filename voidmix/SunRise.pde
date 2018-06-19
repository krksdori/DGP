
class SunRise {
  
  Blob b;
  PGraphics pg;
  int blockw = 2560;
  int blockh = 1440;
 
  SunRise(int _width, int _height) {
     b = new Blob(0, 0);
     //pg = createGraphics(1080, 720);
     pg = createGraphics(_width, _height);
     blockw = _width;
     blockh = _height;


     
  }
  

  PGraphics draw(float cloudCoverN) {

    float v = map(cloudCoverN, 0, 1.0, 230, 150);
   if(cloudCoverN > 0.6) {
     v = 100;
   } 


   pg.beginDraw();
   pg.loadPixels();
    for (int x = 0; x < pg.width; x++) {
      for (int y = 0; y < pg.height; y++) {
        int index = x + y * pg.width;
        float d = dist(x, y, b.pos.x, b.pos.y);
        float col = 350 * b.r / d;
        pg.pixels[index] = color(col, v);
      }
    }
  
  pg.updatePixels();
  pg.endDraw();
  b.update();
  
  return pg;
  }
  
  
  
}


class Blob {
  PVector pos;
  float r;
  PVector vel;
  
  Blob(float x, float y) {
    pos = new PVector (cos(x), sin(y));
    vel = new PVector(-1 , 1);
    r = 80;
    
    pos.x = 0;
    pos.y = 0;
  }
  
  float sliceRotation = (PI*1.0)/(60.0*6.0);

  void update() {
    pos.x = ((cos( ((-frameCount*sliceRotation)-PI*2.0)%(PI*1.0) ) )*blockw/2) + blockw/2 ; // + width/2 -100
    pos.y = ((sin( ((-frameCount*sliceRotation)-PI*2.0)%(PI*1.0) ) )*blockh*1.2) +blockh*1.5 ; // + height+height/3 -100
    // pos.x = ((width+200.0) - ((frameCount*sliceRotation)%(width+400.0)));
    // pos.y = height/2;

  }

  

}