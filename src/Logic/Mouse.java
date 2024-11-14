package Logic;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mouse extends MouseAdapter
{
    public static int x, y;
    public static boolean pressed;

    /**
     * Called when the mouse is pressed. Sets the pressed flag to true.
     *
     * @param e The MouseEvent triggered by the mouse press.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        pressed = true;

    }

    /**
     * Called when the mouse is released. Sets the pressed flag to false.
     *
     * @param e The MouseEvent triggered by the mouse release.
     */
    @Override
    public void mouseReleased(MouseEvent e)
    {
        pressed = false;
    }

    /**
     * Called when the mouse is dragged. Updates the current mouse cursor position to the
     * new x and y coordinates.
     *
     * @param e The MouseEvent triggered by the mouse drag.
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        x = e.getX();
        y = e.getY();
    }

    /**
     * Called when the mouse is moved. Updates the current mouse cursor position to the
     * new x and y coordinates.
     *
     * @param e The MouseEvent triggered by the mouse movement.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }


}
