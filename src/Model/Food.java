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
public class  Food {
    // ----- constants
    private static final int DEFAULT_SIZE = 20;
    private static final int DEFAULT_FOOD_AMOUNT = 100;
    
    // ----- fields
    private MVector location;
    private int foodAmount;
    private int size;
    private GeneralPath shape;
    private Boolean isOccupied;
    
    // ----- properties
    public MVector getLocation() { return location; }
    public void setLocation(MVector location) { this.location = location; this.updateShape(); }
    
    public GeneralPath getShape() { return shape; }
    public void setShape(GeneralPath shape) { this.shape = shape; }
    
    public int getFoodAmount() { return foodAmount; }
    public void setFoodAmount(int foodCount) { this.foodAmount = foodCount; }
    
    public Boolean isOccupied() { return isOccupied; }
    public void setOccupied(Boolean isOccupied) { this.isOccupied = isOccupied; }
    
    // ----- initializers
    public Food(MVector _location) {
        // set location
        this.location = _location;
        // and shape
        this.shape = new GeneralPath();
        this.size = DEFAULT_SIZE;
        this.foodAmount = DEFAULT_FOOD_AMOUNT;
        this.isOccupied = false;
        this.updateShape();
    }

    
    // ------- drawing
    public void paintSelf(Graphics2D g) {
        
        if (this.isOccupied) { g.setColor(Color.YELLOW); }
        else { g.setColor(Color.GREEN); }
        g.fill(this.getShape());
        
        g.setColor(Color.WHITE);
        double rate = (double)this.foodAmount/(double)DEFAULT_FOOD_AMOUNT;
        g.fillOval((int)this.location.getX() - (int)(rate*DEFAULT_SIZE), (int)this.location.getY() - (int)(rate*DEFAULT_SIZE), 
                    (int)(DEFAULT_SIZE*2*rate), (int)(DEFAULT_SIZE*2*rate));
    }
    
    private void updateShape(){
        this.shape.reset();
        this.shape.moveTo(this.location.getX() - this.size, this.location.getY() - this.size);
        this.shape.lineTo(this.location.getX() + this.size, this.location.getY() - this.size);
        this.shape.lineTo(this.location.getX() + this.size, this.location.getY() + this.size);
        this.shape.lineTo(this.location.getX() - this.size, this.location.getY() + this.size);
        this.shape.closePath();
    }    

}
