import {useState, useEffect} from "react";
import RaceTable from "./RaceTable.jsx";
import RaceForm from "./RaceForm.jsx";
import "./RaceApp.css";
import {GetRaces, AddRace, DeleteRace, UpdateRace} from "./utils/rest.js";

export default function RaceApp() {
    const [races, setRaces] = useState([]);

    function addFunc(race) {
        console.log("[RaceApp] Add race: " + JSON.stringify(race));

        AddRace(race)
            .then(() => GetRaces())
            .then(_races => setRaces(_races))
            .catch(error => console.log("Error adding a race: " + error));
    }

    function deleteFunc(id) {
        console.log("[RaceApp] Delete race with id: " + id);

        DeleteRace(id)
            .then(() => GetRaces())
            .then(_races => setRaces(_races))
            .catch(error => console.log("Error adding a race: " + error));
    }

    function updateFunc(race) {
        console.log("[RaceApp] Race to update: " + JSON.stringify(race));

        UpdateRace(race)
            .then(() => GetRaces())
            .then(_races => setRaces(_races))
            .catch(error => console.log("Error adding a race: " + error));
    }

    useEffect(()=>{
        console.log('[RaceApp] Use effect called.');
        GetRaces().then(_races=>setRaces(_races));},[]);

    return (<div className="RaceApp">
        <h1> Motorcycle Contest - Races </h1>
        <RaceForm addFunc={addFunc}/>
        <br/>
        <br/>
        <RaceTable races={races} updateFunc={updateFunc} deleteFunc={deleteFunc}/>
    </div>);
}
