/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Random;

public class Robot {
    private static final int RADIO_RADIUS = 200;
    
    private MVector location;
    private MVector velocity;
    private MVector acceleration;
    private MVector direction;
    private MVector steer;
    
    private Random r;
    private GeneralPath vehicleShape;
    private double angle, maxSpeed, maxForce, maxWanderForce, pointOnCircleAngle, vehicleScale;
    
    private int state; // 0 for beacon, 1 for walker
    private int foodCardinality;
    private int nestCardinality;
    private Nest nest;
    private Food food;
    private Boolean isCarryingFood;
    ArrayList<Robot> neighbours;
    
    private ObstacleSensor leftSensor;
    private ObstacleSensor rightSensor;
    
    // this is the sensor that got activated first
    private ObstacleSensor primarySensor;
    
    // -- do we really need this?
    private MVector pointToFleeFrom;
    private int arriveDistanceistance;
       
    public Robot( MVector _location, MVector _direction){
        location = _location;
        
        maxSpeed = 1.5;
        maxForce = 0.2;
        maxWanderForce = 0.05;
        arriveDistanceistance = 20;
        vehicleScale = 10;
        velocity = new MVector();
        acceleration = new MVector();
        steer = new MVector();
        pointToFleeFrom = new MVector();
        vehicleShape = new GeneralPath();
        
        r = new Random();
        
        this.state = 1;
        this.foodCardinality = 0;
        this.nestCardinality = 0;
        this.isCarryingFood = false;
        
        direction = _direction;
        
        leftSensor = new ObstacleSensor();
        rightSensor = new ObstacleSensor();
        primarySensor = new ObstacleSensor();
        neighbours = new ArrayList<>();
        
        this.updateShape();
    }
    
    
    //  ---------- properties
    public int getState() { return state; }
    public void setState(int state) { this.state = state; }

    public int getFoodCardinality() { return foodCardinality; }
    public void setFoodCardinality(int foodCardinality) { this.foodCardinality = foodCardinality; }

    public int getNestCardinality() { return nestCardinality; }
    public void setNestCardinality(int nestCardinality) { this.nestCardinality = nestCardinality; }
    
    public MVector getPointToFleeFrom() { return pointToFleeFrom; }
    public void setPointToFleeFrom(MVector pointToFleeFrom) { this.pointToFleeFrom = pointToFleeFrom; }
    
    public double getScale() { return vehicleScale; }
    public void setScale(double scale){ this.vehicleScale = scale; }
    
    public double getMaxSpeed() { return maxSpeed; }
    public void setMaxSpeed(double speed) { maxSpeed = speed; }

    public double getMaxForce(){ return maxForce; }
    public void setMaxForce(double force){ maxForce = force; }

    public MVector getLocation() { return location; }
    public void setLocation(MVector location) { this.location = location; this.updateShape();}
    
    public MVector getVelocity() { return velocity; }
    public void setVelocity(MVector velocity) { this.velocity = velocity; }
    
    public GeneralPath getShape() { return vehicleShape; }
    public void setShape(GeneralPath vehicleShape) { this.vehicleShape = vehicleShape; }
    
    public MVector getAcceleration() { return acceleration; }
    public void setAcceleration(MVector acceleration) { this.acceleration = acceleration; }
    
    public MVector getDirection() { return direction; }
    public void setDirection(MVector direction) { this.direction = direction; }
    
    public Boolean isCarryingFood() { return isCarryingFood; }
    public void setCarryingFood(Boolean isCarryingFood) { this.isCarryingFood = isCarryingFood; }
  
    // ------ behaviors
    public void Wander(){
        // calculate the target as a random point on a circle in front of the vehicle
        
        MVector normilizedSpeed = new MVector(getDirection().getX(), getDirection().getY());
        normilizedSpeed.Normalize();
        normilizedSpeed.Multiply(100);
        
        pointOnCircleAngle += r.nextDouble() - 0.5;  
        MVector pointOnCircle = new MVector(pointOnCircleAngle);
        pointOnCircle.Multiply(60);
        normilizedSpeed.Add(pointOnCircle);
        normilizedSpeed.Add(location);
        
        MVector desired = MVector.Subtract(normilizedSpeed, location);
        desired.Limit(maxSpeed);

        steer = MVector.Subtract(desired, velocity);
        steer.Limit(maxWanderForce);
        ApplyForce(steer);
    }
    
    public void Seek(MVector target){
        MVector desired = MVector.Subtract(target, location);
        desired.Limit(maxSpeed);
        steer = MVector.Subtract(desired, velocity);
        steer.Limit(maxForce);
        ApplyForce(steer);
    }
    
