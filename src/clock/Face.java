/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clock;

import GamePanel.GamePanel;
import java.awt.Color;
import java.util.Scanner;
import math.geom2d.Point2D;

/**
 * The <b>Face</b> class draws a clock's "face."
 */
public class Face extends GamePanel {
    // global constants
    private final int X_CENTER = 400;   // the x-component of the clock hands' center of rotation
    private final int Y_CENTER = 300;   // the y-component of the clock hand's center of rotation
    private final int RADIUS = 250;     // the radius of the "clock" face 
    
    // fields
    private double thet0;               // the "seconds" hand's start angle in "radians driven"
    private double theta;               // the current "angle covered" by the "seconds" hand
    private double alpha;               // the angle between P->Q (center to ball's center) and
                                        //   the beginning of our simulation (north = 0 radians)
    private final double omega;         // the angular velocity of the END of the "seconds" hand
                                        //   omega = delta theta / delta time
    
    // object references
    private Point2D P;                  // the clock's center of rotation
    private Point2D R;                  // the rotating end-point of the "seconds" hand
    private Case caseTop;               // a reference to the calling object
                                        // (itself derived from a JFrame object)
    
    public Face(){
        // call the two-argument constructor (the parameters are a reference to this and the time period)
        this(null, 0L);
    }

    public Face(Case face, long period) {
        this.period = period;
        
        thet0 = -Math.PI/2;
        theta = 0.0F;  // start with the "seconds" hand at "zero"
        alpha = 0.0F;
        omega = Math.PI/30;
        
        // set up the clock's center of rotation
        P = new Point2D(X_CENTER, Y_CENTER);
        
        // set up the "seconds" hand's end point or "tip"
        R = new Point2D();
        
        // let's hold on to a copy of the Case object received as a parameter
        caseTop = face;
    }


    @Override
    public void customizeGameUpdate() {       
        // let the player know that an update is being run
        System.out.printf("t = %d, theta = %f%n", super.getTimeSpentInGame(), theta);
        
        // w = theta / t   => theta = w*t
        theta = thet0 + omega * super.getTimeSpentInGame();
        
        
        // rotate the rod a few radians... (w = theta / t)
        R = new Point2D(X_CENTER+RADIUS*Math.cos(theta), Y_CENTER+RADIUS*Math.sin(theta));
    }

    @Override
    public void customizeGameRender() { 
        // run this method at the "top" of each update/render/sleep game loop
        // check whether the game has ended first (no need for further updates!)
        if (!super.gameOver) {
                // draw the canvas 
                dbg.setColor(Color.white);
                dbg.fillRect(0, 0, super.getWidth(), super.getHeight());

                // draw the clock's bezel
                dbg.setColor(Color.black);
                dbg.drawOval((int)(P.x()-RADIUS), (int)(P.y()-RADIUS), RADIUS*2, RADIUS*2);

                // draw the red sweeping "seconds" hand
                dbg.setColor(Color.red);
                dbg.drawLine((int)P.x(), (int)P.y(), (int)R.x(), (int)R.y());
        }
    }
}
