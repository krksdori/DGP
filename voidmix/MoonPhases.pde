
class MoonPhases {
     
  float currentf = 255;
  color f = color(255);
  color bg = color(0,0,0);
  PShader blur;
  float theta;
  float t;
  float radius = 500;
  int frames = 600;
  PGraphics moonTexture; 
  PGraphics moonPhases;
  PGraphics moon;
  int blockw = 2560;
  int blockh = 1440;
  PGraphics pg;
  
  MoonPhases(int _width, int _height) {
      blockw = _width;
      blockh = _height;
      //pg = createGraphics(1080, 720);
      pg = createGraphics(_width, _height);

      moonTexture = createGraphics(blockw, blockh);
      moonPhases = createGraphics(blockw, blockh);
      moon = createGraphics(blockw, blockh);

      moonTexture.beginDraw();
      moonTexture.background(255);
      for(int i = 0; i< 24000; i++){
        float t = random(TWO_PI);
        
        moonTexture.strokeWeight(0.1);
        moonTexture.stroke(0, 0, 0, 10);

        float x1 = blockw/2 + radius*0.5 * cos(t);
        float y1 = blockh/2 + radius*0.5 * sin(t);

        t = random(TWO_PI);

        float x2 = blockw/2 + radius*0.5 * cos(t);
        float y2 = blockh/2 + radius*0.5 * sin(t);
        moonTexture.line(x1, y1, x2, y2);
      }
    
      moonTexture.endDraw();
      //moonPhasesDraw(moonPhases, 0.0);

  }

  void moonPhasesDraw(PGraphics p, float moonAge,  float moonAgeStart, float cloudCoverN, float ticker) {
    p.beginDraw();
    
    float mapMoonData = map(moonAge, 0.0, 29.53059, 600.0, 0.0);
    int offset = int(map(moonAgeStart, 0.0, 29.53059, 600.0, 0.0));


    float sliceSize = 29.53059/600.0;
    float slider = ((300)+offset - (ticker)*sliceSize);
    
    t = (( slider ) % frames) / (float)frames;

    if(slider < 0) {
      t = 1.0 + t;
    }

    //println(slider + " --  " + t);

    //((mapMoonData+300)%frames)/(float)frames;
    
    p.background(0);
    p.translate(blockw/2, blockh/2);
    

    float newf = map(cloudCoverN, 0, 1, 255, 200);
    currentf = currentf*0.9 + newf * 0.1;
    f = color(currentf);


    //f = color(255, map(cloudCoverN, 0, 1, 10, 200));
    //f = color(100);
    //float moonRotate = map(moonAge, 0.0, 29.53059, PI*0.0, -PI*2.0);

    //float rX = cos(moonRotate)*blockw/3;
    //float rY = sin(moonRotate)*blockh/3;

    //p.rotate(moonRotate);
    //p.translate(floor(rX), floor(rY));
    //p.rotate(frameCount*0.1);


    p.noStroke();
    p.rotate(PI/2);
    if (t < 0.5) {
      float tt = map(t, 0, 0.5, 0, 1); // normalizing the time of the first half of the cycle
      if (tt < .5) {
        p.fill(f);
        float r = map(tt, 0, 0.5, radius, 0);
        p.arc(0, 0, radius, r, 0, PI); // moon lower, shrinking
        p.arc(0, 0, radius, radius, PI, TWO_PI); // moon upper, stable
      } else {
        p.fill(f);
        p.arc(0, 0, radius, radius, PI, TWO_PI); // moon upper, stable
        float r = map(tt, 0.5, 1.0, 0, radius);
        p.fill(bg);
        p.arc(0, 0, radius, r, PI, TWO_PI); // bg upper, increasing
      }
    } else {
      float tt = map(t, 0.5, 1, 0, 1); // normalizing the time of the second half of the cycle
      if (tt <.5) {
        p.fill(bg);
        p.arc(0, 0, radius, radius, PI, TWO_PI); // bg upper, stable
        float r = map(tt, 0, 0.5, radius, 0);
        p.fill(f);
        p.arc(0, 0, radius, radius, 0, PI); // moon lower, stable > partly hidden by the following arc
        p.fill(bg);
        p.arc(0, 0, radius, r, 0, PI); // bg lower, decreasing
      } else {
        p.fill(f);
        float r = map(tt, 0.5, 1.0, 0, radius);
        p.arc(0, 0, radius, radius, 0, PI); // moon lower, stable
        p.arc(0, 0, radius, r, PI, TWO_PI); // moon upper, increasing
      }
    }
    theta+=TWO_PI/frames;
    
    //p.filter(blur);
    //p.filter(BLUR, 8);

    p.endDraw();
}

PGraphics rotateMoon(float moonAge, float x, float y, float cloudCoverN) {
  moon.beginDraw();
  moon.pushMatrix();
  moon.translate(blockw/2,blockh/2);

  float moonRotate = map(moonAge, 0.0, 29.53059, PI*0.0, -PI*2.0);

  //moon.rotate(moonRotate);

  float rX = cos(moonRotate)*blockw/3;
  float rY = sin(moonRotate)*blockh/3;
  
 // moon.translate(floor(rX), floor(rY));
    
  //moon.rotate( (float)(frameCount*0.02*Math.sin(frameCount*0.005)*0.01) );
  //moon.fill(255, 0, 0);

  
  // moon.rect(0, 0, width,height);
  moon.translate(x, y);
  //moon.tint(25, 255);//map(cloudCoverN, 0.0, 1.0, 255, 10));
  moon.image(moonTexture, -blockw/2, -blockh/2);
  //moon.fill(255, 0, 0);
  //moon.rect(0, 0, width, height);
  moon.popMatrix();
  moon.endDraw();
  return moon;
}
  
  PGraphics draw(float moonAge, float moonAgeStart, int ticker, float cloudCoverN) {

    
    pg.beginDraw();
    pg.background(0);

    //float moonRotate = map(moonAge, 0.0, 29.53059, PI*0.0, -PI*2.0);

    float sliceRotation = ((PI*2.0))/( ((60.0*4.0)*29.53059) );

    float offset = map(moonAgeStart, 0.0, 29.53059, 0, 2.0*PI); // start

    float x = ((cos( ((ticker*-sliceRotation-offset))%(PI*2.0) ) )*blockw/3.3) ;//+ blockw/2 ; // + width/2 -100
    float y = ((sin( ((ticker*-sliceRotation-offset))%(PI*2.0) ) )*blockw/4.3) ; //+blockh*1.5 ; // + height+height/3 -100
    

    //float rX = cos(moonRotate)*blockw/3;
    //float rY = sin(moonRotate)*blockh/3;
    //pg.translate(floor(rX), floor(rY));
    pg.translate(x, y);
    pg.pushMatrix();

    moonPhasesDraw(moonPhases, moonAge, moonAgeStart, cloudCoverN, ticker);
    
    pg.image(moonPhases, 0, 0);
    pg.pushMatrix();

    pg.blend(rotateMoon(moonAge, x, y, cloudCoverN), 0, 0, blockw, blockh, 0, 0, blockw, blockh, SUBTRACT);
    pg.popMatrix();
    pg.popMatrix();

    pg.endDraw();
    
    
    return pg;
  }
  
}