    public void Flee(MVector target){
        MVector desired = MVector.Subtract(target, location);
        desired.Limit(maxSpeed);
        steer = MVector.Subtract(velocity, desired);
        steer.Limit(maxForce);
        ApplyForce(steer);
    
    }
    
    public void Arrive(MVector target){
        MVector desired = MVector.Subtract(target, location);
        double d = desired.getLength();
        if (d < arriveDistanceistance) {
            double m = d/arriveDistanceistance * maxSpeed;
            desired.Normalize();
            desired.Multiply(m);
        } 
        else{
            desired.Limit(maxSpeed);
        }
       
        steer = MVector.Subtract(desired, velocity);
        steer.Limit(maxForce);
        ApplyForce(steer);
        //this.Update();
    }
    
    public void Stop () {
        this.acceleration = new MVector(0,0);
        this.velocity = new MVector(0,0);   
    }
    
    public Boolean avoidObstacles(ArrayList<GeneralPath> obstacles) {
        boolean rightSensorStateBeforeUpdate = rightSensor.getState();
        boolean leftSensorStateBeforeUpdate = leftSensor.getState();
        
        rightSensor.setState(false);
        leftSensor.setState(false);
        
        for (GeneralPath obstacle : obstacles) {
            if (rightSensor.getShape().intersects(obstacle.getBounds2D())) {
                rightSensor.setState(true);
            }
            if (leftSensor.getShape().intersects(obstacle.getBounds2D())) {
                leftSensor.setState(true);
            }
        }
        
        MVector obstaclePointToFleeFrom = new MVector();
        
        if (leftSensor.getState()) {
            obstaclePointToFleeFrom.setX(leftSensor.getShape().getCurrentPoint().getX());
            obstaclePointToFleeFrom.setY(leftSensor.getShape().getCurrentPoint().getY());
            if ( (primarySensor != null) && (primarySensor != rightSensor)) {
                primarySensor = leftSensor;
            }
        } 
                
        if (rightSensor.getState()) {
            obstaclePointToFleeFrom.setX(rightSensor.getShape().getCurrentPoint().getX());
            obstaclePointToFleeFrom.setY(rightSensor.getShape().getCurrentPoint().getY());
            if ( (primarySensor != null) && (primarySensor != leftSensor)) {
                primarySensor = rightSensor;
            }
        }
        
        if (rightSensor.getState() && leftSensor.getState()) {
            obstaclePointToFleeFrom.setX(primarySensor.getShape().getCurrentPoint().getX());
            obstaclePointToFleeFrom.setY(primarySensor.getShape().getCurrentPoint().getY());
        }
        
        
        if (leftSensor.getState() || rightSensor.getState()) {
            maxSpeed = 0.6;
            maxForce = 0.1;
            this.Flee(obstaclePointToFleeFrom);
            return true;
        } else {
            maxSpeed = 1.5;
            maxForce = 0.2;
        }
        
        return false;
    }
    
    
    // ------- cardinality
    public void becomeBeacon() {
        this.state = 0;
        this.foodCardinality = 0;
        this.nestCardinality = 0;
    }
    
    public void becomeWalker() {
        this.state = 1;
    }
    
    public void Beacon() {
        this.Stop();
        
        // update both cardinalities
        int minNestCardinality = 10000;
        int minFoodCardinality = 10000;
        for (Robot neighbour : this.neighbours) {
            if ((neighbour.foodCardinality < minFoodCardinality) && (neighbour.foodCardinality != 0)) {
                minFoodCardinality = neighbour.foodCardinality;
            }
            if ((neighbour.nestCardinality < minNestCardinality) && (neighbour.nestCardinality != 0)) {
                minNestCardinality = neighbour.nestCardinality;
            }
            
            if ((this.nest != null) && this.getShape().intersects(nest.getShape().getBounds())){
                this.nestCardinality = 1;
            } else if (minNestCardinality != 1000) {
                this.nestCardinality = minNestCardinality+1;
            }
            
            if ((this.food != null) && this.getShape().intersects(food.getShape().getBounds())){
                this.foodCardinality = 1;
            } else if (minFoodCardinality != 10000) {
                this.foodCardinality = minFoodCardinality+1;
            }
        }
    }
    
