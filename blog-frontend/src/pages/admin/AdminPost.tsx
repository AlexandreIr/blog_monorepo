import { useEffect, useState } from "react";
import { adminRequest } from "../../api/adminApi";
import { Link } from "react-router-dom";
// @ts-ignore
import "./adminCss.css";

interface Post {
    id: number;
    title: string;
    summary: string;
    status: "DRAFT" | "PUBLISHED";
    viewCount: number;
}

interface PagedResponse<T> {
    content: T[];
}

export default function AdminPosts() {
    const [posts, setPosts] = useState<Post[]>([]);
    const [loading, setLoading] = useState(true);

    async function loadPosts() {
        try {
            setLoading(true);

            const data = await adminRequest<PagedResponse<Post> | Post[]>("/admin/posts");

            if (Array.isArray(data)) {
                setPosts(data);
            } else {
                setPosts(data.content ?? []);
            }
        } catch (error) {
            console.error("Erro ao carregar posts:", error);
            setPosts([]);
        } finally {
            setLoading(false);
        }
    }

    async function publish(id: number) {
        await adminRequest(`/admin/posts/${id}/publish`, { method: "PATCH" });
        loadPosts();
    }

    async function unpublish(id: number) {
        await adminRequest(`/admin/posts/${id}/unpublish`, { method: "PATCH" });
        loadPosts();
    }

    async function deletePost(id: number) {
        const confirmed = window.confirm(
            "Tem certeza que deseja deletar este post? Essa ação não pode ser desfeita."
        );

        if (!confirmed) return;

        try {
            await adminRequest(`/admin/posts/${id}`, { method: "DELETE" });
            setPosts((previous) => previous.filter((post) => post.id !== id));
        } catch (error) {
            console.error("Erro ao deletar post:", error);
            alert("Não foi possível deletar o post.");
        }
    }

    useEffect(() => {
        loadPosts();
    }, []);

    return (
        <div>
            <div className="admin-header">
                <div>
                    <h1>Posts</h1>
                    <p>Gerencie os artigos do blog.</p>
                </div>

                <Link to="/painel-secreto/posts/new">
                    <button>Novo post</button>
                </Link>
            </div>

            {loading && <p>Carregando posts...</p>}

            {!loading && posts.length === 0 && <p>Nenhum post encontrado.</p>}

            <div className="admin-list">
                {posts.map((post) => (
                    <div className="admin-card" key={post.id}>
                        <div>
                            <h2>{post.title}</h2>
                            <p>{post.summary}</p>

                            <span className={`status-pill ${post.status.toLowerCase()}`}>
                {post.status}
              </span>
                        </div>

                        <div className="admin-card-actions">
                            <Link to={`/painel-secreto/posts/${post.id}/edit`}>
                                <button>Editar</button>
                            </Link>

                            {post.status === "PUBLISHED" ? (
                                <button className="secondary" onClick={() => unpublish(post.id)}>
                                    Despublicar
                                </button>
                            ) : (
                                <button onClick={() => publish(post.id)}>
                                    Publicar
                                </button>
                            )}

                            <button className="danger" onClick={() => deletePost(post.id)}>
                                Deletar
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}