/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

/**
 *
 * @author pashathebeast
 */
public class Nest {
    // ----- constants
    private final int DEFAULT_SIZE = 20;
    private final int MAX_FOOD_AMOUNT = 100;
    
    // ----- fields
    private MVector location;
    private int foodAmount;
    private int size;
    private GeneralPath shape;
    
    // ----- properties
    public MVector getLocation() { return location; }
    public void setLocation(MVector location) { this.location = location; this.updateShape(); }
    
    public GeneralPath getShape() { return shape; }
    public void setShape(GeneralPath shape) { this.shape = shape; }
        
    public int getFoodAmount() { return foodAmount; }
    public void setFoodAmount(int foodCount) { this.foodAmount = foodCount; }
    
    public int maxFoodAmount() {
        return MAX_FOOD_AMOUNT;
    }
    
    // ----- initializers
    public Nest(MVector _location) {
        // set location
        this.location = _location;
        // and shape
        this.shape = new GeneralPath();
        this.updateShape();
        this.foodAmount = 0;
    }

    // ------- drawing
    public void paintSelf(Graphics2D g) {
        g.setColor(Color.RED);
        g.fill(this.getShape());
        
        
        g.setColor(Color.WHITE);
        double rate = (double)this.foodAmount/(double)MAX_FOOD_AMOUNT;
        g.fillOval((int)(this.location.getX() - (rate*DEFAULT_SIZE)), (int)(this.location.getY() - (rate*DEFAULT_SIZE)), 
                    (int)(DEFAULT_SIZE*2*rate), (int)(DEFAULT_SIZE*2*rate));
    }
    
    private void updateShape(){
        this.shape.reset();
        this.shape.moveTo(this.location.getX() - DEFAULT_SIZE , this.location.getY() -DEFAULT_SIZE);
        this.shape.lineTo(this.location.getX() + DEFAULT_SIZE, this.location.getY() - DEFAULT_SIZE);
        this.shape.lineTo(this.location.getX() + DEFAULT_SIZE, this.location.getY() + DEFAULT_SIZE);
        this.shape.lineTo(this.location.getX() - DEFAULT_SIZE, this.location.getY() + DEFAULT_SIZE);
        this.shape.closePath();
    }
    
}
