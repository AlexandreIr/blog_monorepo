import { Link } from "react-router-dom";
// @ts-ignore
import "./adminCss.css";

export default function AdminDashboard() {
    return (
        <div>
            <div className="admin-header">
                <div>
                    <h1>Painel administrativo</h1>
                    <p>Gerencie o conteúdo público do blog.</p>
                </div>
            </div>

            <div className="admin-grid">
                <Link to="/painel-secreto/posts" className="admin-stat-card">
                    <strong>Posts</strong>
                    <p>Criar, revisar, publicar e despublicar artigos.</p>
                </Link>

                <Link to="/painel-secreto/categories" className="admin-stat-card">
                    <strong>Categorias</strong>
                    <p>Organizar conteúdos por temas estratégicos.</p>
                </Link>

                <Link to="/painel-secreto/comments" className="admin-stat-card">
                    <strong>Comentários</strong>
                    <p>Aprovar ou rejeitar comentários enviados pelos leitores.</p>
                </Link>
            </div>
        </div>
    );
}