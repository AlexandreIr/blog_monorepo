import { useEffect, useState } from "react";
import { adminRequest } from "../../api/adminApi";
import "./adminCss.css";

interface Comment {
    id: number;
    authorName: string;
    authorEmail: string;
    content: string;
    status: string;
    postId: number;
}

interface PagedResponse<T> {
    content: T[];
}

export default function AdminComments() {
    const [comments, setComments] = useState<Comment[]>([]);

    async function loadComments() {
        try {
            const data = await adminRequest<PagedResponse<Comment> | Comment[]>(
                `/admin/comments/pending`
            );

            if (Array.isArray(data)) {
                setComments(data);
            } else {
                setComments(data.content ?? []);
            }
        } catch (error) {
            console.error("Erro ao carregar comentários:", error);
            setComments([]);
        }
    }

    async function approve(id: number) {
        await adminRequest(`/admin/comments/${id}/approve`, { method: "PATCH" });
        setComments((previous) => previous.filter((comment) => comment.id !== id));
    }

    async function reject(id: number) {
        await adminRequest(`/admin/comments/${id}/reject`, { method: "PATCH" });
        setComments((previous) => previous.filter((comment) => comment.id !== id));
    }

    useEffect(() => {
        loadComments();
    }, []);

    return (
        <div>
            <div className="admin-header">
                <div>
                    <h1>Comentários</h1>
                    <p>Modere comentários enviados pelos leitores.</p>
                </div>
            </div>

            {comments.length === 0 && <p>Nenhum comentário pendente.</p>}

            <div className="admin-list">
                {comments.map((comment) => (
                    <div className="admin-card" key={comment.id}>
                        <div>
                            <h2>{comment.authorName}</h2>
                            <p>{comment.content}</p>
                            <small>{comment.authorEmail}</small>
                        </div>

                        <div className="admin-card-actions">
                            <button onClick={() => approve(comment.id)}>Aprovar</button>
                            <button className="secondary" onClick={() => reject(comment.id)}>
                                Rejeitar
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}