import { Link } from "react-router-dom";
// @ts-ignore
import "./adminCss.css";

export default function AdminDashboard() {
    return (
        <div>
            <div className="admin-header dashboard-header">
                <div>
                    <span className="admin-eyebrow">Visão geral</span>
                    <h1>Painel administrativo</h1>
                    <p>Gerencie o conteúdo, acompanhe o fluxo editorial e mantenha o blog organizado.</p>
                </div>

                <Link to="/painel-secreto/posts/new">
                    <button>Novo post</button>
                </Link>
            </div>

            <section className="dashboard-stats">
                <div className="dashboard-card primary">
                    <span>Conteúdo</span>
                    <strong>Posts</strong>
                    <p>Crie, edite, publique e despublique artigos do blog.</p>
                    <Link to="/painel-secreto/posts">Gerenciar posts</Link>
                </div>

                <div className="dashboard-card">
                    <span>Organização</span>
                    <strong>Categorias</strong>
                    <p>Organize os artigos por temas para facilitar navegação e estratégia editorial.</p>
                    <Link to="/painel-secreto/categories">Gerenciar categorias</Link>
                </div>

                <div className="dashboard-card">
                    <span>Comunidade</span>
                    <strong>Comentários</strong>
                    <p>Aprove ou rejeite comentários antes que eles apareçam publicamente.</p>
                    <Link to="/painel-secreto/comments">Moderar comentários</Link>
                </div>
            </section>

            <section className="dashboard-grid">
                <div className="dashboard-panel">
                    <h2>Fluxo recomendado</h2>

                    <div className="timeline">
                        <div>
                            <span>1</span>
                            <p>Crie uma categoria estratégica para o conteúdo.</p>
                        </div>

                        <div>
                            <span>2</span>
                            <p>Escreva o post com título, resumo, SEO e imagem de capa.</p>
                        </div>

                        <div>
                            <span>3</span>
                            <p>Revise o artigo e publique quando estiver pronto.</p>
                        </div>

                        <div>
                            <span>4</span>
                            <p>Monitore comentários e aprove apenas o que fizer sentido.</p>
                        </div>
                    </div>
                </div>

                <div className="dashboard-panel highlight">
                    <h2>Status do projeto</h2>

                    <ul className="status-list">
                        <li>
                            <strong>API pública</strong>
                            <span>Ativa</span>
                        </li>

                        <li>
                            <strong>Painel administrativo</strong>
                            <span>Ativo</span>
                        </li>

                        <li>
                            <strong>Editor rich text</strong>
                            <span>Ativo</span>
                        </li>

                        <li>
                            <strong>Cloudinary</strong>
                            <span>Integrado</span>
                        </li>

                        <li>
                            <strong>Moderação</strong>
                            <span>Ativa</span>
                        </li>
                    </ul>
                </div>
            </section>
        </div>
    );
}