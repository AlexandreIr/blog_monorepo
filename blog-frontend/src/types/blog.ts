export interface Category {
  id: number;
  name: string;
  slug: string;
}

export interface PostSummary {
  id: number;
  title: string;
  slug: string;
  summary: string;
  coverImageUrl?: string;
  publishedAt?: string;
  authorName?: string;
  categories: Category[];
}

export interface PostDetail {
  id: number;
  title: string;
  slug: string;
  summary: string;
  content: string;
  coverImageUrl?: string;
  metaTitle?: string;
  metaDescription?: string;
  publishedAt?: string;
  authorName?: string;
  categories: Category[];
}

export interface Comment {
  id: number;
  authorName: string;
  authorEmail?: string;
  content: string;
  status?: string;
  postId?: number;
  createdAt?: string;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}