/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.awt.geom.GeneralPath;

/**
 *
 * @author pashathebeast
 */
public class ObstacleSensor {
    
    // the path that represents the sensor
    private GeneralPath shape;
    private Boolean state; // 0 - not triggered, 1- triggered
    
    public GeneralPath getShape() { return shape; }
    public void setShape(GeneralPath shape) { this.shape = shape; }
    
    public Boolean getState() { return state; }
    public void setState(Boolean state) { this.state = state; }
    
    public ObstacleSensor(GeneralPath _shape) {
        shape = _shape;
        state = false;
    }
    
    public ObstacleSensor() {
        shape = new GeneralPath();
        state = false;
    }


}
