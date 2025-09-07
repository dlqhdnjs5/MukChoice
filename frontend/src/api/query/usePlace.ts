import {keepPreviousData, useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {PLACE_KEY} from "./queryKey.ts";
import {
    fetchPlaces,
    fetchPlacesMultiCategory,
    fetchWishDongList,
    fetchWishList,
    searchPlaces
} from "../function/PlaceFunc.ts";
import type {FetchPlacesMultiCategoryParams, FetchPlacesParams} from "../../types/place.ts";
import {postApi} from "../function";


// TODO 추후 f/o
export const useFetchPlacesQuery = (
    params: FetchPlacesParams,
    options?: { enabled?: boolean }
) => {
    return useQuery({
        queryKey: [
            PLACE_KEY.fetchPlaces,
            params.coordinateX,
            params.coordinateY,
            params.query,
            params.page ?? 1,
        ],
        queryFn: () => fetchPlaces({
            coordinateX: params.coordinateX,
            coordinateY: params.coordinateY,
            query: params.query,
            page: params.page ?? 1,
        }),
        placeholderData: keepPreviousData,
        refetchOnWindowFocus: false,
        enabled: options?.enabled ?? true,
    });
};

export const useFetchPlacesMultiCategoryQuery = (
    params: FetchPlacesMultiCategoryParams,
    options?: { enabled?: boolean }
) => {
    return useQuery({
        queryKey: [
            PLACE_KEY.fetchPlaceMultiCategory,
            params.coordinateX,
            params.coordinateY,
            params.categories?.join(','),
            params.page ?? 1,
        ],
        queryFn: () =>  fetchPlacesMultiCategory({
            coordinateX: params.coordinateX,
            coordinateY: params.coordinateY,
            categories: params.categories,
            page: params.page ?? 1,
        }),
        placeholderData: keepPreviousData,
        refetchOnWindowFocus: false,
        enabled: options?.enabled ?? true,
    });
};

export const useFetchWishListQuery = (
    params: { offset: number; limit: number, currentLocationNo?: number, bcode?: string },
) => {
    return useQuery({
        queryKey: [PLACE_KEY.fetchWishList, params.offset ?? 0, params.limit ?? 20, params.currentLocationNo, params.bcode],
        queryFn: () => fetchWishList(params),
        refetchOnWindowFocus: false,
    });
};

export const useFetchWishDongListQuery = () => {
    return useQuery({
        queryKey: [PLACE_KEY.fetchWishDongList],
        queryFn: fetchWishDongList,
        refetchOnWindowFocus: false,
    });
};

export const useAddWishList = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: async (addWishListRequest: object) => {
            await postApi(`/api/places/wishList`, addWishListRequest);
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [PLACE_KEY.fetchPlaces] });
        },
        onError: () => {
        }
    });
}

export const useSearchPlaces = (
    params: {
        coordinateX: string;
        coordinateY: string;
        query: string;
    },
    options?: { enabled?: boolean }
) => {
    return useQuery({
        queryKey: [
            PLACE_KEY.searchPlaces,
            params.coordinateX,
            params.coordinateY,
            params.query,
        ],
        queryFn: () => searchPlaces(params),
        refetchOnWindowFocus: false,
        enabled: options?.enabled ?? false,
        staleTime: 1000 * 60 * 5,
    });
};
