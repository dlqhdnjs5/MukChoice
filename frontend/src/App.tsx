import {Route, Routes, useNavigate} from "react-router-dom";
import Test from "./pages/Test.tsx";
import About from "./pages/About.tsx";
import Login from "./pages/login/Login.tsx";
import OAuthRedirect from "./pages/login/Redirect.tsx";
import MainTest from "./pages/MainTest.tsx";
import MainContainer from "./component/MainContainer.tsx";
import Choice from "./pages/choice/Choice.tsx";
import AuthRoute from "./component/auth/AuthRoute.tsx";
import GuestRoute from "./component/auth/GuestRoute.tsx";
import {useEffect} from "react";
import {setNavigate} from "./api";
import type {NavigateFunction} from "react-router";
import Wish from "./pages/wish/Wish.tsx";
import PlaceGroup from "./pages/placeGroup/PlaceGroup.tsx";
import My from "./pages/my/My.tsx";
import UserStore from "./store/UserStore.ts";
import {getCookie} from "./utils/cookieUtils.ts";

function App() {
    // 상태 관리용 navigate 함수 여러 곳에서 사용하기 위해 추가함
    const navigate: NavigateFunction = useNavigate();
    useEffect(() => {
        setNavigate(navigate); // api.ts 에서 navigate를 설정

        const token = getCookie(import.meta.env.VITE_MUKCHOICE_X_TOKEN);
        if (token) {
            UserStore.initializeUser();
        }
    }, [navigate]);

    return (
        <Routes>
            <Route path="/" element={<AuthRoute><MainContainer children={<Choice/>}/></AuthRoute>}/>
            <Route path="/choice" element={<AuthRoute><MainContainer children={<Choice/>}/></AuthRoute>}/>
            <Route path="/wish" element={<AuthRoute><MainContainer children={<Wish/>}/></AuthRoute>}/>
            <Route path="/place-group" element={<AuthRoute><MainContainer children={<PlaceGroup/>}/></AuthRoute>}/>
            <Route path="/my" element={<AuthRoute><MainContainer children={<My/>}/></AuthRoute>}/>
            {/*<Route path="/about" element={<AuthRoute><MainContainer children={<About/>}/></AuthRoute>}/>*/}
            {/*<Route path="/test2" element={<AuthRoute><MainContainer children={<MainTest/>}/></AuthRoute>}/>*/}
            {/* <Route path="/map" element={<AuthRoute><MainContainer children={<KakaoMap/>}/></AuthRoute>}/> */}
            <Route path="/login" element={<GuestRoute><Login/></GuestRoute>}/>
            <Route path="/kakao-oauth/redirect" element={<GuestRoute><OAuthRedirect/></GuestRoute>}/>
        </Routes>
    )
}

export default App
