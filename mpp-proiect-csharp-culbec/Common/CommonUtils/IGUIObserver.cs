namespace Common.CommonUtils;

public interface IGuiObserver : IObserver
{
    /// <summary>
    /// Shuts down the GUI if requested.
    /// </summary>
    void ShutdownGui();
}