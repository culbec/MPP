package CommonUtils;

public interface IGUIObserver extends IObserver {
    /**
     * Shuts down the GUI if requested.
     */
    void shutdownGUI();
}
