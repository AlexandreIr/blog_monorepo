import { Link, Route, Routes } from "react-router-dom";
import Home from "./pages/Home";
import PostDetail from "./pages/PostDetail";
import Login from "./pages/Login";
import AdminPosts from "./pages/AdminPosts";
import AdminCategories from "./pages/AdminCategories";
import AdminComments from "./pages/AdminComments";
import NewPost from "./pages/NewPost";

export default function App() {
  const isLoggedIn = !!localStorage.getItem("token");

  return (
    <div>
      <nav className="navbar">
        <Link to="/">Blog</Link>

        {isLoggedIn && (
          <>
            <Link to="/painel-secreto/posts">Posts</Link>
            <Link to="/painel-secreto/posts/new">Novo Post</Link>
            <Link to="/painel-secreto/categories">Categorias</Link>
            <Link to="/painel-secreto/comments">Moderação</Link>
          </>
        )}
      </nav>

      <main className="container">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/posts/:slug" element={<PostDetail />} />

          <Route path="/painel-secreto/login" element={<Login />} />
          <Route path="/painel-secreto/posts" element={<AdminPosts />} />
          <Route path="/painel-secreto/posts/new" element={<NewPost />} />
          <Route path="/painel-secreto/categories" element={<AdminCategories />} />
          <Route path="/painel-secreto/comments" element={<AdminComments />} />
        </Routes>
      </main>
    </div>
  );
}