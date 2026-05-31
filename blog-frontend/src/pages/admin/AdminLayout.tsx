import { Link, NavLink, Outlet, useNavigate } from "react-router-dom";
import { isAuthenticated, removeToken } from "../../api/adminApi";
// @ts-ignore
import "./adminCss.css";

export default function AdminLayout() {
    const navigate = useNavigate();

    if (!isAuthenticated()) {
        navigate("/painel-secreto/login");
        return null;
    }

    function logout() {
        removeToken();
        navigate("/");
    }

    return (
        <div className="admin-shell">
            <aside className="admin-sidebar">
                <Link to="/painel-secreto" className="admin-logo">
                    <div>
                        <strong>Painel Administrativo</strong>
                        <small>Libertad</small>
                    </div>
                </Link>

                <nav className="admin-nav">
                    <NavLink to="/painel-secreto">Dashboard</NavLink>
                    <NavLink to="/painel-secreto/posts">Posts</NavLink>
                    <NavLink to="/painel-secreto/categories">Categorias</NavLink>
                    <NavLink to="/painel-secreto/comments">Comentários</NavLink>
                    <Link to="/">Ver blog</Link>
                </nav>

                <button className="logout-button" onClick={logout}>
                    Sair
                </button>
            </aside>

            <main className="admin-main">
                <Outlet />
            </main>
        </div>
    );
}