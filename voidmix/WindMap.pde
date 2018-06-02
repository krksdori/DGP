
class WindMap {

	FlowField flowfield;
	ArrayList<Vehicle> vehicles;
	PGraphics pg;

	WindMap(int width, int height) {
		//pg = createGraphics(1080, 720);
		pg = createGraphics(width, height);

		flowfield = new FlowField(10);
		vehicles = new ArrayList<Vehicle>();

		for (int i = 0; i < 300; i++) {
		  vehicles.add(new Vehicle(new PVector(random(width), random(height)), random(2, 5), random(0.1, 1.1)));
		}
	}

	 PGraphics draw(int windDirection, float windSpeedN) {

	 	pg.beginDraw();

	 	pg.background(0);
  		flowfield.display(pg);
  		
  		for (Vehicle v : vehicles) {
    		v.follow(flowfield);
    		v.run(pg, windSpeedN);
  		}
  
  		flowfield.init(windDirection);
  		pg.endDraw();	

  		return pg;
	 }


}


class Vehicle {

  // The usual stuff
  PVector position;
  PVector velocity;
  PVector acceleration;
  float r;
  float maxforce;    // Maximum steering force
  float maxspeed;    // Maximum speed
  float extraspeed;

    Vehicle(PVector l, float ms, float mf) {
    position = l.get();
    r = 3.0;
    maxspeed = ms;
    maxforce = mf;
    extraspeed = random(0.0, 1.0);
    acceleration = new PVector(0,0);
    velocity = new PVector(0,0);
  }

  public void run(PGraphics pg, float windSpeedN) {
    update(windSpeedN);
    borders();
    display(pg);
  }

void follow(FlowField flow) {
    PVector desired = flow.lookup(position);
    
    desired.mult(maxspeed);
    PVector steer = PVector.sub(desired, velocity);
    steer.limit(maxforce);  // Limit to maximum steering force
    applyForce(steer);
  }

  void applyForce(PVector force) {
    acceleration.add(force);
  }

  void update(float windSpeedN) {
    
    maxspeed = (windSpeedN*5.25)+extraspeed*2.0; // sin(frameCount*0.1)*
    velocity.add(acceleration);
    velocity.limit(maxspeed);
    position.add(velocity);
    acceleration.mult(0);
  }

  void display(PGraphics pg) {
    pg.pushMatrix();

    pg.stroke(255,0,0);
    float arrowsize = 8;
    pg.translate(position.x,position.y);
    pg.stroke(255);
    pg.rotate(velocity.heading2D());
    float len = velocity.mag();
    float c = map(maxspeed, 2, 7, 0, 255);
    pg.stroke(c, 255-c, 255-c);
    pg.line(len,0,len-arrowsize*2.0,0);
    pg.line(len,0,len-arrowsize,+arrowsize/2);
    pg.line(len,0,len-arrowsize,-arrowsize/2);
    pg.popMatrix();
  }

  void borders() {
    if (position.x < -r) position.x = width+r;
    if (position.y < -r) position.y = height+r;
    if (position.x > width+r) position.x = -r;
    if (position.y > height+r) position.y = -r;
  }
}



class FlowField {

  PVector[][] field;
  int cols, rows; 
  int resolution; 

  FlowField(int r) {
    resolution = r;
    cols = width/resolution;
    rows = height/resolution;
    field = new PVector[cols][rows];
    init(0);
  }

  void init(int windDirection) {
    noiseSeed((int)1000);
    float xoff = 0;

    float windDireciton = windDirection;
    float windDirecitonMapped = map(windDireciton, 0.0, 360.0, PI*0.0, PI*2.0);

    
    for (int i = 0; i < cols; i++) {
      float yoff = 0;
      for (int j = 0; j < rows; j++) {
        //float theta = map(sin(frameCount*0.01+noise(xoff,yoff)),0,2,0,TWO_PI);
        field[i][j] = new PVector(cos(windDirecitonMapped+noise(xoff,yoff)),sin(windDirecitonMapped+(noise(xoff,yoff)*0.5) ));
        yoff += 0.1;
      }
      xoff += 0.1;
    }


  }

  void display(PGraphics pg) {
    for (int i = 0; i < cols; i += 2) {
      for (int j = 0; j < rows; j += 2) {
        drawVector(field[i][j],i*resolution,j*resolution,resolution-2, pg);
      }
    }

  }

  void drawVector(PVector v, float x, float y, float scayl, PGraphics pg) {
    pg.pushMatrix();
    float arrowsize = 4;
    pg.translate(x,y);
    pg.stroke(255, 100);
    pg.rotate(v.heading2D());
    float len = v.mag()*scayl;
    pg.line(-len/2,0,len/2,0);
    pg.colorMode(RGB, 255);
    pg.popMatrix();
  }

  PVector lookup(PVector lookup) {
    int column = int(constrain(lookup.x/resolution,0,cols-1));
    int row = int(constrain(lookup.y/resolution,0,rows-1));
    return field[column][row].get();
  }


}
