import {useState} from "react";
import PropTypes from "prop-types";

export default function RaceForm({addFunc}) {
    const [engineCapacity, setEngineCapacity] = useState(0);

    function handleEngineCapacityChange(event) {
        setEngineCapacity(event.target.value);
    }

    function handleAdd(event) {
        let race = {
            engineCapacity: engineCapacity
        };
        console.log("[RaceForm] A race was submitted: " + JSON.stringify(race));

        addFunc(race);
        event.preventDefault();
    }

    return (
        <div className="RaceForm">
            <form>
                <span>
                    <label htmlFor="engine-capacity-input">
                        Engine Capacity:
                    </label>
                    <input id="engine-capacity-input" type="text" value={engineCapacity}
                           onChange={event => handleEngineCapacityChange(event)}/>
                </span>
                <span>
                    <button onClick={handleAdd}>Add race</button>
                </span>
            </form>
        </div>);
}

RaceForm.propTypes = {
    addFunc: PropTypes.func.isRequired
}