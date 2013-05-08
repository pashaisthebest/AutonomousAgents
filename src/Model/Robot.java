/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.Random;

public class Robot {
    
    private MVector location;
    private MVector velocity;
    private MVector acceleration;
    private MVector bounds;
    private MVector steer;
    private MVector pointToFleeFrom;
    private Random r;
    private GeneralPath vehicleShape;
    private double angle, maxSpeed, maxForce, pointOnCircleAngle, vehicleScale;
    private int arriveDistanceistance ;
    private Boolean state; // 0 for beacon, 1 for walker
    private int foodCardinality;
    private int nestCardinality;
       
    public Robot( MVector _location, MVector _bounds ){
        location = _location;
        bounds = _bounds;
        
        maxSpeed = 8;
        maxForce = 0.5;
        arriveDistanceistance = 130;
        vehicleScale = 10;
        
        velocity = new MVector();
        acceleration = new MVector();
        steer = new MVector();
        pointToFleeFrom = new MVector();
        r = new Random();
        
        this.state = true;
        this.foodCardinality = 0;
        this.nestCardinality = 0;
        
        vehicleShape = new GeneralPath();
    }
    
    
    //  ---------- properties
    
    public Boolean getState() { return state; }
    public void setState(Boolean state) { this.state = state; }

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
    public void setLocation(MVector location) { this.location = location; }
    
    public MVector getVelocity() { return velocity; }
    public void setVelocity(MVector velocity) { this.velocity = velocity; }
    
    public MVector getBounds() { return bounds; }
    public void setBounds(MVector _bounds) { this.bounds = _bounds; }
    
    public GeneralPath getVehicleShape() { return vehicleShape; }
    public void setMoverShape(GeneralPath vehicleShape) { this.vehicleShape = vehicleShape; }
    
    public MVector getAcceleration() { return acceleration; }
    public void setAcceleration(MVector acceleration) { this.acceleration = acceleration; }
    
    
  
    // ------ behaviors
    
    public void Wander(){
        // calculate the target as a random point on a circle in front of the vehicle
        
        MVector normilizedSpeed = new MVector(velocity.getX()+0.00001, velocity.getY()-0.00001);
        normilizedSpeed.Normalize();
        normilizedSpeed.Multiply(100);
        
        pointOnCircleAngle += r.nextDouble() -0.5;  
        MVector pointOnCircle = new MVector(pointOnCircleAngle);
        pointOnCircle.Multiply(60);
        normilizedSpeed.Add(pointOnCircle);
        normilizedSpeed.Add(location);
        
        MVector desired = MVector.Subtract(normilizedSpeed, location);
        desired.Limit(maxSpeed);

        steer = MVector.Subtract(desired, velocity);
        steer.Limit(maxForce);
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
        //this.Update();
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
    
    public boolean KeepInsideBoundaries(){
        MVector desired = null;
        
        if (location.getX()>bounds.getX()-40){  
            desired = new MVector(-maxSpeed, velocity.getY());
            //location.setX(50);
            //velocity.setX(velocity.getX() * -1);
        }
        else{
            if(location.getX()<40){
                desired = new MVector(maxSpeed, velocity.getY());
                //location.setX(bounds.getX()-50);
                //velocity.setX(velocity.getX() * -1);
            }
        }
        if (location.getY()>bounds.getY()-40){
            desired = new MVector(velocity.getX(), -maxSpeed);
            //location.setY(50);
            //velocity.setY(velocity.getY() * -1);
        }
        else{
            if(location.getY()<40){
                desired = new MVector(velocity.getX(), maxSpeed);
                //location.setY(bounds.getY()-50);
                //velocity.setY(velocity.getY() * -1);
            }
        }
        
        if (desired != null) {
            
            desired.Normalize();
            desired.Multiply(maxSpeed);

            steer = MVector.Subtract(desired, velocity);
            steer.Limit(maxForce);
            this.ApplyForce(steer);
            
            return true;
        }
        return false;
    }
    
    public void Update(){
        this.updateVehicleShape();
        velocity.Add(acceleration);
        velocity.Limit(maxSpeed);
        location.Add(velocity);
        acceleration.Multiply(0);
    }
    
    private void updateVehicleShape(){
        vehicleShape.reset();
        vehicleShape.moveTo(location.getX() , location.getY() + 1.5 * vehicleScale);
        vehicleShape.lineTo(location.getX() + vehicleScale, location.getY() - vehicleScale );
        vehicleShape.lineTo(location.getX() - vehicleScale, location.getY() - vehicleScale);
        vehicleShape.closePath();
        AffineTransform rat = new AffineTransform();
        angle = -1 * Math.atan2(velocity.getX(), velocity.getY()) ;
        rat.rotate(angle, location.getX(), location.getY());
        vehicleShape.transform(rat);
    }
    
    private void ApplyForce(MVector force){
        this.acceleration.Add(force);
    }
    
    
    // ------- cardinality
    
    public void becomeBeacon() {
        this.state = false;
        this.foodCardinality = 0;
        this.nestCardinality = 0;
    }
    
    public void becomeWalker() {
        this.state = true;
    }
}