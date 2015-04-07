/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clock;

import GamePanel.GamePanel;
import java.awt.Color;
import math.geom2d.Point2D;

/**
 * The <b>Face</b> class draws a clock's "face."
 */
public class Face extends GamePanel {
    // global constants
    private final int X_CENTER = 0;        // the x-component of the clock hands' center of rotation
    private final int Y_CENTER = 600;      // the y-component of the clock hand's center of rotation
    private final int CLOCK_RADIUS = 500;  // the radius of the "clock" face
    
    // fields
    private int ballRadius;
    
    private double d;                   // the distance between the clock's center and the center
                                        // of the ball
    
    private double thet0;               // the "seconds" hand's start angle in "radians driven"
    private double thetf;               // the angle at which to stop "ticking" (in radians)
    private double theta;               // the current "angle covered" by the "seconds" hand
    private double alpha;               // the angle between P->Q (center to ball's center) and
                                        // the beginning of our simulation (north = 0 radians)
    
    private final double omega;         // the angular velocity of the END of the "seconds" hand
                                        // omega = delta theta / delta time
    
    // object references
    private Point2D P;                  // the clock's center of rotation

    private Point2D Q;                  // the incurring "ball" which we are trying to "sweep"
                                        // with the "seconds" hand, if it is "hittable"
    
    private Point2D R;                  // the rotating end-point of the "seconds" hand
    
    private Case caseTop;               // a reference to the calling object
                                        // (itself derived from a JFrame object)
    
    public Face(){
        // call the two-argument constructor (the parameters are a reference to this
        // and the time period)
        this(null, 0L, 0.0F, 0.0F, null, 0);
    }

    public Face(Case face, long period, double w, double a, Point2D bc, int br) {
        this.period = period;
        
        
        thet0 = a*Math.PI/180 - Math.PI/2;  // have angle from user with respect
                                            // to "12-noon" as the zero angle, so
                                            // subtract his or her angle from 90 degrees
        
        thetf = 0.0F;
        
        theta = 0.0F;  // start with the "seconds" hand at "zero"
        
        omega = w * Math.PI/180;  // negative velocity rolls clockwise
        

        // set up the clock's center of rotation
        P = new Point2D(X_CENTER, Y_CENTER);
        
        // load in the ball's center (previously received from the user)
        Q = bc;
        
        // set up the "seconds" hand's end point or "tip"
        R = new Point2D();
        
        ballRadius = br;
        
        // the distance traveled between the clock's center and the ball's center
        d = Point2D.distance(P, Q);

        // compute alpha (the true angle of the line PQ in our system, in radians)
        alpha = Math.asin(Q.x()/d)-Math.PI/2;
        
        // let's hold on to a copy of the Case object received as a parameter
        caseTop = face;
    }


    private String angularCollisionLineCircle() {
    
        if (d > CLOCK_RADIUS+ballRadius)
            return "no collision";
        
        if (d < ballRadius)
            return "embedded";
        
        if (alpha > omega)
            return "no collision";
        
        if (d*d < CLOCK_RADIUS*CLOCK_RADIUS + ballRadius*ballRadius) {
            double ballAngularDistance = Math.asin(ballRadius/d);
            return Double.toString(alpha-ballAngularDistance);  // normal hit
        } else {
            double ballAngularDistance = Math.acos((CLOCK_RADIUS*CLOCK_RADIUS + d*d- ballRadius*ballRadius)/(2*CLOCK_RADIUS*d));
            return Double.toString(alpha-ballAngularDistance);  // eccentric hit
        }
    }


    @Override
    public void customizeGameUpdate() {       
        // let the player know that an update is being run
        System.out.printf("t = %d, alpha = %.3f, theta = %.3f, ", super.getTimeSpentInGame(), alpha, theta);

        // w = theta / t   => theta = w*t
        theta = thet0 + omega * super.getTimeSpentInGame();

        // rotate the rod a few radians... (w = theta / t)
        R = new Point2D(X_CENTER+CLOCK_RADIUS*Math.cos(theta), Y_CENTER+CLOCK_RADIUS*Math.sin(theta));
        
        // count down to a "hit"
        System.out.printf("%s%n", angularCollisionLineCircle());
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
                dbg.drawOval((int)(P.x()-CLOCK_RADIUS), (int)(P.y()-CLOCK_RADIUS), CLOCK_RADIUS*2, CLOCK_RADIUS*2);

                // draw the red sweeping "seconds" hand
                dbg.setColor(Color.red);
                dbg.drawLine((int)P.x(), (int)P.y(), (int)R.x(), (int)R.y());
                
                // draw the placed ball
                dbg.setColor(Color.green);
                dbg.drawOval((int)(Q.x()-ballRadius), (int)(Q.y()-ballRadius), ballRadius*2, ballRadius*2);
                
                // draw the blue line PQ (traveling from the clock's center to the ball's center)
                dbg.setColor(Color.blue);
                dbg.drawLine((int)P.x(), (int)P.y(), (int)Q.x(), (int)Q.y());
        }
    }
}
