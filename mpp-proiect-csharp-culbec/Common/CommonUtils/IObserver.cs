using Model;

namespace Common.CommonUtils;

public interface IObserver
{
    /// <summary>
    /// Notifies the observer that a participant has been added.
    /// </summary>
    /// <param name="participant">Participant that was added.</param>
    void ParticipantAdded(Participant participant);
}