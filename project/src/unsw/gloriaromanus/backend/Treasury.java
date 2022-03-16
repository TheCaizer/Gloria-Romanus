package unsw.gloriaromanus.backend;

import java.io.IOException;
import java.io.Serializable;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Stores the wealth of a faction. Allows faction to make purchases of units
 * or buildings. Could be observed to check for win conditions. 
 */
public class Treasury implements Serializable { //implements Observable 
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private transient IntegerProperty balance;

    public Treasury(int balance) {
        this.balance = new SimpleIntegerProperty(balance);
    }

    public int getBalance() {
        if (balance.get() < 0) {
            // Should never happen
            balance.set(0);
        }
        return balance.get();
    }

    public boolean remove(int gold) {
        if (gold > balance.get()) return false; // ensure balance stays positive
        balance.set(balance.get() - gold);
        balance.subtract(gold);
        return true;
    }

    public void add(int gold) {
        if (gold >= 0) balance.set(balance.get() + gold);

    }

    public IntegerProperty getBalanceProperty() {
        return balance;
    }


        /**
     * Custom serialization function for this class. Used by 
     * ObjectOutputStream.writeObject().
     * @param out ObjectStream to write to.
     * @throws IOException if I/O errors occur while writing to the underlying OutputStream
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        // Taking faction out of property and serializing
        out.writeObject(balance.get());
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
        
        balance = new SimpleIntegerProperty((int) in.readObject());
    }
}