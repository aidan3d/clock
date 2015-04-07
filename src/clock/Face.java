/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clock;

import GamePanel.GamePanel;
import java.awt.Color;
import math.geom2d.Angle2D;
import math.geom2d.Point2D;

/**
 * The <b>Face</b> class draws a clock's "face,"
 * a "seconds" hand and a ball.
 */
public class Face extends GamePanel {
    //<editor-fold defaultstate="collapsed" desc="Fields">
    // global constants
    private final int X_CENTER = 400;      // the x-component of the clock hand's center of rotation
    private final int Y_CENTER = 300;      // the y-component of the clock hand's center of rotation
    private final int CLOCK_RADIUS = 250;  // the radius of the "clock" face
    
    // fields
    private int ballRadius;             // the ball's radius
    
    private double d;                   // the distance between the clock's center and the
                                        // ball's center
    
    private double thet0;               // the "seconds" hand's start angle in "radians driven"
    private double theta;               // the current "angle covered" by the "seconds" hand
    private double thetf;               // the angle at which to stop "ticking" (in radians)
    private double alpha;               // the angle between P->Q (center to ball's center)
                                        // and the beginning of our simulation
                                        // (north = 0 radians)
    
    private final double omega;         // the angular velocity of the END of the "seconds" hand
                                        // omega = delta theta / delta time
    
    // object references
    private Point2D P;                  // the clock's center of rotation

    private Point2D Q;                  // the incurring "ball" which we are trying to "sweep"
                                        // with the "seconds" hand, if it is "hittable"
    
    private Point2D R;                  // the rotating end-point of the "seconds" hand
    
    private Case caseTop;               // a reference to the calling object
                                        // (itself derived from a JFrame object)
    //</editor-fold>

    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Face(){
        // call the two-argument constructor (the parameters are a reference to this
        // and the time period)
        this(null, 0L, 0.0F, 0.0F, null, 0);
    }

    public Face(Case face, long period, double w, double a, Point2D bc, int br) {
        this.period = period;
        
        
        thet0 = a * Angle2D.M_PI/180;
        
        thetf = Angle2D.M_2PI;
        
        theta = 0.0F;  // start with the "seconds" hand at "zero"
                       // think of theta as "accumulated" radians travelled
        
        omega = w * Angle2D.M_PI/180; // convert from w in degrees to omega as radians
        

        // set up the clock's center of rotation
        P = new Point2D(X_CENTER, Y_CENTER);
        
        // load in the ball's center (previously received from the user)
        Q = bc;
        
        // set up the "seconds" hand's end point or "tip"
        R = new Point2D();
        
        ballRadius = br;
        
        // the distance traveled between the clock's center and the ball's center
        d = Point2D.distance(P, Q);

        // compute alpha (the angle of the line PQ, in radians)
        alpha = Math.acos((Q.x()-P.x())/d);
        
        // let's hold on to a copy of the Case object received as a parameter
        caseTop = face;
    }
    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="Operations">
    /**
     * This method computes the estimated time of impact
     * between the traveling "seconds" hand and the
     * stationary ball.
     * @return either a message indicating a miss,
     * or the number of seconds 'til impact 
     */
    private String angularCollisionLineCircle() {
        if (d > CLOCK_RADIUS+ballRadius) // the ball is outside our clock face
            return "no collision";
        
        
        if (d < ballRadius)  // the ball is too big
            return "embedded";
        
        if (theta > alpha-thet0)   // we have managed to miss the ball by thet0 being too large (n.b.:
            return "no collision"; // the Kodicek/Flynt book diminishes alpha, we'll just take a bite)
        
        if (d*d < CLOCK_RADIUS*CLOCK_RADIUS + ballRadius*ballRadius) {
            double ballAngularDistance = Math.asin(ballRadius/d);
            
            return Double.toString((1/omega) * ((alpha-thet0)-ballAngularDistance));  // normal hit
        } else {
            double ballAngularDistance = Math.acos((CLOCK_RADIUS*CLOCK_RADIUS + d*d- ballRadius*ballRadius)/(2*CLOCK_RADIUS*d));
            return Double.toString((1/omega) * ((alpha-thet0)-ballAngularDistance));  // eccentric hit
        }
    }

    /**
     * This method is run every time the game loop is run.
     * We'll use it to move things around.
     */
    @Override
    public void customizeGameUpdate() {       
        // limit the clock to one rotation (3 o'clock to 3 o'clock postions)
        if (theta < thetf) {
            // let the player know that an update is being run
            System.out.printf("t = %d, omega = %.3f, alpha = %.3f, theta = %.3f, ", super.getTimeSpentInGame(),
                    omega, alpha, theta);

            // w = theta / t   => theta = w*t
            theta = thet0 + omega * super.getTimeSpentInGame();

            // rotate the rod a few radians... (w = theta / t)
            R = new Point2D(X_CENTER+CLOCK_RADIUS*Math.cos(theta), Y_CENTER+CLOCK_RADIUS*Math.sin(theta));

            // count down to a "hit"
            System.out.printf("%s%n", angularCollisionLineCircle());
        }
    }

    /**
     * This method is also run every time the game loop
     * can run it, (i.e. if we're not too far behind on
     * updates). We'll paint things here.
     */
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
    //</editor-fold>
}
