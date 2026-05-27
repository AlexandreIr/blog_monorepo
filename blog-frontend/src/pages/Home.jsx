import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../api/api";

export default function Home() {
  const [posts, setPosts] = useState([]);

  useEffect(() => {
    api.get("/posts")
      .then((res) => setPosts(res.data.content ?? []))
      .catch(console.error);
  }, []);

  return (
    <div>
      <h1>Libertad comercial e serviços</h1>
      <p>Conteúdos do blog</p>

      <div className="grid">
        {posts.map((post) => (
          <article className="card" key={post.id}>
            <h2>{post.title}</h2>
            <p>{post.summary}</p>
            <small>Autor: Libertad</small>
            <br />
            <Link to={`/posts/${post.slug}`}>Ler artigo</Link>
          </article>
        ))}
      </div>
    </div>
  );
}