package drow.tetrisLikeGame;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;

public class FrameDragListener extends MouseAdapter {

    private final JFrame frame;
    private Point mouseDownCompCoords = null;

    public FrameDragListener(JFrame frame) {
        this.frame = frame;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDownCompCoords = null;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseDownCompCoords = e.getPoint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point currCoords = e.getLocationOnScreen();
        try {
            frame.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
        } catch (Exception ex) {

        }

    }
}