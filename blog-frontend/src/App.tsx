import { Suspense, lazy } from "react";
import { Route, Routes } from "react-router-dom";
import Home from "./pages/home/Home";

const PostDetailPage = lazy(() => import("./pages/postDetailsPage/PostDetailPage.tsx"));

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/categories/:slug" element={<Home />} />

      <Route
        path="/posts/:slug"
        element={
          <Suspense fallback={<p>Carregando artigo...</p>}>
            <PostDetailPage />
          </Suspense>
        }
      />
    </Routes>
  );
}