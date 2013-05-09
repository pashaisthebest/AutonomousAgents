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
    private static final int DEFAULT_SIZE = 20;
    
    // ----- fields
    private MVector location;
    private int size;
    private GeneralPath shape;
    
    // ----- properties
    public MVector getLocation() { return location; }
    public void setLocation(MVector location) { this.location = location; this.updateShape(); }
    
    public GeneralPath getShape() { return shape; }
    public void setShape(GeneralPath shape) { this.shape = shape; }
    
    // ----- initializers
    public Nest(MVector _location) {
        // set location
        this.location = _location;
        // and shape
        this.shape = new GeneralPath();
        this.updateShape();
    }

    // ------- drawing
    public void paintSelf(Graphics2D g) {
        g.setColor(Color.RED);
        g.fill(this.getShape());
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
