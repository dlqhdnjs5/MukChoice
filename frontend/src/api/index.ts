import axios from "axios";
import { getCookie } from "../utils/cookieUtils";
import type {NavigateFunction} from "react-router";
import {URL_WHITE_LIST} from "../consts/configInfos.ts";

let navigate: NavigateFunction | null = null;
export function setNavigate(fn: NavigateFunction) {
    navigate = fn;
}

export const API = axios.create({
    /*...(import.meta.env.DEV
        ? {}
        : { baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080' }
    ),*/
    headers: {
        "Content-Type": "application/json",
    },
    timeout: 35000,
    timeoutErrorMessage: 'time out!'
})

API.defaults.withCredentials = true;

API.interceptors.request.use(function (config) {
    const accessToken = getCookie(import.meta.env.VITE_MUKCHOICE_X_TOKEN)
    const requestUrl = config.url || '';
    const isWhiteListed = URL_WHITE_LIST.some((whiteUrl) => requestUrl.startsWith(whiteUrl));

    if (!accessToken && !isWhiteListed && navigate) {
        alert('세션이 만료되어. 로그인 페이지로 이동합니다.');
        navigate('/login', { replace: true });
        return Promise.reject(new axios.Cancel('No token, redirect to login'));
    }

    return config;
}, function (error) {
    return Promise.reject(error);
});
