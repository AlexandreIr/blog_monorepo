import React, { useEffect, useState } from "react";
import { Link, useParams, useSearchParams } from "react-router-dom";
import { api } from "../../api/api";
import { Category, PagedResponse, PostSummary } from "../../types/blog";
import { Header } from "../../components/header/Header";
import { Hero } from "../../components/hero/Hero";
import { PostCard } from "../../components/PostCard";
import { CategoriesBox } from "../../components/CategoriesBox";
import { Footer } from "../../components/Footer";
import { PostCardSkeleton } from "../../components/postCardSkeleton/PostCardSkeleton";
// @ts-ignore
import "./home.css";

export default function Home() {
  const { slug: categorySlug } = useParams();
  const [searchParams, setSearchParams] = useSearchParams();

  const queryFromUrl = searchParams.get("query") ?? "";
  const pageFromUrl = Number(searchParams.get("page") ?? "0");
  const sizeFromUrl = Number(searchParams.get("size") ?? "6");

  const currentPage = Number.isNaN(pageFromUrl) ? 0 : pageFromUrl;
  const currentSize = Number.isNaN(sizeFromUrl) ? 6 : sizeFromUrl;

  const [posts, setPosts] = useState<PostSummary[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [search, setSearch] = useState(queryFromUrl);
  const [loadingPosts, setLoadingPosts] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");

  const [pagination, setPagination] = useState({
    page: 0,
    size: 6,
    totalElements: 0,
    totalPages: 0,
    first: true,
    last: true,
  });

  useEffect(() => {
    setSearch(queryFromUrl);
  }, [queryFromUrl]);

  useEffect(() => {
    async function loadPosts() {
      try {
        setLoadingPosts(true);
        setErrorMessage("");

        let endpoint = `/posts?page=${currentPage}&size=${currentSize}`;

        if (categorySlug) {
          endpoint = `/categories/${categorySlug}/posts?page=${currentPage}&size=${currentSize}`;
        }

        if (queryFromUrl.trim()) {
          endpoint = `/posts/search?query=${encodeURIComponent(
            queryFromUrl.trim()
          )}&page=${currentPage}&size=${currentSize}`;
        }

        const response = await api.get<PagedResponse<PostSummary> | PostSummary[]>(endpoint);

        if (Array.isArray(response.data)) {
          setPosts(response.data);
          setPagination({
            page: 0,
            size: response.data.length,
            totalElements: response.data.length,
            totalPages: 1,
            first: true,
            last: true,
          });
          return;
        }

        setPosts(response.data.content ?? []);
        setPagination({
          page: response.data.page,
          size: response.data.size,
          totalElements: response.data.totalElements,
          totalPages: response.data.totalPages,
          first: response.data.first,
          last: response.data.last,
        });
      } catch (error) {
        console.error("Erro ao carregar posts:", error);
        setPosts([]);
        setErrorMessage("Não foi possível carregar os artigos. Verifique se a API está rodando.");
      } finally {
        setLoadingPosts(false);
      }
    }

    loadPosts();
  }, [categorySlug, queryFromUrl, currentPage, currentSize]);

  useEffect(() => {
    async function loadCategories() {
      try {
        const response = await api.get<PagedResponse<Category> | Category[]>("api/categories");

        if (Array.isArray(response.data)) {
          setCategories(response.data);
          return;
        }

        setCategories(response.data.content ?? []);
      } catch (error) {
        console.error("Erro ao carregar categorias:", error);
        setCategories([]);
      }
    }

    loadCategories();
  }, []);

  function handleSearchSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const normalizedSearch = search.trim();

    if (!normalizedSearch) {
      setSearchParams({
        page: "0",
        size: String(currentSize),
      });
      return;
    }

    setSearchParams({
      query: normalizedSearch,
      page: "0",
      size: String(currentSize),
    });
  }

  function goToPage(nextPage: number) {
    const params: Record<string, string> = {
      page: String(nextPage),
      size: String(currentSize),
    };

    if (queryFromUrl.trim()) {
      params.query = queryFromUrl.trim();
    }

    setSearchParams(params);
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  function changePageSize(event: React.ChangeEvent<HTMLSelectElement>) {
    const newSize = event.target.value;

    const params: Record<string, string> = {
      page: "0",
      size: newSize,
    };

    if (queryFromUrl.trim()) {
      params.query = queryFromUrl.trim();
    }

    setSearchParams(params);
  }

  const featuredPost = posts[0];
  const currentCategory = categories.find((category) => category.slug === categorySlug);

  return (
    <div>
      <Header
        search={search}
        onSearchChange={setSearch}
        onSearchSubmit={handleSearchSubmit}
      />

      <main className="page">
        <Hero post={featuredPost} />

        <section className="content-layout">
          <div className="main-content">
            <div className="section-header">
              <div>
                <h2>
                  {queryFromUrl
                    ? `Resultado para "${queryFromUrl}"`
                    : currentCategory
                      ? `Categoria: ${currentCategory.name}`
                      : "Últimos Posts"}
                </h2>
                <div className="underline" />
              </div>

              {(queryFromUrl || categorySlug) && (
                <Link className="view-all" to="/">
                  Limpar filtro
                </Link>
              )}
            </div>

            <div className="pagination-info-top">
              <span>
                {pagination.totalElements} artigo(s) encontrado(s)
              </span>

              <label>
                Exibir
                <select value={currentSize} onChange={changePageSize}>
                  <option value="3">3</option>
                  <option value="6">6</option>
                  <option value="9">9</option>
                  <option value="12">12</option>
                </select>
                por página
              </label>
            </div>

            {errorMessage && (
              <div className="error-box">
                <strong>Ops, algo deu errado.</strong>
                <p>{errorMessage}</p>

                <button onClick={() => window.location.reload()}>
                  Tentar novamente
                </button>
              </div>
            )}

            {loadingPosts && !errorMessage && (
              <div className="posts-grid">
                {Array.from({ length: currentSize }).map((_, index) => (
                  <PostCardSkeleton key={index} />
                ))}
              </div>
            )}

            {!loadingPosts && posts.length === 0 && (
              <p>Nenhum artigo publicado encontrado.</p>
            )}



            <div className="posts-grid">
              {posts.map((post) => (
                <PostCard key={post.id} post={post} />
              ))}
            </div>

            {!loadingPosts && posts.length > 0 && (
              <div className="pagination">
                <button
                  disabled={pagination.first}
                  onClick={() => goToPage(pagination.page - 1)}
                >

                  Anterior
                </button>

                <span>
                  Página <strong>{pagination.page + 1}</strong> de{" "}
                  <strong>{pagination.totalPages || 1}</strong>
                </span>

                <button
                  disabled={pagination.last}
                  onClick={() => goToPage(pagination.page + 1)}
                >
                  Próxima

                </button>
              </div>
            )}
          </div>

          <aside className="sidebar">
            <CategoriesBox categories={categories} />
          </aside>
        </section>


      </main>

      <Footer />
    </div>
  );
}