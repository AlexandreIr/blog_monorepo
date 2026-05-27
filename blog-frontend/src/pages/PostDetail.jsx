import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { api } from "../api/api";

export default function PostDetail() {
  const { slug } = useParams();

  const [post, setPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);

  const [form, setForm] = useState({
    authorName: "",
    authorEmail: "",
    content: "",
  });

  useEffect(() => {
    async function loadData() {
      try {
        const postResponse = await api.get(`/posts/${slug}`);
        const commentsResponse = await api.get(`/posts/${slug}/comments`);

        setPost(postResponse.data);
        setComments(commentsResponse.data);
      } catch (error) {
        console.error(error);
      } finally {
        setLoading(false);
      }
    }

    loadData();
  }, [slug]);

  async function submitComment(e) {
    e.preventDefault();

    await api.post(`/posts/${slug}/comments`, form);

    alert("Comentário enviado para moderação.");

    setForm({
      authorName: "",
      authorEmail: "",
      content: "",
    });
  }

  if (loading) return <p>Carregando...</p>;
  if (!post) return <p>Post não encontrado.</p>;

  return (
    <div>
      <article className="article">
        <h1>{post.title}</h1>
        <p>{post.summary}</p>
        <small>Por Libertad facilities</small>

        <hr />

        <p>{post.content}</p>
      </article>

      <section className="card">
        <h2>Comentários</h2>

        {comments.length === 0 && <p>Nenhum comentário aprovado ainda.</p>}

        {comments.map((comment) => (
          <div key={comment.id}>
            <strong>{comment.authorName}</strong>
            <p>{comment.content}</p>
            <hr />
          </div>
        ))}

        <h3>Enviar comentário</h3>

        <form onSubmit={submitComment}>
          <input
            placeholder="Seu nome"
            value={form.authorName}
            onChange={(e) =>
              setForm({ ...form, authorName: e.target.value })
            }
          />

          <input
            placeholder="Seu email"
            value={form.authorEmail}
            onChange={(e) =>
              setForm({ ...form, authorEmail: e.target.value })
            }
          />

          <textarea
            placeholder="Seu comentário"
            value={form.content}
            onChange={(e) =>
              setForm({ ...form, content: e.target.value })
            }
          />

          <button>Enviar comentário</button>
        </form>
      </section>
    </div>
  );
}