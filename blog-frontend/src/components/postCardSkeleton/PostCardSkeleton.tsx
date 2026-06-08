// @ts-ignore
import "./skeleton.css";

export function PostCardSkeleton() {
  return (
    <article className="post-card skeleton-card">
      <div className="skeleton skeleton-image" />

      <div className="post-content">
        <div className="skeleton skeleton-title" />
        <div className="skeleton skeleton-text" />
        <div className="skeleton skeleton-text short" />

        <div className="post-meta">
          <div className="skeleton skeleton-meta" />
          <div className="skeleton skeleton-meta" />
        </div>
      </div>
    </article>
  );
}