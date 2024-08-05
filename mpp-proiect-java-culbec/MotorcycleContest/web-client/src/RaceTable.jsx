import {useState} from "react";
import PropTypes from "prop-types";

function RaceRow({race, updateFunc, deleteFunc}) {
    const [engineCapacity, setEngineCapacity] = useState(race.engineCapacity);

    function handleUpdate() {
        race.engineCapacity = engineCapacity;
        console.log("[RaceTable] Update race: " + JSON.stringify(race));
        updateFunc(race);
    }

    function handleDelete() {
        console.log("[RaceTable] Delete race: " + race.id);
        deleteFunc(race.id);
    }

    function handleEngineCapacityChange(event) {
        setEngineCapacity(event.target.value);
    }

    return (
        <tr>
            <td><input type={"text"} value={engineCapacity} onChange={event => handleEngineCapacityChange(event)}/></td>
            <td>{race.noParticipants}</td>
            <td>
                <button onClick={handleUpdate}>Update</button>
                <button onClick={handleDelete}>Delete</button>
            </td>
        </tr>
    );
}

RaceRow.propTypes = {
    race: PropTypes.shape({
        id: PropTypes.number,
        engineCapacity: PropTypes.number,
        noParticipants: PropTypes.number
    }).isRequired,
    updateFunc: PropTypes.func.isRequired,
    deleteFunc: PropTypes.func.isRequired,
}

export default function RaceTable({races, updateFunc, deleteFunc}) {
    let rows = [];
    races.forEach(function(race) {
        rows.push(<RaceRow race={race} key={race.id} updateFunc={updateFunc} deleteFunc={deleteFunc}/> )
    })

    return(
        <div className={"RaceTable"}>
            <table className={"center"}>
                <thead>
                <tr>
                    <th>Engine Capacity</th>
                    <th>No. Participants</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>{rows}</tbody>
            </table>
        </div>
    )
}

RaceTable.propTypes = {
    races: PropTypes.arrayOf(PropTypes.shape({
        id: PropTypes.number,
        engineCapacity: PropTypes.number,
        noParticipants: PropTypes.number
    })).isRequired,
    updateFunc: PropTypes.func.isRequired,
    deleteFunc: PropTypes.func.isRequired,
}