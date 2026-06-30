import { Link, NavLink, Outlet, useLocation, useNavigate } from "react-router-dom";
import { isAuthenticated, removeToken } from "../../api/adminApi";
// @ts-ignore
import "./adminCss.css";

export default function AdminLayout() {
    const navigate = useNavigate();
    const location = useLocation();

    const isEditorPage =
        location.pathname.includes("/posts/new") ||
        location.pathname.includes("/edit");

    if (!isAuthenticated()) {
        navigate("/painel-secreto/login");
        return null;
    }

    function logout() {
        removeToken();
        navigate("/painel-secreto/login");
    }

    return (
        <div className={isEditorPage ? "admin-shell editor-mode" : "admin-shell"}>
            {!isEditorPage && (
                <aside className="admin-sidebar">
                    <Link to="/painel-secreto" className="admin-logo">

                        <div>
                            <strong>Painel administrativo</strong>
                            <small>Blog libertad</small>
                        </div>
                    </Link>

                    <nav className="admin-nav">
                        <NavLink to="/painel-secreto/security">Segurança</NavLink>
                        <NavLink to="/painel-secreto" end>Dashboard</NavLink>
                        <NavLink to="/painel-secreto/posts">Posts</NavLink>
                        <NavLink to="/painel-secreto/categories">Categorias</NavLink>
                        <NavLink to="/painel-secreto/comments">Comentários</NavLink>
                        <Link to="/">Ver blog</Link>
                    </nav>

                    <button className="logout-button" onClick={logout}>
                        Sair
                    </button>
                </aside>
            )}

            <main className="admin-main">
                <Outlet />
            </main>
        </div>
    );
}