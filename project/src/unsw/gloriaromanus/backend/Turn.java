package unsw.gloriaromanus.backend;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Turn implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int turnNum = 0;
    private ArrayList<TurnListener> obs;
    //Listeners that will not be serialized
    private transient ArrayList<TurnListener> weakListeners;

    public Turn() {
        obs = new ArrayList<TurnListener>();
        weakListeners = new ArrayList<TurnListener>();
    }

    // Used for debugging
    public void setTurn(int turnNum) {
        this.turnNum = turnNum;
        notifyObservers();
    }

    public int increment() {
        turnNum++;
        notifyObservers();
        return turnNum;
    }

    /**
     * @return the turnNum
     */
    public int getTurnNum() {
        return turnNum;
    }

    public void attach(TurnListener o) {
        obs.add(o);
    }

    public void weaklyAttach(TurnListener o) {
        weakListeners.add(o);
    }

    public void detach(TurnListener o) {
        obs.remove(o);
        weakListeners.remove(o);
    }

    public void notifyObservers() {
        var i = obs.iterator();
        while (i.hasNext()) {
            // Remove observer if requested.
            if (i.next().onTurnChange(this)) i.remove();
        }

        i = weakListeners.iterator();
        while (i.hasNext()) {
            // Remove observer if requested.
            if (i.next().onTurnChange(this)) i.remove();
        }
    }


    /**
     * Custom deserialization function for this class. Used by 
     * ObjectInputStream.readObject().
     * @param in ObjectStream to read from
     * @throws IOException  if the class of a serialized object could not be found.
     * @throws ClassNotFoundException if an I/O error occurs.
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        weakListeners = new ArrayList<TurnListener>();
    }
}