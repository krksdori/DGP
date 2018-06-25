    
class RainDrops {
  
  ArrayList<Rain> rains = new ArrayList();
  PGraphics pg;
  int blockw = 2560;
  int blockh = 1440;
  float rainOffset = 0.0;
  float rainOffsetCurrent = 0.0;
  float rainOffsetTarget = 0.0;
  
  RainDrops(int _width, int _height) {
    
    //pg = createGraphics(1080, 720);
    pg = createGraphics(_width, _height);
    
    blockw = _width;
    blockh = _height;
     
    for(int i = 0; i < 1000; i++){
     rains.add(new Rain());
    }
    
    
  }
  
  PGraphics draw(float p, float windDirection, float windSpeed) {
   
   pg.beginDraw();
   pg.background(100);


   if(p < 0.025) {
     pg.background(0, 0, map(p, 0.0, 1.0, 140, 100));
     //pg.background(0, 0, 255);
   } else {
     //pg.background(255, 0, 0);
     pg.background(map(p, 0.0, 1.0, 10, 50));
   } 
   
   if( p < 0.025) {
      p = 0.0; 
   }
   float treshHold = map(p, 0.0, 1.0, 0, 1000);
   
   rainOffset = 0.0;
   if(windDirection > 300 || windDirection < 60) {
      rainOffset = -40*windSpeed;
      rainOffsetTarget = map(windDirection, 300, 60, -40*windSpeed, -0*windSpeed);
      //println("right");
   } else if(windDirection > 120 && windDirection < 240) {
      rainOffset = 40*windSpeed;
      rainOffsetTarget = map(windDirection, 120, 240, 40*windSpeed, 0*windSpeed);
      //println("left");
   }


   //rainOffsetCurrent = rainOffsetCurrent*0.9 + rainOffsetTarget*0.1;

  // rainOffset = rainOffsetCurrent;

   for (int i = 0; i < rains.size(); i++) {
     if(i<treshHold) {   //treshHold
        Rain rain = rains.get(i);
        rain.fall(rainOffset);
        rain.show(pg, rainOffset);
        
      }
   }
   
   pg.endDraw();
   return pg;
  }
}

class Rain {
  
  float x;
  float y;
  float z;
  float len;
  float yspeed;
  float mx;

  Rain() {
    x  = random(blockw);
    y  = random(-blockh, 0);
    z  = 2.0;//random(-400, 25);
    len = random(10)+5.0;
    yspeed  = random(5)+10;

  }

  void fall(float rainOffset) {
    y = y + (yspeed+yspeed*0.1);
    x = x - rainOffset;//(yspeed+yspeed*0.1);
    
    if (y > blockh) {
      y = 0;
      yspeed = random(5)+10;
    }
    
     if (x > blockw) {
      x = 0;
     }
     
     if (x < 0) {
      x = blockw;
     }
  }

  void show(PGraphics pg, float rainOffset) {
    pg.stroke(255, 150);
    pg.line(x, y, x-rainOffset, y+yspeed+len);
  }
}