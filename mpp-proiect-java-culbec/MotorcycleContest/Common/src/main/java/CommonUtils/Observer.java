package CommonUtils;

import Exceptions.AppException;
import Model.Participant;

public interface Observer {
    /**
     * Notifies the observer that a new participant was added.
     * @param participant Participant that was added.
     * @throws AppException If the observer encountered a problem in updating itself.
     */
    void participantAdded(Participant participant) throws AppException;
}
