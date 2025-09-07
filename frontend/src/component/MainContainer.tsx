import type {ReactNode} from "react";
import BottomNav from "./BottomNav.tsx";
import '/src/styles/container.css';
import '/src/styles/bottomNav.css';
import TopNav from "./TopNav.tsx";

const MainContainer = ({children}: { children: ReactNode }) => {
    return (
        <>
            <div className="main-container-wrapper">
                <TopNav/>
                <div className="lp-main-shadow">
                    <div className="lp-main-container " style={{background: '#f7f7fa'}}>
                        <div className="pt-8">
                            {children}
                        </div>
                    </div>
                </div>
                <BottomNav/>
            </div>
        </>
    )
}

export default MainContainer;

