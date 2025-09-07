import {API} from "../index.ts";

export const fetchHello = async () => {
    const res = await API.get('/api/sample/hello');

    if (res.status !== 200) throw new Error('API 요청 실패');
    return res.data;
};

export const fetchShopImageTest = async () => {
    const res = await API.get('/api/sample/shopImage');

    if (res.status !== 200) throw new Error('API 요청 실패');
    return res.data;
};

export const fetchPlaces = async () => {
    const res = await API.get('/api/places');

    if (res.status !== 200) throw new Error('API 요청 실패');
    return res.data;
};
