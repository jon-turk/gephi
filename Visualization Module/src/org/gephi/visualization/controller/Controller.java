/*
Copyright 2008-2011 Gephi
Authors : Antonio Patriarca <antoniopatriarca@gmail.com>
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.gephi.visualization.controller;


import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import org.gephi.lib.gleem.linalg.Mat4f;
import org.gephi.lib.gleem.linalg.Vec3f;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.project.api.WorkspaceListener;
import org.gephi.visualization.api.MotionManager;
import org.gephi.visualization.api.selection.CameraBridge;
import org.gephi.visualization.api.selection.SelectionManager;
import org.gephi.visualization.api.selection.Shape;
import org.gephi.visualization.camera.Camera;
import org.gephi.visualization.geometry.AABB;
import org.openide.util.Lookup;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class Controller implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private Camera camera;
    private CameraBridge cameraBridge;
    private MotionManager motionManager;

    private static Controller instance;

    private Dimension viewSize;

    private Shape shape;

    private boolean centerGraph = true;
    private boolean centerZero;

    private Controller() {
        // Random values
        this.camera = new Camera(300, 300, 100f, 10000.0f);
        this.motionManager = new MotionManager3D();
        this.viewSize = new Dimension();

        Lookup.getDefault().lookup(ProjectController.class).addWorkspaceListener(new WorkspaceListener() {
            @Override
            public void initialize(Workspace workspace) {
            }

            @Override
            public void select(Workspace workspace) {
                Lookup.getDefault().lookup(SelectionManager.class).initialize();
            }

            @Override
            public void unselect(Workspace workspace) {
            }

            @Override
            public void close(Workspace workspace) {
            }

            @Override
            public void disable() {
            }
        });

        this.cameraBridge = new CameraBridge() {
            @Override
            public Mat4f viewMatrix() {
                return Controller.this.camera.viewMatrix();
            }

            @Override
            public Mat4f projectiveMatrix() {
                return Controller.this.camera.projectiveMatrix();
            }
            
            @Override
            public Point projectPoint(float x, float y, float z) {
                return Controller.this.camera.projectPoint(x, y, z);
            }
        };

    }

    public synchronized static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public void resize(int width, int height) {
        this.viewSize = new Dimension(width, height);
        this.camera.setImageSize(viewSize);
    }

    public Dimension getViewDimensions() {
        return viewSize;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public CameraBridge getCameraBridge() {
        return this.cameraBridge;
    }

    public MotionManager getMotionManager() {
        return motionManager;
    }

    public void beginUpdateFrame() {
    }

    public void endUpdateFrame(AABB box) {
        if (box == null) {
            return;
        }
        if (centerGraph) {
            final Vec3f center = box.center();
            final Vec3f scale = box.scale();
            final Vec3f minVec = box.minVec();
            final Vec3f maxVec = box.maxVec();

            float d = scale.y() / (float)Math.tan(0.5 * camera.fov());

            final Vec3f origin = new Vec3f(center.x(), center.y(), minVec.z() - d*1.1f);
            camera.lookAt(origin, center, Vec3f.Y_AXIS);
            //camera.setClipPlanes(d, maxVec.z() - minVec.z() + d*1.2f);
            centerGraph = false;
        }
        if (centerZero) {

            centerZero = false;
        }
    }

    /**
     * Sets the shape to be drawn for current frame. Only the last shape will
     * be drawn.
     */
    public void setShape(Shape shape) {
        this.shape = shape;
    }

    /**
     * Gets shape to be drawn on screen and clears the buffer.
     */
    public Shape getSelectionShape() {
        Shape s = this.shape;
        this.shape = null;
        return s;
    }

    public void beginRenderFrame() {
    }

    public void endRenderFrame() {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        motionManager.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        motionManager.mouseReleased(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        motionManager.mouseDragged(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        motionManager.mouseMoved(e);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        motionManager.mouseWheelMoved(e);
    }

    public void centerOnGraph() {
        centerGraph = true;
    }

    public void centerOnZero() {
        centerZero = true;
    }

}
