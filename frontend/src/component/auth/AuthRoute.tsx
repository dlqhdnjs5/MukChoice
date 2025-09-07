import { Navigate } from "react-router-dom";
import { getCookie } from "../../utils/cookieUtils.ts";
import type {JSX} from "react";

const tokenName = import.meta.env.VITE_MUKCHOICE_X_TOKEN;

interface AuthRouteProps {
  children: JSX.Element;
}

export default function AuthRoute({ children }: AuthRouteProps) {
  const token = getCookie(tokenName);
  if (!token) {
    alert('로그인 페이지로 이동합니다.');

    return <Navigate to="/login" replace />;
  }
  return children;
}

