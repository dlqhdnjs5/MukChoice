import {keepPreviousData, useQuery} from '@tanstack/react-query';
import {HELLO_KEY} from "./queryKey.ts";
import {fetchHello} from "../function/helloFunction.ts";

export const useHelloQuery = () => {
  return useQuery({
    queryKey: [HELLO_KEY.hello],
    queryFn: fetchHello,
    placeholderData: keepPreviousData,
    refetchOnWindowFocus: false,
    enabled: false
  });
};

