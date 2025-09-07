import { Navigate } from "react-router-dom";
import { getCookie } from "../../utils/cookieUtils.ts";
import type {JSX} from "react";

const tokenName = import.meta.env.VITE_MUKCHOICE_X_TOKEN;

interface GuestRouteProps {
  children: JSX.Element;
}

export default function GuestRoute({ children }: GuestRouteProps) {
  const token = getCookie(tokenName);
  if (token) {
    return <Navigate to="/choice" replace />;
  }
  return children;
}

