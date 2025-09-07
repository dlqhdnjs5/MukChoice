import {getApi} from "./index.ts";

export const fetchLocations = async () => {
    const res = await getApi('/api/locations');
    return res.data;
};