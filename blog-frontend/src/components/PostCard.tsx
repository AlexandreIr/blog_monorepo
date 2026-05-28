import { Calendar, Clock } from "lucide-react";
import { PostSummary } from "../types/blog";

interface PostCardProps {
  post: PostSummary;
}

export function PostCard({ post }: PostCardProps) {
  const category = post.categories?.[0];

  return (
    <article className="post-card">
      <a href={`/posts/${post.slug}`} className="post-card-link">
        <div className="post-image">
          {post.coverImageUrl ? (
            <img
            src={post.coverImageUrl}
            alt={post.title}
            loading="lazy"
            decoding="async"
            />
          ) : (
            <div className="image-placeholder">LCS</div>
          )}

          {category && <span className="category-tag">{category.name}</span>}
        </div>

        <div className="post-content">
          <h3>{post.title}</h3>
          <p>{post.summary}</p>

          <div className="post-meta">
            <span>

              {post.publishedAt
                ? new Date(post.publishedAt).toLocaleDateString("pt-BR")
                : "Sem data"}
            </span>


          </div>
        </div>
      </a>
    </article>
  );
}