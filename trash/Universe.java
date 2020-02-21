/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emre;

import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.vecmath.Color3f;

import com.sun.j3d.utils.geometry.Cylinder;
import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;
import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 *
 * @author emre
 */
public final class Universe extends JPanel {

    private static final Vector3f YAXIS = new Vector3f(0, 1, 0);
    private float angleX = 0f;
    private float angleY = 0f;
    private float angleZ = 0f;

    public float getAngleX() {
        return angleX;
    }

    public void setAngleX(float angleX) {
        this.angleX = angleX;
    }

    public float getAngleY() {
        return angleY;
    }

    public void setAngleY(float angleY) {
        this.angleY = angleY;
    }

    public float getAngleZ() {
        return angleZ;
    }

    public void setAngleZ(float angleZ) {
        this.angleZ = angleZ;
    }

    int s = 0, count = 0;

    private Canvas3D canvas3D;
    private Cylinder cylinder;
    private SimpleUniverse simpleU;
    private BranchGroup lineGroup;

    int c = 1;

    public Universe(float anglez) {
        setLayout(new BorderLayout());

        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                c++;

                angleZ = (float) (3.14 / c);

                if (c == 6) {
                    c = 1;
                }
            }
        };

        init();
        
        transform((float) (angleZ));

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(t, 500, 500);
    }

    public void init() {
        GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();

        canvas3D = new Canvas3D(gc);//See the added gc? this is a preferred config
        add("Center", canvas3D);

        lineGroup = new BranchGroup();
        ColoringAttributes ca = new ColoringAttributes(new Color3f(0.6f, 0.138f, 1f), ColoringAttributes.ALLOW_COLOR_WRITE);
        Appearance app = new Appearance();

        Color3f color = new Color3f(Color.yellow);
        Color3f blue = new Color3f(0.68f, 0.138f, 0.255f);
        Color3f white = new Color3f(0.3f, 0.3f, 0.3f);

        app.setMaterial(new Material(color, blue, color, white, 90f));

        app.setColoringAttributes(ca);

        cylinder = new Cylinder(0.2f, (float) 1.5f, app);

        Transform3D transform = new Transform3D();

        Transform3D rotate = new Transform3D();

        Transform3D translate1 = new Transform3D();
        translate1.setTranslation(new Vector3d(0.0, 0.0f, 0.0));
        transform.mul(translate1, transform);

        rotate.rotY(0);
        rotate.rotX(0);
        rotate.rotZ(angleZ);

        //rotate.setScale(0.7);
        transform.mul(rotate, transform);

        Transform3D translate2 = new Transform3D();
        translate2.setTranslation(new Vector3d(0.0f, 0.0f, 0.0));
        transform.mul(translate2, transform);

        TransformGroup tg = new TransformGroup();
        tg.setTransform(transform);
        tg.addChild(cylinder);

        lineGroup.addChild(tg);

        lineGroup.compile();

        // SimpleUniverse is a Convenience Utility class
        simpleU = new SimpleUniverse(canvas3D);
        // This moves the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        simpleU.getViewingPlatform().setNominalViewingTransform();

        //canvas3D.setBackground(Color.blue);
        simpleU.addBranchGraph(lineGroup);
    }

    public void transform(float angle) {
        // This moves the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        Appearance app = new Appearance();

        Color3f color = new Color3f(Color.blue);
        Color3f blue = new Color3f(0.68f, 0.138f, 0.255f);
        Color3f white = new Color3f(0.3f, 0.3f, 0.3f);

        app.setMaterial(new Material(color, blue, color, white, 100f));
        
        cylinder = new Cylinder(0.3f, (float) 1.5f, app);
        lineGroup = new BranchGroup();

        Transform3D transform = new Transform3D();

        Transform3D rotate = new Transform3D();

        Transform3D translate1 = new Transform3D();

        translate1.setTranslation(new Vector3d(0.0, 0.0f, 0.0));
        transform.mul(translate1, transform);

        rotate.rotX(0);
        rotate.rotY(angle);
        rotate.rotZ(angle);

        transform.mul(rotate, transform);

        Transform3D translate2 = new Transform3D();
        translate2.setTranslation(new Vector3d(0.0f, 0.0f, 0.0));
        transform.mul(translate2, transform);

        //rotate.setScale(0.1);
        TransformGroup tg = new TransformGroup();

        tg.setTransform(transform);

        tg.addChild(cylinder);

        lineGroup.addChild(tg);

        simpleU.addBranchGraph(lineGroup);
    }
}
