import {makeAutoObservable} from "mobx";
import type {CATEGORIES} from "../types/categories.ts";

class CategoryStore {
    categories: { name: CATEGORIES; displayName: string }[] = [
        { name: "KOREAN_FOOD", displayName: "한식" }
    ];

    constructor() {
        makeAutoObservable(this);
    }

    setCategories(categories: { name: string; displayName: string }[]) {
        this.categories = categories.slice(0, 2); // 최대 2개까지 허용
    }
}

export default new CategoryStore();

