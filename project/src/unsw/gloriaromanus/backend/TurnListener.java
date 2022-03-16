package unsw.gloriaromanus.backend;


public interface TurnListener {

    /**
     * Event to perform when the turn is changed.
     * @param obj the turn object being observed.
     * @return true if the TurnListener wishes to be detached after event.
     *         otherwise false.
     */
    public boolean onTurnChange(Turn obj);
}