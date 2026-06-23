import { useEffect, useState } from "react";
import { ArrowLeft, Calendar, Clock, Send, Eye } from "lucide-react";
import { api } from "../../api/api";
// @ts-ignore
import { Comment, PostDetail } from "../types/blog";
import { Header } from "../../components/header/Header";
import { Footer } from "../../components/Footer";
// @ts-ignore
import "./postDetailsPage.css";
import { sanitizeHtml } from "../../utils/sanitizeHtml";
import {Category} from "../../types/blog";
// @ts-ignore
import "../admin/mediaStyles.css";

export default function PostDetailPage() {
  const slug = window.location.pathname.split("/posts/")[1];

  const [search, setSearch] = useState("");
  const [post, setPost] = useState<PostDetail | null>(null);
  const [comments, setComments] = useState<Comment[]>([]);
  const [loading, setLoading] = useState(true);

  const [commentForm, setCommentForm] = useState({
    authorName: "",
    authorEmail: "",
    content: "",
  });

  useEffect(() => {
    async function loadPost() {
      try {
        const postResponse = await api.get<PostDetail>(`/posts/${slug}`);
        const commentsResponse = await api.get<Comment[]>(`/posts/${slug}/comments`);

        setPost(postResponse.data);
        setComments(Array.isArray(commentsResponse.data) ? commentsResponse.data : []);

        await api.post(`/api/posts/${slug}/views`);
      } catch (error) {
        console.error("Erro ao carregar post:", error);
        setPost(null);
      } finally {
        setLoading(false);
      }
    }

    if (slug) {
      loadPost();
    }
  }, [slug]);

  async function submitComment(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (
      !commentForm.authorName.trim() ||
      !commentForm.authorEmail.trim() ||
      !commentForm.content.trim()
    ) {
      alert("Preencha todos os campos para comentar.");
      return;
    }

    try {
      await api.post(`/posts/${slug}/comments`, {
        authorName: commentForm.authorName.trim(),
        authorEmail: commentForm.authorEmail.trim(),
        content: commentForm.content.trim(),
      });

      alert("Comentário enviado para moderação.");

      setCommentForm({
        authorName: "",
        authorEmail: "",
        content: "",
      });
    } catch (error) {
      console.error("Erro ao enviar comentário:", error);
      alert("Não foi possível enviar seu comentário.");
    }
  }

  // @ts-ignore
  // @ts-ignore
  return (
    <div>
      <Header search={search} onSearchChange={setSearch} />

      <main className="page">
        <a className="back-link" href="/">
          <ArrowLeft size={18} />
          Voltar para o blog
        </a>

        {loading && <p>Carregando artigo...</p>}

        {!loading && !post && (
          <section className="article-shell">
            <h1>Post não encontrado</h1>
            <p>O artigo solicitado não existe ou ainda não foi publicado.</p>
          </section>
        )}


        {post && (
          <>
            <article className="article-shell">
              <div className="article-cover">
                {post.coverImageUrl ? (
                  <img src={post.coverImageUrl} alt={post.title} />
                ) : (
                  <div className="article-cover-placeholder">LCS</div>
                )}
              </div>


              <div className="article-body">
                <div className="article-categories">
                  {post.categories?.map((category : Category) => (
                    <span key={category.id}>{category.name} | </span>
                  ))}
                </div>

                <p>
                  Visualizações: <strong>{post.viewCount ?? 0}</strong>
                </p>

                <h1>{post.title}</h1>

                <p className="article-summary">{post.summary}</p>

                <div className="article-meta">
                  <span>Por equipe LCS</span>

                  <span>
                    <Calendar size={16} />
                    {post.publishedAt
                      ? new Date(post.publishedAt).toLocaleDateString("pt-BR")
                      : "Sem data"}
                  </span>

                  <span>
                    <Clock size={16} />5 min de leitura
                  </span>
                </div>

                <div
                    className="article-content article-body"
                    dangerouslySetInnerHTML={{
                      __html: sanitizeHtml(post.content),
                    }}
                />
              </div>
            </article>

            <section className="comments-section">
              <div className="comments-header">
                <div>
                  <h2>Comentários</h2>
                  <div className="underline" />
                </div>

                <span>{comments.length} aprovado{comments.length>1?'s':''}</span>
              </div>

              {comments.length === 0 && (
                <p className="empty-message">
                  Nenhum comentário ainda. Seja o primeiro a participar.
                </p>
              )}

              {comments.map((comment) => (
                <div className="comment-card" key={comment.id}>
                  <strong>{comment.authorName}</strong>
                  <p>{comment.content}</p>

                  {comment.createdAt && (
                    <small>
                      {new Date(comment.createdAt).toLocaleDateString("pt-BR")}
                    </small>
                  )}
                </div>
              ))}

              <form className="comment-form" onSubmit={submitComment}>
                <h3>Enviar comentário</h3>
                <p>Seu comentário será enviado para moderação antes de aparecer no blog.</p>

                <div className="form-grid">
                  <input
                    placeholder="Seu nome"
                    value={commentForm.authorName}
                    onChange={(event) =>
                      setCommentForm({
                        ...commentForm,
                        authorName: event.target.value,
                      })
                    }
                  />

                  <input
                    placeholder="Seu e-mail"
                    value={commentForm.authorEmail}
                    onChange={(event) =>
                      setCommentForm({
                        ...commentForm,
                        authorEmail: event.target.value,
                      })
                    }
                  />
                </div>

                <textarea
                  placeholder="Escreva seu comentário"
                  value={commentForm.content}
                  onChange={(event) =>
                    setCommentForm({
                      ...commentForm,
                      content: event.target.value,
                    })
                  }
                />

                <button>
                  Enviar comentário <Send size={16} />
                </button>
              </form>
            </section>
          </>
        )}
      </main>

      <Footer />
    </div>
  );
}