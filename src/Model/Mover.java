/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.Random;

/**
 *
 * @author pashathebeast
 */
public class Mover {
    private MVector location;
    private MVector velocity;
    private MVector acceleration;
    private MVector bounds;
    private Random r;
    private float hue;
    private double angle;
    private GeneralPath moverShape;
    
    public Mover(){
        location = new MVector(0,0);
        velocity = new MVector(0,0);
        bounds = new MVector(0,0);
        acceleration = new MVector(0,0);
        r = new Random();
        hue = r.nextFloat();
        moverShape = new GeneralPath();
        moverShape.moveTo(-5, 5);
        moverShape.lineTo(0, 10);
        moverShape.lineTo(-5, -5);
        moverShape.closePath();
    }
    
    public Mover(MVector _location, MVector _velocity, MVector _acceleration, MVector _bounds, float _hue){
        location = _location;
        velocity = _velocity;
        bounds = _bounds;
        acceleration = _acceleration;
        r = new Random();
        hue = _hue;
        moverShape = new GeneralPath();
        moverShape.moveTo(location.getX() - 5, location.getY() - 5);
        moverShape.lineTo(location.getX() + 10, location.getY());
        moverShape.lineTo(location.getX() - 5, location.getY() + 5);
        moverShape.closePath();
    }

    public MVector getLocation() { return location; }
    public void setLocation(MVector location) { this.location = location; }
    
    public float getHue() { return hue; }
    public void setHue(float _hue) { this.hue = _hue; }
    
    public MVector getVelocity() { return velocity; }
    public void setVelocity(MVector velocity) { this.velocity = velocity; }
    
    public MVector getBounds() { return bounds; }
    public void setBounds(MVector _bounds) { this.bounds = _bounds; }
    
    public GeneralPath getMoverShape() { return moverShape; }
    public void setMoverShape(GeneralPath moverShape) { this.moverShape = moverShape; }
    
    public MVector getAcceleration() { return acceleration; }
    public void setAcceleration(MVector acceleration) { this.acceleration = acceleration; }
    
    public void Update(MVector mouseLocation){
        //getting the random acceleration vector
        /*
        if (r.nextBoolean() == true) {
            acceleration.setX(r.nextDouble()*-1);
        }
        else{
            acceleration.setX(r.nextDouble());
        }
        if (r.nextBoolean() == true) {
            acceleration.setY(r.nextDouble()*-1);
        }
        else{
            acceleration.setY(r.nextDouble());
        }
        */
        AffineTransform rat = new AffineTransform();
        //rat.setToTranslation(angle, angle);
        //rat.setToTranslation(location.getX(), location.getY());
        //rat.getRotateInstance(angle, location.getX(), location.getY());
        
        //rat.translate(location.getX(), location.getY());
        moverShape.reset();
        moverShape.moveTo(location.getX() , location.getY() + 15);
        moverShape.lineTo(location.getX() + 5, location.getY() - 10 );
        moverShape.lineTo(location.getX() - 5, location.getY() - 10);
        moverShape.closePath();
        angle = -1 * Math.atan2(velocity.getX(), velocity.getY()) ;
        rat.rotate(angle, location.getX(), location.getY());
        getMoverShape().transform(rat);

        
        setAcceleration(MVector.Subtract(mouseLocation, location));
        getAcceleration().Limit(r.nextDouble());
        
        
        velocity.Add(getAcceleration());
        velocity.Limit(r.nextInt(10)+5);
        location.Add(velocity);
        
        
        
        if (location.getX()>bounds.getX()){  
            location.setX(0);
        }
        else{
            if(location.getX()<0){
                location.setX(bounds.getX());
            }
        }
        if (location.getY()>bounds.getY()){
            location.setY(0);
        }
        else{
            if(location.getY()<0){
                location.setY(bounds.getY());
            }
        }
    }




    
    
}
