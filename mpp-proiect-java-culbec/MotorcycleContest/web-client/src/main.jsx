import ReactDOM from 'react-dom/client'
import {DevSupport} from "@react-buddy/ide-toolbox";
import {ComponentPreviews, useInitial} from "./dev/index.js";
import RaceApp from "./RaceApp.jsx";

ReactDOM.createRoot(document.getElementById('root')).render(
    <DevSupport ComponentPreviews={ComponentPreviews}
                useInitialHook={useInitial}
    >
        <RaceApp/>
    </DevSupport>
)
