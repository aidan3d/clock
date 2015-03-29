/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clock;

import GamePanel.GamePanel;

/**
 * The <b>ClockPanel</b> class draws a clock's
 * "face."
 */
public class ClockPanel extends GamePanel {
    public ClockPanel(){
        // call the two-argument constructor
        // (the parameters are a reference to this and the time period)
        this(null, 0L);
    }
    
    public ClockPanel(Clock face, long periodBetweenFrames) {
    }

    @Override
    public void customizeGameUpdate() {
        
        // Let the player know that an update is
        // being run.
        System.out.println("Update...");
    }
    
    @Override
    public void customizeGameRender() {
    
    }
    
    
}
