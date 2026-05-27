import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../api/api";

export default function AdminPosts() {
  const [posts, setPosts] = useState([]);

  const loadPosts = useCallback(async () => {
    try {
      const res = await api.get("/api/admin/posts");
      const data = res.data;

      if (Array.isArray(data)) {
        setPosts(data);
        return;
      }

      if (Array.isArray(data.content)) {
        setPosts(data.content);
        return;
      }

      setPosts([]);
    } catch (error) {
      console.error("Erro ao carregar posts:", error);
      setPosts([]);
    }
  }, []);

  useEffect(() => {
    loadPosts();
  }, [loadPosts]);

  function publish(id) {
    api.patch(`/api/admin/posts/${id}/publish`).then(loadPosts);
  }

  function unpublish(id) {
    api.patch(`/api/admin/posts/${id}/unpublish`).then(loadPosts);
  }

  return (
    <div>
      <div className="page-header">
        <h1>Administração de Posts</h1>

        <Link to="/painel-secreto/posts/new">
          <button>Novo post</button>
        </Link>
      </div>

      {posts.length === 0 && <p>Nenhum post encontrado.</p>}

      {posts.map((post) => (
        <div className="card" key={post.id}>
          <h2>{post.title}</h2>
          <p>{post.summary}</p>
          <p>
            Status: <strong>{post.status}</strong>
          </p>

          <button onClick={() => publish(post.id)}>Publicar</button>
          <button onClick={() => unpublish(post.id)}>Despublicar</button>
        </div>
      ))}
    </div>
  );
}