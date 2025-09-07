import type {FetchPlacesParams, FetchPlacesMultiCategoryParams, WishDongInfoResponse} from "../../types/place.ts";
import {getApi} from "./index.ts";

// TODO 추후 f/o
export const fetchPlaces = async ({coordinateX, coordinateY, query, page = 1}: FetchPlacesParams) => {
    const response = await getApi('/api/places', {
        coordinateX,
        coordinateY,
        query,
        page,
    })

    return response.data
};

export const searchPlaces = async ({coordinateX, coordinateY, query}: FetchPlacesParams) => {
    const response = await getApi('/api/places', {
        coordinateX,
        coordinateY,
        query,
    })

    return response.data
};

export const fetchPlacesMultiCategory = async ({
                                                   coordinateX,
                                                   coordinateY,
                                                   categories,
                                                   page = 1
                                               }: FetchPlacesMultiCategoryParams) => {
    const queries = categories?.join(",");
    const response = await getApi('/api/places/v2', {
        coordinateX,
        coordinateY,
        queries,
        page,
    });
    return response.data;
};

export const fetchWishList = async ({offset = 0, limit = 20, currentLocationNo, bcode}: {
    offset: number,
    limit: number,
    currentLocationNo?: number,
    bcode?: string
}) => {
    const response = await getApi('/api/places/wishes', {
        offset,
        limit,
        currentLocationNo,
        bcode
    });
    return response.data;
};

export const fetchWishDongList = async (): Promise<WishDongInfoResponse> => {
    const response = await getApi('/api/places/wishes/dongs');
    return response.data;
};
