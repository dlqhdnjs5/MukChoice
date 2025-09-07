import { makeAutoObservable } from 'mobx'
class AppStore {
    count = 0
    constructor() {
        makeAutoObservable(this)
    }
    increment = () => this.count++
}
export const appStore = new AppStore()