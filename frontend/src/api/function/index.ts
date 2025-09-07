import {API} from "../index.ts";

export const getApi = async (url: string, params?: object) => {
    try {
        if (params) {
            return await API.get(url, { params });
        } else {
            return await API.get(url);
        }
    } catch (e) {
        handleApiError();
        throw e;
    }
}

export const postApi = async (url: string, data?: object) => {
    try {
        if (data) {
            return await API.post(url, data);
        } else {
            return await API.post(url);
        }
    } catch (e) {
        handleApiError();
        throw e;
    }
}

export const putApi = async (url: string, data?: object) => {
    try {
        if (data) {
            return await API.put(url, data);
        } else {
            return await API.put(url);
        }
    } catch (e) {
        handleApiError();
        throw e;
    }
}

export const deleteApi = async (url: string, data?: object) => {
    try {
        if (data) {
            return await API.delete(url, { data });
        } else {
            return await API.delete(url);
        }
    } catch (e) {
        handleApiError();
        throw e;
    }
}

function handleApiError() {
    alert('시스템 오류가 발생하였습니다, 담당자에게 문의해 주시기 바랍니다.');
}