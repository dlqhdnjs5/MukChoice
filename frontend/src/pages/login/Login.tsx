import kakaoLoginImg from '/src/assets/kakao_login_medium_narrow.png'
import {KAKAO_LOGIN_URL} from "../../consts/kakao.ts";
import {useEffect, useState} from 'react';

const Login = () => {
    const [isInviteLogin, setIsInviteLogin] = useState(false);
    const [inviteId, setInviteId] = useState<string | null>(null);
    const [isLoggingIn, setIsLoggingIn] = useState(false); // 로그인 진행 상태 추가

    useEffect(() => {
        // URL 파라미터에서 초대 여부 확인
        const urlParams = new URLSearchParams(window.location.search);
        const inviteParam = urlParams.get('state');

        if (inviteParam === 'invite-group') {
            setIsInviteLogin(true);
            setInviteId(urlParams.get('id'))
        }
    }, []);

    const handleKakaoLogin = () => {
        if (isLoggingIn) {
            return;
        }

        setIsLoggingIn(true);

        if (isInviteLogin) {
           window.location.href = KAKAO_LOGIN_URL + `&state=invite-group_${inviteId}`;
        } else {
            window.location.href = KAKAO_LOGIN_URL;
        }
    };

    return (
        <div className="min-h-screen flex flex-col items-center justify-center px-4 py-8">
            <h2 className="text-2xl font-bold text-white mb-8 tracking-tight">MukChoice</h2>
            <h1 className="text-2xl font-bold text-white mb-8 tracking-tight">
                {isInviteLogin ? '초대받으셨나요? 반가워요!' : 'Login'}
            </h1>
            <button
                className={`w-full flex items-center justify-center bg-transparent p-0 border-none shadow-none transition mb-3 ${
                    isLoggingIn ? 'opacity-50 cursor-not-allowed' : 'hover:scale-105'
                }`}
                onClick={handleKakaoLogin}
                disabled={isLoggingIn}>
                <img src={kakaoLoginImg} alt="카카오 로그인" className="w-full h-auto"/>
            </button>
            {isLoggingIn && (
                <p className="text-yellow-500 text-sm text-center">카카오 로그인 페이지로 이동 중...</p>
            )}
            <p className="text-gray-500 text-xs text-center">KakaoTalk</p>
        </div>
    );
};

export default Login;
