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
public class Obstacle {
        // the path that represents the sensor
    private GeneralPath shape;
    
    public GeneralPath getShape() { return shape; }
    public void setShape(GeneralPath shape) { this.shape = shape; }
    
    public Obstacle(GeneralPath _shape) {
        shape = _shape;
    }
    
    public Obstacle() {
        shape = new GeneralPath();
    }
    
    public void paintSelf(Graphics2D g) {
        g.setColor(Color.BLUE);
        g.fill(this.getShape());
    }
}
