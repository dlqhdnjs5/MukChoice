import type { Location } from "../types/location.ts";
import {makeAutoObservable} from "mobx";

class LocationStore {
    currentLocation: Location | null = null;

    constructor() {
        makeAutoObservable(this)
    }

    setCurrentLocation(location: Location | null) {
        this.currentLocation = location;
    }
}

export default new LocationStore();

