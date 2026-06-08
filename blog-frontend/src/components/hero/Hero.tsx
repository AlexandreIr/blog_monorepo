import { PostSummary } from "../types/blog";
import "./hero.css";

interface HeroProps {
  post?: PostSummary;
}

export function Hero({ post }: HeroProps) {
  return (
    <section className="hero">
      <div className="hero-overlay">
        <span className="badge">Destaque</span>

        <h1>{post?.title ?? "Blog Libertad"}</h1>

        <p>
          {post?.summary ??
            "Fique por dentro de todas as novidades aqui da libertad. "}
        </p>

        <div className="hero-meta">
          <span>{post?.publishedAt ? new Date(post.publishedAt).toLocaleDateString("pt-BR") : ""}</span>

        </div>

        {post && (
          <a className="hero-button" href={`/posts/${post.slug}`}>
            Ler artigo completo
          </a>
        )}
      </div>
    </section>
  );
}