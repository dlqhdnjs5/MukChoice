import { makeAutoObservable } from "mobx";
import type {User} from "../types/user.ts";
import { getCookie, deleteCookie } from "../utils/cookieUtils.ts";
import { API } from "../api/index.ts";

class UserStore {
  user: User | null = null;
  isInitialized: boolean = false;

  constructor() {
    makeAutoObservable(this)
  }

  setUser(user: User) {
    this.user = user;
  }

  clearUser() {
    this.user = null;
  }

  // 로그아웃 시 모든 사용자 데이터 정리
  logout() {
    this.clearUser();
    this.isInitialized = false;
  }

  get isLoggedIn() {
    return !!this.user;
  }

  async initializeUser() {
    if (this.isInitialized) {
      return;
    }

    try {
      const token = getCookie(import.meta.env.VITE_MUKCHOICE_X_TOKEN);
      if (token) {
        const response = await API.get('/api/users/me');
        const userData: User = response.data;
        this.setUser(userData);
      }
    } catch (error) {
      console.error('사용자 정보 복원 실패:', error);
      deleteCookie(import.meta.env.VITE_MUKCHOICE_X_TOKEN);
    } finally {
      this.isInitialized = true;
    }
  }
}

export default new UserStore();
