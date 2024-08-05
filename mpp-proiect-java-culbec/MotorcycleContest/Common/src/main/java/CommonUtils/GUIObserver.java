package CommonUtils;

public interface GUIObserver extends Observer {
    /**
     * Shuts down the GUI if requested.
     */
    void shutdownGUI();
}
