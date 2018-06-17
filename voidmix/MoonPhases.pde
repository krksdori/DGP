
class MoonPhases {
     
  color f = color(255);
  color bg = color(0,0,0);
  PShader blur;
  float theta;
  float t;
  float radius = 200;
  int frames = 600;
  PGraphics moonTexture; 
  PGraphics moonPhases;
  PGraphics moon;
  
  PGraphics pg;
  
  MoonPhases(int width, int height) {
      //pg = createGraphics(1080, 720);
      pg = createGraphics(width, height);

      moonTexture = createGraphics(width, height);
      moonPhases = createGraphics(width, height);
      moon = createGraphics(width, height);

      moonTexture.beginDraw();
      moonTexture.background(0);
      for(int i = 0; i< 24000; i++){
        float t = random(TWO_PI);
        
        moonTexture.strokeWeight(0.1);
        moonTexture.stroke(255, 255, 255, 10);

        float x1 = width/2 + radius*0.5 * cos(t);
        float y1 = height/2 + radius*0.5 * sin(t);

        t = random(TWO_PI);

        float x2 = width/2 + radius*0.5 * cos(t);
        float y2 = height/2 + radius*0.5 * sin(t);
        moonTexture.line(x1, y1, x2, y2);
      }
    
      moonTexture.endDraw();
      //moonPhasesDraw(moonPhases, 0.0);

  }

  void moonPhasesDraw(PGraphics p, float moonAge) {
    p.beginDraw();
    float mapMoonData = map(moonAge, 0.0, 29.53059, 600.0, 0.0);

    t = ((mapMoonData+300)%frames)/(float)frames;
    p.background(0);
    p.translate(width/2, height/2);

    float moonRotate = map(moonAge, 0.0, 29.53059, PI*0.0, -PI*2.0);

    float rX = cos(moonRotate)*200;
    float rY = sin(moonRotate)*200;

    //p.rotate(moonRotate);
    p.translate(floor(rX), floor(rY));
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

PGraphics rotateMoon(float moonAge) {
  moon.beginDraw();
  moon.pushMatrix();
  moon.translate(width/2,height/2);

  float moonRotate = map(moonAge, 0.0, 29.53059, PI*0.0, -PI*2.0);

  //moon.rotate(moonRotate);

  float rX = cos(moonRotate)*200;
  float rY = sin(moonRotate)*200;
  
  moon.translate(floor(rX), floor(rY));
    
  //moon.rotate( (float)(frameCount*0.02*Math.sin(frameCount*0.005)*0.01) );
  //moon.fill(255, 0, 0);

  
  // moon.rect(0, 0, width,height);
  moon.image(moonTexture, -width/2, -height/2);
  //moon.fill(255, 0, 0);
  //moon.rect(0, 0, width, height);
  moon.popMatrix();
  moon.endDraw();
  return moon;
}
  
  PGraphics draw(float moonAge) {

    
    pg.beginDraw();
    pg.background(0);

    moonPhasesDraw(moonPhases, moonAge);
    pg.image(moonPhases, 0, 0);
    pg.blend(rotateMoon(moonAge), 0, 0, width, height, 0, 0, width, height, DARKEST);
    pg.endDraw();
    
    
    return pg;
  }
  
}