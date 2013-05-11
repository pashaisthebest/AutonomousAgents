/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Random;

public class Robot {
    
    private MVector location;
    private MVector velocity;
    private MVector acceleration;
    private MVector direction;
    private MVector bounds;
    private MVector steer;
    
    private Random r;
    private GeneralPath vehicleShape;
    private double angle, maxSpeed, maxForce, maxWanderForce, pointOnCircleAngle, vehicleScale;
    
    private int state; // 0 for beacon, 1 for walker
    private int foodCardinality;
    private int nestCardinality;
    
    private ObstacleSensor leftSensor;
    private ObstacleSensor rightSensor;
    
    // this is the sensor that got activated first
    private ObstacleSensor primarySensor;
    
    // -- do we really need this?
    private MVector pointToFleeFrom;
    private int arriveDistanceistance ;
       
    public Robot( MVector _location, MVector _bounds ){
        location = _location;
        bounds = _bounds;
        
        maxSpeed = 1.5;
        maxForce = 0.2;
        maxWanderForce = 0.05;
        arriveDistanceistance = 130;
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
        
        direction = new MVector(1,0);
        
        leftSensor = new ObstacleSensor();
        rightSensor = new ObstacleSensor();
        primarySensor = new ObstacleSensor();
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
    
    public MVector getBounds() { return bounds; }
    public void setBounds(MVector _bounds) { this.bounds = _bounds; }
    
    public GeneralPath getShape() { return vehicleShape; }
    public void setShape(GeneralPath vehicleShape) { this.vehicleShape = vehicleShape; }
    
    public MVector getAcceleration() { return acceleration; }
    public void setAcceleration(MVector acceleration) { this.acceleration = acceleration; }
    
    
  
    // ------ behaviors
    public void Wander(){
        // calculate the target as a random point on a circle in front of the vehicle
        
        MVector normilizedSpeed = new MVector(direction.getX(), direction.getY());
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
    
//    public boolean KeepInsideBoundaries(){
//        MVector desired = null;
//        
//        if (location.getX()>bounds.getX()-40){  
//            desired = new MVector(-maxSpeed, velocity.getY());
//            //location.setX(50);
//            //velocity.setX(velocity.getX() * -1);
//        }
//        else{
//            if(location.getX()<40){
//                desired = new MVector(maxSpeed, velocity.getY());
//                //location.setX(bounds.getX()-50);
//                //velocity.setX(velocity.getX() * -1);
//            }
//        }
//        if (location.getY()>bounds.getY()-40){
//            desired = new MVector(velocity.getX(), -maxSpeed);
//            //location.setY(50);
//            //velocity.setY(velocity.getY() * -1);
//        }
//        else{
//            if(location.getY()<40){
//                desired = new MVector(velocity.getX(), maxSpeed);
//                //location.setY(bounds.getY()-50);
//                //velocity.setY(velocity.getY() * -1);
//            }
//        }
//        
//        if (desired != null) {
//            
//            desired.Normalize();
//            desired.Multiply(maxSpeed);
//
//            steer = MVector.Subtract(desired, velocity);
//            steer.Limit(maxForce);
//            this.ApplyForce(steer);
//            
//            return true;
//        }
//        return false;
//    }
    
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
            maxForce = 0.15;
            this.Flee(obstaclePointToFleeFrom);
            return true;
        } else {
            maxSpeed = 1.5;
            maxForce = 0.2;
        }
        
        return false;
    }
    
    
    public void Update(){
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
        
        g.setColor(Color.BLACK);
        g.fill(this.getShape());
        
    }
    
    private void updateShape(){
        // update shape of the vehicle
        vehicleShape.reset();
        vehicleShape.moveTo(location.getX() , location.getY() + 1.5 * vehicleScale);
        vehicleShape.lineTo(location.getX() + vehicleScale, location.getY() - vehicleScale );
        vehicleShape.lineTo(location.getX() - vehicleScale, location.getY() - vehicleScale);
        vehicleShape.closePath();
        
        
        // adjust direction if velocity is non-zero
        if ((velocity.getX() != 0) && (velocity.getY() != 0)) {
            direction.setX(velocity.getX());
            direction.setY(velocity.getY());
        }
        
        angle = -1 * Math.atan2(direction.getX(), direction.getY()) ;
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

    // ------- cardinality
    public void becomeBeacon() {
        this.state = 0;
        this.foodCardinality = 0;
        this.nestCardinality = 0;
    }
    
    public void becomeWalker() {
        this.state = 1;
    }
}