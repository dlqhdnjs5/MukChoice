import { makeAutoObservable } from "mobx";

export interface SelectedDongInfo {
    dong: string;
    bcode: string;
}

class PlaceStore {
    selectedDong: SelectedDongInfo = {
        dong: "전체",
        bcode: "ALL"
    };

    constructor() {
        makeAutoObservable(this);
    }

    setSelectedDong(dongInfo: SelectedDongInfo) {
        this.selectedDong = dongInfo;
    }
}

export default new PlaceStore();

