import { Link, useLocation } from "react-router-dom";

const BottomNav = () => {
    const location = useLocation();
    const navs = [
        { label: "홈", path: "/choice", icon: "🏠" },
        { label: "위시", path: "/wish", icon: "💖" },
        { label: "그룹", path: "/place-group", icon: "🔔" },
        { label: "마이", path: "/my", icon: "👤" },
    ];
    return (
        <nav className="lp-bottom-nav">
            {navs.map((nav) => (
                <Link
                    key={nav.label}
                    to={nav.path}
                    className={`lp-bottom-nav__item${location.pathname.startsWith(nav.path) ? " active" : ""}`}
                    style={{ position: "relative", padding: "8px 0", transition: "background 0.2s" }}
                >
                    <span style={{ fontSize: 20, display: "block", marginBottom: 2 }}>{nav.icon}</span>
                    {nav.label}
                    {location.pathname.startsWith(nav.path) && (
                        <span style={{
                            position: "absolute",
                            left: "50%",
                            bottom: 0,
                            transform: "translateX(-50%)",
                            width: 24,
                            height: 3,
                            borderRadius: 2,
                            background: "#4f46e5",
                            transition: "all 0.2s"
                        }} />
                    )}
                </Link>
            ))}
        </nav>
    );
}

export default BottomNav;