import { Suspense, lazy } from "react";
import { Route, Routes } from "react-router-dom";
import Home from "./pages/home/Home";
const PostDetailPage = lazy(() => import("./pages/postDetailsPage/PostDetailPage"));
const LoginPage = lazy(() => import("./pages/admin/LoginPage"));
const AdminLayout = lazy(() => import("./pages/admin/AdminLayout"));
const AdminDashboard = lazy(() => import("./pages/admin/AdminDashboard"));
const AdminPosts = lazy(() => import("./pages/admin/AdminPost"));
const AdminCategories = lazy(() => import("./pages/admin/AdminCategories"));
const AdminComments = lazy(() => import("./pages/admin/AdminComments"));
const AdminNewPost = lazy(() => import("./pages/admin/AdminNewPost"));
const AdminEditPost = lazy(() => import("./pages/admin/AdminEditPost"));

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

            <Route
                path="/painel-secreto/login"
                element={
                    <Suspense fallback={<p>Carregando login...</p>}>
                        <LoginPage />
                    </Suspense>
                }
            />

            <Route
                path="/painel-secreto"
                element={
                    <Suspense fallback={<p>Carregando painel...</p>}>
                        <AdminLayout />
                    </Suspense>
                }
            >
                <Route index element={<AdminDashboard />} />
                <Route path="posts" element={<AdminPosts />} />
                <Route path="posts/new" element={<AdminNewPost />} />
                <Route path="posts/:id/edit" element={<AdminEditPost />} />
                <Route path="categories" element={<AdminCategories />} />
                <Route path="comments" element={<AdminComments />} />
            </Route>
        </Routes>
    );
}