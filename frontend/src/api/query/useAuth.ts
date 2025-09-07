import { useMutation } from "@tanstack/react-query";
import { kakaoLogout } from "../function/AuthFunc.ts";

export const useKakaoLogout = () => {
    return useMutation({
        mutationFn: kakaoLogout,
        onSuccess: () => {
            console.log('카카오 로그아웃 성공');
        },
        onError: (error) => {
            console.error('카카오 로그아웃 에러:', error);
        }
    });
};
