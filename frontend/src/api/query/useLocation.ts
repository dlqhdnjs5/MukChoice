import {keepPreviousData, useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import LocationStore from "../../store/LocationStore";
import {LOCATION_KEY} from "./queryKey.ts";
import {fetchLocations} from "../function/LocationFunc.ts";
import {deleteApi, postApi, putApi} from "../function";

export const useAddLocation = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: async (locationRequest: object) => {
            const res = await postApi('/api/locations', locationRequest);
            return res.data;
        },
        onSuccess: (data) => {
            LocationStore.setCurrentLocation(data.location);
            queryClient.invalidateQueries({
                queryKey: [LOCATION_KEY.fetchLocation]
            });
        },
        onError: (err) => {
            console.error('위치 등록 실패:', err);
        }
    })
}

export const usePickLocation = () => {
    const queryClient = useQueryClient();
    const pickLocationMutation = useMutation({
        mutationFn: async (locationRequest: object) => {
            const res = await putApi(`/api/locations/pick`, locationRequest);
            return res.data;
        },
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [LOCATION_KEY.fetchLocation]
            });
        },
        onError: (err) => {
            console.error('위치 선택 실패:', err);
        }
    });

    return pickLocationMutation;
}

export const useRemoveLocation = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: async (locationNo: number) => {
            const res = await deleteApi(`/api/locations/${locationNo}`);
            return res.data;
        },
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [LOCATION_KEY.fetchLocation]
            });
        },
        onError: (err) => {
            console.error('위치 삭제 실패:', err);
        }
    });
}

export const useFetchLocationQuery = () => {
    return useQuery({
        queryKey: [LOCATION_KEY.fetchLocation],
        queryFn: fetchLocations,
        placeholderData: keepPreviousData,
        refetchOnWindowFocus: false,
        enabled: true
    });
};
