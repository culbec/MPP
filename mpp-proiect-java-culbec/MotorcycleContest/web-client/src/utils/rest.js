import {RACE_BASE_URL} from "./consts.js";

function status(response) {
    if (response.status >= 200 && response.status < 300) {
        return Promise.resolve(response)
    } else {
        return Promise.reject(new Error(response.statusText))
    }
}

function json(response) {
    return response.json()
}

function createRequest(method, headers, body, url) {
    let init;

    if (method !== 'GET' && method !== 'HEAD') {
        init = {
            method: method,
            headers: headers,
            mode: 'cors',
            body: body,
        };
    } else {
        init = {
            method: method,
            headers: headers,
            mode: 'cors',
        };
    }

    return new Request(url, init);
}

export function GetRaces() {
    let headers = new Headers();
    headers.append('Accept', 'application/json');

    let request = createRequest('GET', headers, '', RACE_BASE_URL);

    return fetch(request)
        .then(status)
        .then(json)
        .then(response => {
            console.log('[REST - GetRaces] Request succeeded with JSON response', response);
            return response;
        }).catch(error => {
            console.log('Request failed', error);
            return Promise.reject(error);
        })
}

export function AddRace(race) {
    let headers = new Headers();
    headers.append('Accept', 'application/json');
    headers.append('Content-Type', 'application/json');

    let request = createRequest('POST', headers, JSON.stringify(race), RACE_BASE_URL);

    return fetch(request)
        .then(status)
        .then(json)
        .then(response => {
            console.log('[REST - AddRace] Request succeeded with JSON response', response);
            return response;
        }).catch(error => {
            console.log('Request failed', error);
            return Promise.reject(error);
        });
}

export function DeleteRace(id) {
    let headers = new Headers();
    headers.append('Accept', 'application/json');

    let deleteUrl = RACE_BASE_URL + '/' + id;
    let request = createRequest('DELETE', headers, '', deleteUrl);

    return fetch(request)
        .then(status)
        .then(json)
        .then(response => {
            console.log('[REST - DeleteRace] Request succeeded with JSON response', response);
            return response;
        }).catch(error => {
            console.log('Request failed', error);
            return Promise.reject(error);
        });
}

export function UpdateRace(race) {
    let headers = new Headers();
    headers.append('Accept', 'application/json');
    headers.append('Content-Type', 'application/json');

    let request = createRequest('PUT', headers, JSON.stringify(race), RACE_BASE_URL);

    return fetch(request)
        .then(status)
        .then(json)
        .then(response => {
            console.log('[REST - UpdateRace] Request succeeded with JSON response', response);
            return response;
        }).catch(error => {
            console.log('Request failed', error);
            return Promise.reject(error);
        });
}