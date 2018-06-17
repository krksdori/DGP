    
class RainDrops {
  
  ArrayList<Rain> rains = new ArrayList();
  PGraphics pg;
  
  RainDrops(int width, int height) {
    
    //pg = createGraphics(1080, 720);
    pg = createGraphics(width, height);
     
    for(int i = 0; i < 1000; i++){
     rains.add(new Rain());
    }
    
    
  }
  
  PGraphics draw(float p, float windDirection, float windSpeed) {
   
   pg.beginDraw();
   if(p < 0.025) {
     //pg.background(0, 0, map(p, 0.0, 1.0, 140, 100));
     pg.background(100);
   } else {
     pg.background(map(p, 0.0, 1.0, 10, 50));
   } 
   
   if( p < 0.025) {
      p = 0.0; 
   }
   float treshHold = map(p, 0.0, 1.0, 0, 1000);
   
   float rainOffset = 0.0;
   
   if(windDirection > 300 || windDirection < 60) {
      rainOffset = -40*windSpeed;
      println("right");
   } else if(windDirection > 120 && windDirection < 240) {
      rainOffset = 40*windSpeed;
      println("left");
   }




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
    x  = random(width);
    y  = random(-height, 0);
    z  = 2.0;//random(-400, 25);
    len = random(10)+5.0;
    yspeed  = random(5)+10;

  }

  void fall(float rainOffset) {
    y = y + (yspeed+yspeed*0.1);
    x = x - rainOffset;//(yspeed+yspeed*0.1);
    
    if (y > height) {
      y = 0;
      yspeed = random(5)+10;
    }
    
     if (x > width) {
      x = 0;
     }
     
     if (x < 0) {
      x = width;
     }
  }

  void show(PGraphics pg, float rainOffset) {
    pg.stroke(255, 150);
    pg.line(x, y, x-rainOffset, y+yspeed+len);
  }
}