    public void Walker(ArrayList<GeneralPath> _obstacles) {               
        if (neighbours.size() >=2 ) {
            if (!this.avoidObstacles(_obstacles)) {
                // check whether we are sensing food, if we are not carrying it
                if ((food != null) && (!this.isCarryingFood) && (rightSensor.getShape().intersects(food.getShape().getBounds()) || 
                        leftSensor.getShape().intersects(food.getShape().getBounds()))) {
                   
                    // check whether it is occupied
                    if  (!food.isOccupied()) { // if it is not occupied, go stand on it as beacon! 
                        if (((int)this.getLocation().getX() == (int)food.getLocation().getX()) && 
                            ((int)this.getLocation().getY() == (int)food.getLocation().getY())) {
                            this.becomeBeacon();
                            this.food.setOccupied(true);
                        } else {
                            this.Seek(food.getLocation());
                        } 
                    } 
                    else { // well if it is occupied than get a piece of it and start moving home
                        this.biteFood(this.food);
                    }
                }
                else { // if we don't sense food, see if we are sensing the nest
                    if ((nest != null) && (this.isCarryingFood) && (rightSensor.getShape().intersects(nest.getShape().getBounds()) || 
                        leftSensor.getShape().intersects(nest.getShape().getBounds()))) { // if we are sensing nest, spit food into it and go back to searching food 
                        this.spitFoodIntoNest(this.nest);
                    }
                    else { // if we are not sensing nest
                        if (this.isCarryingFood) { // if carrying food, get going home
                            this.searchNest();
                        } 
                        else { // if not than should search for food
                            this.searchFood();
                        }
                    }
                }
            }
        } else {
            this.becomeBeacon();
        }
    }
    
    public void biteFood(Food _food) {
        if (_food.getFoodAmount()!=0) {
            _food.setFoodAmount(_food.getFoodAmount()-1);
            this.setCarryingFood(true);
            System.out.print(String.format("food source amount after bite %d", this.food.getFoodAmount()));
        }
    }
    
    public void spitFoodIntoNest(Nest _nest) {
        if (_nest.getFoodAmount() < _nest.maxFoodAmount()) {
            _nest.setFoodAmount(_nest.getFoodAmount() + 1);
            this.setCarryingFood(false);
            System.out.print(String.format("nest food amount after spit %d", this.nest.getFoodAmount()));
        }
    }
    
    public void searchNest() {
        // to search for nest we have to go through all neighbours
        // to see whether at least one has nest cardinality other than 0
        int minNestCardinality = 1000;
        Robot beaconToGoTo = null;
        for (Robot neighbour : this.neighbours) {
            if ((neighbour.nestCardinality < minNestCardinality) && (neighbour.nestCardinality != 0)) {
                minNestCardinality = neighbour.nestCardinality;
                beaconToGoTo = neighbour;
            }
        }
        
        if (beaconToGoTo != null) { // go to that beacon
            this.Seek(beaconToGoTo.getLocation());
        }
        else { // just wander
            this.Wander();
        }        
    }
    
    public void searchFood() {
        // to search for food we have to go through all neighbours
        // to see whether at least one has food cardinality other than 0
        int minFoodCardinality = 1000;
        Robot beaconToGoTo = null;
        for (Robot neighbour : this.neighbours) {
            if ((neighbour.foodCardinality < minFoodCardinality) && (neighbour.foodCardinality != 0)) {
                minFoodCardinality = neighbour.foodCardinality;
                beaconToGoTo = neighbour;
            }
        }
        
        if (beaconToGoTo != null) { // go to that beacon
            this.Seek(beaconToGoTo.getLocation());
        }
        else { // just wander
            this.Wander();
        }
        
    }
    
    public void Update(ArrayList<Robot> _neighbours, ArrayList<Obstacle> _obstacles, Nest _nest, Food _food){
        // here is where all the moving logic will be handled
        
        nest = _nest;
        food = _food;
        
        this.neighbours.clear();
        ArrayList<GeneralPath> allObstacles = new ArrayList<>();

         for (Obstacle obs : _obstacles) {
            allObstacles.add(obs.getShape());
         }
         for (Robot robot : _neighbours) {
            
            if (robot.state == 0) {
                this.neighbours.add(robot);
            } else { // think that beacons are thin as air =)
                allObstacles.add(robot.getShape());
            }
         }
        
         if (this.state == 0) { // beacon
             this.Beacon();
         }
         else if (this.state == 1) {
             this.Walker(allObstacles);
         }
         
         
//        if(!this.avoidObstacles(allObstacles)){
//            this.Wander();
//        }


        this.updateShape();
        velocity.Add(acceleration);
        velocity.Limit(maxSpeed);
        location.Add(velocity);
        acceleration.Multiply(0);
    }
        
    private void ApplyForce(MVector force){
        this.acceleration.Add(force);
    }
    
