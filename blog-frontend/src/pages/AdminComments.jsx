import { useCallback, useEffect, useState } from "react";
import { api } from "../api/api";

export default function AdminComments() {
  const [comments, setComments] = useState([]);

  const loadComments = useCallback(async () => {
    try {
      const res = await api.get("api/admin/comments/pending");

      const data = res.data;

      if (Array.isArray(data)) {
        setComments(data);
        return;
      }

      if (Array.isArray(data.content)) {
        setComments(data.content);
        return;
      }

      setComments([]);
    } catch (error) {
      console.error("Erro ao carregar comentários:", error);
      setComments([]);
    }
  }, []);

  useEffect(() => {
    loadComments();
  }, [loadComments]);

  async function approve(id) {
    try {
      await api.patch(`/api/admin/comments/${id}/approve`);
      setComments((previous) => previous.filter((comment) => comment.id !== id));
    } catch (error) {
      console.error("Erro ao aprovar comentário:", error);
    }
  }

  async function reject(id) {
    try {
      await api.patch(`/api/admin/comments/${id}/reject`);
      setComments((previous) => previous.filter((comment) => comment.id !== id));
    } catch (error) {
      console.error("Erro ao rejeitar comentário:", error);
    }
  }

  return (
    <div>
      <h1>Moderação de Comentários</h1>

      {comments.length === 0 && (
        <p>Nenhum comentário pendente para moderação.</p>
      )}

      {comments.map((comment) => (
        <div className="card" key={comment.id}>
          <p>{comment.content}</p>

          <small>
            {comment.authorName} — {comment.authorEmail}
          </small>

          <p>
            Post ID: <strong>{comment.postId}</strong>
          </p>

          <div>
            <button onClick={() => approve(comment.id)}>Aprovar</button>
            <button onClick={() => reject(comment.id)}>Rejeitar</button>
          </div>
        </div>
      ))}
    </div>
  );
}