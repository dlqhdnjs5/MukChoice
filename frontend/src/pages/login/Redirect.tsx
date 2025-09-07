import {observer} from "mobx-react-lite";
import {useEffect} from 'react';
import {useNavigate} from 'react-router-dom';
import {API} from "../../api";
import Spinner from '../../component/ui/Spinner.tsx';
import UserStore from '../../store/UserStore.ts';
import type {UserResponse} from "../../types/user.ts";
import {setCookie} from "../../utils/cookieUtils.ts";
import {parseInvitationState} from "../../types/invitation.ts";

const OAuthRedirect = observer(() => {
    const navigate = useNavigate();

    useEffect(() => {
        const accessToken = new URLSearchParams(window.location.search).get("accessToken");
        if (!accessToken) {
            alert('카카오톡 로그인 정보가 없습니다. 다시 시도해주세요.');
            navigate("/login", {replace: true});
            return;
        }

        const state = new URLSearchParams(window.location.search).get("state");
        const parsedInvitation = parseInvitationState(state);
        const fetchUser = async () => {
            const loginUrl = parsedInvitation.isInvitation && parsedInvitation.invitationInfo
                ? `/api/oauth/kakao-login/invitations/${parsedInvitation.invitationInfo.inviteType}?accessToken=${accessToken}&invitationId=${parsedInvitation.invitationInfo.invitationId}`
                : `/api/oauth/kakao-login?accessToken=${accessToken}`;
            try {
                const response = await API.get<UserResponse>(loginUrl);
                UserStore.setUser(response.data.userDTO);
                setCookie(import.meta.env.VITE_MUKCHOICE_X_TOKEN, response.data.jwtToken, 5);
            } catch (e) {
                alert('로그인 중 오류가 발생했습니다. 다시 시도해주세요.');
                navigate("/login", {replace: true});
            } finally {
                navigate("/choice", {replace: true});
            }
        };

        fetchUser();
    }, [navigate]);

    return (
        <>
            <Spinner message="로그인 중입니다"/>
        </>

    );
});

export default OAuthRedirect;