/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emre;

/**
 *
 * @author emre
 */

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.gl2.GLUT;
import java.awt.BorderLayout;
import javax.swing.JPanel;

public class GLTD extends JPanel implements GLEventListener, MouseMotionListener, OnNewDataReceived {

    private static final long serialVersionUID = 7376825297884956163L;

    private float rotateX, rotateY;
    private int lastX, lastY;

    public GLTD() {
        setLayout(new BorderLayout());
        setSize(400, 400);
        GLProfile glProfile = GLProfile.getDefault();
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);
        glCapabilities.setDoubleBuffered(true);
        GLCanvas glCanvas = new GLCanvas(glCapabilities);
        
        glCanvas.addGLEventListener(this);
        
        //glCanvas.addMouseMotionListener(this);
        add(glCanvas);
        //addMouseMotionListener(this);
       
        rotateX = 0f;
        rotateY = 0f;
        Animator a = new Animator(glCanvas);
        a.start();
    }

    public float getRotateX() {
        return rotateX;
    }

    public void setRotateX(float rotateX) {
        this.rotateX = rotateX;
    }

    public float getRotateY() {
        return rotateY;
    }

    public void setRotateY(float rotateY) {
        this.rotateY = rotateY;
    }

    public int getLastX() {
        return lastX;
    }

    public void setLastX(int lastX) {
        this.lastX = lastX;
    }

    public int getLastY() {
        return lastY;
    }

    public void setLastY(int lastY) {
        this.lastY = lastY;
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0, 0, 0, 0);
        gl.glColor3f(0.68f, 0.138f, 0.255f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(-1, 1, -1, 1, -2, 2);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        //gl.glShadeModel(GLLightingFunc.GL_FLAT);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        gl.glRotatef(rotateX, 0, 1, 0);
        gl.glRotatef(rotateY, 1, 0, 0);

        GLUT glut = new GLUT();
        glut.glutSolidCylinder(0.1, 0.7, 25, 1000);
        //glut.glutSolidCone(0.1, 0.7, 25, 500);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.8F, 0.8F, 0.8F, 1.0F);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glEnable(GL2.GL_NORMALIZE);
    }

    @Override
    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
    }

    @Override
    public void dispose(GLAutoDrawable arg0) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();   
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        rotateX += e.getX() - lastX;
        rotateY += e.getY() - lastY;
        System.out.println("rotateX"+rotateX);
        System.out.println("rotateY"+rotateY);     
        lastX = e.getX();
        lastY = e.getY();
    }

    @Override
    public void onNewDataReceived(int[] data) {
        rotateX += data[0];
        rotateY += data[1];
    }
}