    // ------- drawing
    public void paintSelf(Graphics2D g, Boolean sensorsToggled) {
        
        
       
        
        for (Robot neighbour : this.neighbours) {
            if (this.state == 0) {
                g.setColor(Color.GREEN);
                g.drawLine( (int)this.location.getX(), (int)this.location.getY(), 
                        (int)neighbour.location.getX(), (int)neighbour.location.getY());
            }            
        }
        
        if (this.state == 1) {
            g.setColor(Color.BLACK);
            g.fill(this.getShape());
            
            if (sensorsToggled) {
                if (leftSensor.getState()) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                }
                g.draw(leftSensor.getShape());

                if (rightSensor.getState()) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                }
                g.draw(rightSensor.getShape());
            }
            
            g.setColor(Color.WHITE);
            if (this.isCarryingFood) {
                g.fillOval((int)this.location.getX() - 2, (int)this.location.getY() - 2,  4, 4);
            }
            
        } else if (this.state == 0) {
            g.setColor(Color.RED);
            g.fill(this.getShape());
            g.setColor(Color.BLACK);
            g.draw(this.getShape());
            
            g.setColor(Color.GREEN);
            g.drawString(String.format("%d", this.nestCardinality), (int)this.location.getX()+10, (int)this.location.getY() - 5 );
            g.drawString(String.format("%d", this.foodCardinality), (int)this.location.getX()+10, (int)this.location.getY() + 5 );
        }
        

    }
    
    private void updateShape(){
        if (this.state == 1) { // walker
        
            // update shape of the vehicle
            vehicleShape.reset();
            vehicleShape.moveTo(location.getX() , location.getY() + 1.5 * vehicleScale);
            vehicleShape.lineTo(location.getX() + vehicleScale, location.getY() - vehicleScale );
            vehicleShape.lineTo(location.getX() - vehicleScale, location.getY() - vehicleScale);
            vehicleShape.closePath();


            // adjust direction if velocity is non-zero
            if ((velocity.getX() != 0) && (velocity.getY() != 0)) {
                getDirection().setX(velocity.getX());
                getDirection().setY(velocity.getY());
            }

            angle = -1 * Math.atan2(getDirection().getX(), getDirection().getY()) ;
            AffineTransform rat = new AffineTransform();
            rat.rotate(angle, location.getX(), location.getY());
            vehicleShape.transform(rat);

            // now update shapes of the obstacle sensors        
            GeneralPath rightSensorShape = rightSensor.getShape();
            rightSensorShape.reset();
            rightSensorShape.moveTo(location.getX() - vehicleScale, location.getY() + 2 * vehicleScale);
            rightSensorShape.lineTo(location.getX() - vehicleScale*0.25, location.getY() + vehicleScale);
            rightSensorShape.lineTo(location.getX() - vehicleScale*0.5, location.getY() + 2.25*vehicleScale);
            //rightSensorShape.moveTo(location.getX() - vehicleScale*2.75, location.getY() + 1.5 * vehicleScale);
            //rightSensorShape.lineTo(location.getX() - vehicleScale*1.5, location.getY() + 2.75 * vehicleScale);
            //rightSensorShape.lineTo(location.getX(), location.getY());
            rightSensorShape.closePath();
            rightSensorShape.transform(rat);

            GeneralPath leftSensorShape = leftSensor.getShape();
            leftSensorShape.reset();
            leftSensorShape.moveTo(location.getX() + vehicleScale, location.getY() + 2 * vehicleScale);
            leftSensorShape.lineTo(location.getX() + vehicleScale*0.25, location.getY() + vehicleScale);
            leftSensorShape.lineTo(location.getX() + vehicleScale*0.5, location.getY() + 2.25*vehicleScale);
            //leftSensorShape.moveTo(location.getX() + vehicleScale*2.75, location.getY() + 1.5 * vehicleScale);
            //leftSensorShape.lineTo(location.getX() + vehicleScale*1.5, location.getY() + 2.75 * vehicleScale);
            //leftSensorShape.lineTo(location.getX(), location.getY());
            leftSensorShape.closePath();
            leftSensorShape.transform(rat);
        } 
        else if (this.state == 0) { // beacon 
            // update shape of the vehicle
            vehicleShape.reset();
            vehicleShape.moveTo(location.getX() - vehicleScale/2, location.getY() - vehicleScale/2);
            vehicleShape.lineTo(location.getX() + vehicleScale/2, location.getY() - vehicleScale/2);
            vehicleShape.lineTo(location.getX() + vehicleScale/2, location.getY() + vehicleScale/2);
            vehicleShape.lineTo(location.getX() - vehicleScale/2, location.getY() + vehicleScale/2);
            vehicleShape.closePath();
        }
        
    }



}