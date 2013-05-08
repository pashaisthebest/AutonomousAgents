/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author pashathebeast
 */
public class Nest {
    private MVector location;
    
    public MVector getLocation() { return location; }
    public void setLocation(MVector location) { this.location = location; }
    
    public Nest(MVector _location) {
        this.location = _location;
    }
    
}
